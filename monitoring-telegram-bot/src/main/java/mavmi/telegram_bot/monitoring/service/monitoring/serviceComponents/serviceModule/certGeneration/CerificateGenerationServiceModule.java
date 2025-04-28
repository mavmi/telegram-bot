package mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.certGeneration;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.database_starter.api.PRIVILEGE;
import mavmi.telegram_bot.lib.database_starter.model.CertificateModel;
import mavmi.telegram_bot.lib.database_starter.repository.CertificateRepository;
import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.aop.privilege.api.VerifyPrivilege;
import mavmi.telegram_bot.monitoring.certs.CertificatesManagementService;
import mavmi.telegram_bot.monitoring.mapper.CryptoMapper;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.common.CommonServiceModule;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS12PfxPduBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEOutputEncryptorBuilder;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class CerificateGenerationServiceModule implements ServiceModule<MonitoringServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<MonitoringServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public CerificateGenerationServiceModule(CommonServiceModule commonServiceModule) {
        this.commonServiceModule = commonServiceModule;
        this.serviceComponentsContainer.setDefaultServiceMethod(this::onDefault);
    }

    @Override
    @VerifyPrivilege(PRIVILEGE.CERT_GENERATION)
    public void handleRequest(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<MonitoringServiceRq> method = serviceComponentsContainer.getMethod(msg);
        method.process(request);
    }

    @SneakyThrows
    private void onDefault(MonitoringServiceRq request) {
        long chatId = request.getChatId();
        CryptoMapper cryptoMapper = commonServiceModule.getCryptoMapper();
        TextEncryptor textEncryptor = commonServiceModule.getTextEncryptor();
        CertificateRepository repository = commonServiceModule.getCertificateRepository();
        CertificatesManagementService service = commonServiceModule.getCertificatesManagementService();

        CertificateModel model;
        Optional<CertificateModel> optional = repository.findById(chatId);
        if (optional.isEmpty() || optional.get().getExpiryDate().compareTo(Date.from(Instant.now())) <= 0) {
            CertificatesManagementService.CertificateKeyPair pair = service.generateCertificate(String.valueOf(chatId));

            String cert = Base64.getEncoder().encodeToString(pair.getCertificate().getEncoded());
            String key = Base64.getEncoder().encodeToString(pair.getKey().getEncoded());
            Date expiryDate = pair.getCertificate().getNotAfter();

            model = CertificateModel.builder()
                    .userid(chatId)
                    .certificate(cert)
                    .key(key)
                    .expiryDate(new Timestamp(expiryDate.getTime()))
                    .build();

            repository.save(cryptoMapper.encryptCertificateModel(textEncryptor, model));
        } else {
            model = cryptoMapper.decryptCertificateModel(textEncryptor, optional.get());
        }

        String pass = generateRandomPassword();
        File certFile = createCertFile(chatId, Base64.getDecoder().decode(model.getCertificate()), Base64.getDecoder().decode(model.getKey()), pass.toCharArray());
        if (certFile != null) {
            commonServiceModule.sendText(chatId, pass);
            commonServiceModule.sendFile(chatId, certFile);
            commonServiceModule.sendCurrentMenuButtons(chatId);
            certFile.delete();
        } else {
            commonServiceModule.sendCurrentMenuButtons(chatId, commonServiceModule.getConstants().getPhrases().getCommon().getError());
        }
    }

    @Nullable
    @SneakyThrows
    private File createCertFile(long chatId, byte[] certData, byte[] privKeyData, char[] p12Pass) {
        File certFile = new File(commonServiceModule.getCertificatesOutputDirectory() + "/" + chatId + ".p12");

        OutputEncryptor encryptor = new JcePKCSPBEOutputEncryptorBuilder(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC)
                .setIterationCount(2048)
                .build(p12Pass);

        X509Certificate certificate = CertificatesManagementService.getCertificate(certData);
        PrivateKey privateKey = CertificatesManagementService.getPrivateKey(privKeyData);

        SubjectKeyIdentifier keyIdentifier = new JcaX509ExtensionUtils().createSubjectKeyIdentifier(certificate.getPublicKey());

        JcaPKCS12SafeBagBuilder certBuilder = new JcaPKCS12SafeBagBuilder(certificate);
        certBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, keyIdentifier);

        JcaPKCS12SafeBagBuilder keyBuilder = new JcaPKCS12SafeBagBuilder(privateKey, encryptor);
        keyBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, keyIdentifier);

        PKCS12PfxPduBuilder pfxPduBuilder = new PKCS12PfxPduBuilder()
                .addEncryptedData(encryptor, certBuilder.build())
                .addEncryptedData(encryptor, keyBuilder.build());

        JcePKCS12MacCalculatorBuilder jcePKCS12MacCalculatorBuilder = new JcePKCS12MacCalculatorBuilder()
                .setProvider(new BouncyCastleProvider())
                .setIterationCount(1000);

        try (FileOutputStream outputStream = new FileOutputStream(certFile)) {
            outputStream.write(pfxPduBuilder.build(jcePKCS12MacCalculatorBuilder, p12Pass).getEncoded());
            return certFile;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

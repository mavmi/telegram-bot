package mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.certGeneration;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.model.CertificateModel;
import mavmi.telegram_bot.common.database.repository.CertificateRepository;
import mavmi.telegram_bot.common.privileges.api.PRIVILEGE;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.aop.privilege.api.VerifyPrivilege;
import mavmi.telegram_bot.monitoring.certs.CertificatesManagementService;
import mavmi.telegram_bot.monitoring.mapper.CryptoMapper;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.common.CommonServiceModule;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

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

        File certFile = createCertFile(chatId, Base64.getDecoder().decode(model.getCertificate()));
        if (certFile != null) {
            commonServiceModule.sendFile(chatId, certFile);
            commonServiceModule.sendCurrentMenuButtons(chatId);
            certFile.delete();
        } else {
            commonServiceModule.sendCurrentMenuButtons(chatId, commonServiceModule.getConstants().getPhrases().getCommon().getError());
        }
    }

    @Nullable
    private File createCertFile(long chatId, byte[] certData) {
        File certFile = new File(commonServiceModule.getCertificatesOutputDirectory() + "/" + chatId + ".crt");

        try (FileOutputStream outputStream = new FileOutputStream(certFile)) {
            outputStream.write(certData);
            return certFile;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}

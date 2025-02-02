package mavmi.telegram_bot.monitoring.certs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

@Slf4j
@Component
public class CertificatesManagementService {

    private final X509Certificate caCertificate;
    private final PrivateKey caPrivateKey;
    private final String issuerPrefix;
    private final String issuerCaCommonName;

    public CertificatesManagementService(
            @Value("${certificates.ca.cert-path}") String caCertPath,
            @Value("${certificates.ca.key-path}") String caKeyPath,
            @Value("${certificates.issuer.prefix}") String issuerPrefix,
            @Value("${certificates.issuer.ca-common-name}") String issuerCaCommonName
    ) {
        this.caCertificate = uploadCaCertificate(caCertPath);
        this.caPrivateKey = uploadCaPrivateKey(caKeyPath);
        this.issuerPrefix = issuerPrefix;
        this.issuerCaCommonName = issuerCaCommonName;
    }

    @SneakyThrows
    public CertificateKeyPair generateCertificate(String commonName) {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(
                new X500Name(issuerPrefix + issuerCaCommonName),
                BigInteger.valueOf(System.currentTimeMillis() / 1000),
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + 365L * 24L * 60L * 60L * 1000L),
                new X500Name(issuerPrefix + commonName),
                SubjectPublicKeyInfo.getInstance(publicKey.getEncoded())
        ).addExtension(Extension.keyUsage, false, new KeyUsage(KeyUsage.digitalSignature));
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(caPrivateKey);
        X509Certificate certificate = new JcaX509CertificateConverter()
                .setProvider(new BouncyCastleProvider())
                .getCertificate(certificateBuilder.build(signer));

        return new CertificateKeyPair(certificate, privateKey);
    }

    @SneakyThrows
    public static X509Certificate getCertificate(byte[] data) {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(data));
    }

    @SneakyThrows
    public static PrivateKey getPrivateKey(byte[] data) {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(data);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    @SneakyThrows
    private X509Certificate uploadCaCertificate(String caCertificatePath) {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
        try (FileInputStream fileInputStream = new FileInputStream(caCertificatePath)) {
            return (X509Certificate) certificateFactory.generateCertificate(fileInputStream);
        }
    }

    @SneakyThrows
    private PrivateKey uploadCaPrivateKey(String caPrivateKeyPath) {
        PEMParser pemParser = new PEMParser(new FileReader(caPrivateKeyPath));
        Object object = pemParser.readObject();

        if (object instanceof PEMKeyPair) {
            PEMKeyPair pemKeyPair = (PEMKeyPair) object;
            JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();

            return jcaPEMKeyConverter.getKeyPair(pemKeyPair).getPrivate();
        } else if (object instanceof PrivateKeyInfo) {
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) object;
            JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();

            return jcaPEMKeyConverter.getPrivateKey(privateKeyInfo);
        } else {
            throw new RuntimeException("CA private key not found");
        }
    }

    @Getter
    @AllArgsConstructor
    public static class CertificateKeyPair {
        private X509Certificate certificate;
        private PrivateKey key;
    }
}

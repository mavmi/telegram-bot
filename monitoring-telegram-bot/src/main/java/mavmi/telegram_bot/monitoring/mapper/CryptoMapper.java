package mavmi.telegram_bot.monitoring.mapper;

import mavmi.telegram_bot.common.database.model.CertificateModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Mapper(componentModel = "spring")
public interface CryptoMapper {

    @Mapping(target = "userid", expression = "java(model.getUserid())")
    @Mapping(target = "certificate", expression = "java(CryptoMapper.encrypt(textEncryptor, model.getCertificate()))")
    @Mapping(target = "key", expression = "java(CryptoMapper.encrypt(textEncryptor, model.getKey()))")
    @Mapping(target = "expiryDate", expression = "java(model.getExpiryDate())")
    CertificateModel encryptCertificateModel(TextEncryptor textEncryptor, CertificateModel model);

    @Mapping(target = "userid", expression = "java(model.getUserid())")
    @Mapping(target = "certificate", expression = "java(CryptoMapper.decrypt(textEncryptor, model.getCertificate()))")
    @Mapping(target = "key", expression = "java(CryptoMapper.decrypt(textEncryptor, model.getKey()))")
    @Mapping(target = "expiryDate", expression = "java(model.getExpiryDate())")
    CertificateModel decryptCertificateModel(TextEncryptor textEncryptor, CertificateModel model);

    static String encrypt(TextEncryptor textEncryptor, String string) {
        if (string == null) {
            return null;
        } else {
            return textEncryptor.encrypt(string);
        }
    }

    static String decrypt(TextEncryptor textEncryptor, String string) {
        if (string == null) {
            return null;
        } else {
            return textEncryptor.decrypt(string);
        }
    }
}

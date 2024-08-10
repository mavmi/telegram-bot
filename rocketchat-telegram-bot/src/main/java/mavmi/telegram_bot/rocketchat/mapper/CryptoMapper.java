package mavmi.telegram_bot.rocketchat.mapper;

import mavmi.telegram_bot.common.database.model.RocketchatModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Mapper(componentModel = "spring")
public interface CryptoMapper {

    @Mapping(target = "id", expression = "java(model.getId())")
    @Mapping(target = "telegramId", expression = "java(model.getTelegramId())")
    @Mapping(target = "telegramUsername", expression = "java(CryptoMapper.encrypt(textEncryptor, model.getTelegramUsername()))")
    @Mapping(target = "telegramFirstname", expression = "java(CryptoMapper.encrypt(textEncryptor, model.getTelegramFirstname()))")
    @Mapping(target = "telegramLastname", expression = "java(CryptoMapper.encrypt(textEncryptor, model.getTelegramLastname()))")
    @Mapping(target = "rocketchatUsername", expression = "java(CryptoMapper.encrypt(textEncryptor, model.getRocketchatUsername()))")
    @Mapping(target = "rocketchatPasswordHash", expression = "java(CryptoMapper.encrypt(textEncryptor, model.getRocketchatPasswordHash()))")
    @Mapping(target = "rocketchatToken", expression = "java(CryptoMapper.encrypt(textEncryptor, model.getRocketchatToken()))")
    @Mapping(target = "rocketchatTokenExpiryDate", expression = "java(model.getRocketchatTokenExpiryDate())")
    RocketchatModel encryptRocketchatModel(TextEncryptor textEncryptor, RocketchatModel model);

    @Mapping(target = "id", expression = "java(model.getId())")
    @Mapping(target = "telegramId", expression = "java(model.getTelegramId())")
    @Mapping(target = "telegramUsername", expression = "java(CryptoMapper.decrypt(textEncryptor, model.getTelegramUsername()))")
    @Mapping(target = "telegramFirstname", expression = "java(CryptoMapper.decrypt(textEncryptor, model.getTelegramFirstname()))")
    @Mapping(target = "telegramLastname", expression = "java(CryptoMapper.decrypt(textEncryptor, model.getTelegramLastname()))")
    @Mapping(target = "rocketchatUsername", expression = "java(CryptoMapper.decrypt(textEncryptor, model.getRocketchatUsername()))")
    @Mapping(target = "rocketchatPasswordHash", expression = "java(CryptoMapper.decrypt(textEncryptor, model.getRocketchatPasswordHash()))")
    @Mapping(target = "rocketchatToken", expression = "java(CryptoMapper.decrypt(textEncryptor, model.getRocketchatToken()))")
    @Mapping(target = "rocketchatTokenExpiryDate", expression = "java(model.getRocketchatTokenExpiryDate())")
    RocketchatModel decryptRocketchatModel(TextEncryptor textEncryptor, RocketchatModel model);

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
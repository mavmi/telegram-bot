package mavmi.telegram_bot.rocketchat.mapper;

import mavmi.telegram_bot.rocketchat.service.database.dto.RocketchatDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 * Mapper between encrypted and decrypted versions of db dto
 */
@Mapper(componentModel = "spring")
public interface CryptoMapper {

    @Mapping(target = "telegramId", expression = "java(dto.getTelegramId())")
    @Mapping(target = "telegramUsername", expression = "java(CryptoMapper.encrypt(textEncryptor, dto.getTelegramUsername()))")
    @Mapping(target = "telegramFirstname", expression = "java(CryptoMapper.encrypt(textEncryptor, dto.getTelegramFirstname()))")
    @Mapping(target = "telegramLastname", expression = "java(CryptoMapper.encrypt(textEncryptor, dto.getTelegramLastname()))")
    @Mapping(target = "rocketchatUsername", expression = "java(CryptoMapper.encrypt(textEncryptor, dto.getRocketchatUsername()))")
    @Mapping(target = "rocketchatPasswordHash", expression = "java(CryptoMapper.encrypt(textEncryptor, dto.getRocketchatPasswordHash()))")
    @Mapping(target = "rocketchatToken", expression = "java(CryptoMapper.encrypt(textEncryptor, dto.getRocketchatToken()))")
    @Mapping(target = "rocketchatTokenExpiryDate", expression = "java(dto.getRocketchatTokenExpiryDate())")
    @Mapping(target = "lastQrMsgId", expression = "java(dto.getLastQrMsgId())")
    RocketchatDto encryptRocketchatDto(TextEncryptor textEncryptor, RocketchatDto dto);

    @Mapping(target = "telegramId", expression = "java(dto.getTelegramId())")
    @Mapping(target = "telegramUsername", expression = "java(CryptoMapper.decrypt(textEncryptor, dto.getTelegramUsername()))")
    @Mapping(target = "telegramFirstname", expression = "java(CryptoMapper.decrypt(textEncryptor, dto.getTelegramFirstname()))")
    @Mapping(target = "telegramLastname", expression = "java(CryptoMapper.decrypt(textEncryptor, dto.getTelegramLastname()))")
    @Mapping(target = "rocketchatUsername", expression = "java(CryptoMapper.decrypt(textEncryptor, dto.getRocketchatUsername()))")
    @Mapping(target = "rocketchatPasswordHash", expression = "java(CryptoMapper.decrypt(textEncryptor, dto.getRocketchatPasswordHash()))")
    @Mapping(target = "rocketchatToken", expression = "java(CryptoMapper.decrypt(textEncryptor, dto.getRocketchatToken()))")
    @Mapping(target = "rocketchatTokenExpiryDate", expression = "java(dto.getRocketchatTokenExpiryDate())")
    @Mapping(target = "lastQrMsgId", expression = "java(dto.getLastQrMsgId())")
    RocketchatDto decryptRocketchatDto(TextEncryptor textEncryptor, RocketchatDto dto);

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

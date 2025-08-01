package mavmi.telegram_bot.shakal.service.database;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.database_starter.repository.RequestRepository;
import mavmi.telegram_bot.lib.database_starter.repository.UserRepository;
import mavmi.telegram_bot.shakal.service.database.dto.ShakalRequestDto;
import mavmi.telegram_bot.shakal.service.database.dto.ShakalUserDto;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShakalDatabseService {

    private final ShakalMapper mapper;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    public void save(ShakalUserDto dto) {
        userRepository.save(mapper.userDtoToModel(dto));
    }

    public void save(ShakalRequestDto dto) {
        requestRepository.save(mapper.requestDtoToModel(dto));
    }
}

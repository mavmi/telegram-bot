package mavmi.telegram_bot.congrats.congrats_bot.telegram_bot;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.congrats.utils.database.model.MessageModel;
import mavmi.telegram_bot.congrats.utils.database.repository.MessageRepository;
import mavmi.telegram_bot.congrats.utils.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MessageSenderThread extends Thread {
    private final Long sleepTimeFrom;
    private final Long sleepTimeTo;
    private final Bot bot;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public MessageSenderThread(
            Bot bot,
            UserRepository userRepository,
            MessageRepository messageRepository,
            @Value("${bot.sleep-time.from}") Long sleepTimeFrom,
            @Value("${bot.sleep-time.to}") Long sleepTimeTo
    ) {
        this.bot = bot;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.sleepTimeFrom = sleepTimeFrom;
        this.sleepTimeTo = sleepTimeTo;
    }

    @PostConstruct
    public void init() {
        log.info("Message thread start");
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            List<MessageModel> messageModelList = messageRepository.getAll();
            for (long id : bot.getAllIdx()) {
                log.info("Sent message to id {}", id);
                bot.sendMessage(id, getRandomMsg(messageModelList));
            }

            try {
                long randTime = randSleepTime();
                log.info("From: {}", sleepTimeFrom);
                log.info("To: {}", sleepTimeTo);
                log.info("Going sleep for {} ms", randTime);
                sleep(randTime);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    private String getRandomMsg(List<MessageModel> messageModelList) {
        int pos = (int) (Math.random() * messageModelList.size());
        return messageModelList.get(pos).getMessage();
    }

    private long randSleepTime() {
        return (long) (Math.random() * (sleepTimeTo - sleepTimeFrom)) + sleepTimeFrom;
    }
}

package mavmi.telegram_bot.congrats.congrats_bot.telegram_bot;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.congrats.utils.database.model.MessageModel;
import mavmi.telegram_bot.congrats.utils.database.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MessageSenderThread extends Thread {
    private final Long sleepTimeFrom;
    private final Long sleepTimeTo;
    private final String filesVolPath;
    private final Bot bot;
    private final MessageRepository messageRepository;

    public MessageSenderThread(
            Bot bot,
            MessageRepository messageRepository,
            @Value("${bot.files-vol}") String filesVolPath,
            @Value("${bot.sleep-time.from}") Long sleepTimeFrom,
            @Value("${bot.sleep-time.to}") Long sleepTimeTo
    ) {
        this.bot = bot;
        this.messageRepository = messageRepository;
        this.filesVolPath = filesVolPath;
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
            List<ContentNode> contentNodeList = uploadContent();

            if (!contentNodeList.isEmpty()) {
                for (long id : bot.getAllIdx()) {
                    ContentNode contentNode = contentNodeList.get(randInt(0, contentNodeList.size()));
                    log.info("New rand content type {} for id {}", contentNode.contentType.name(), id);

                    if (contentNode.contentType == CONTENT_TYPE.TEXT) {
                        bot.sendMessage(id, contentNode.str);
                    } else {
                        byte[] file = uploadFile(contentNode.str);

                        if (file == null) {
                            log.error("Cannot upload file {}", contentNode.str);
                        } else if (contentNode.contentType == CONTENT_TYPE.VOICE) {
                            bot.sendVoice(id, file);
                        } else {
                            bot.sendVideoNote(id, file);
                        }
                    }
                }
            } else {
                log.error("Content nodes list is empty");
            }

            try {
                long randTime = randLong(sleepTimeFrom, sleepTimeTo);
                log.info("From: {}; to: {}; going to sleep for {} ms", sleepTimeFrom, sleepTimeTo, randTime);
                sleep(randTime);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    private List<ContentNode> uploadContent() {
        List<ContentNode> list = new ArrayList<>();

        for (MessageModel messageModel : messageRepository.getAll()) {
            list.add(new ContentNode(messageModel.getMessage(), CONTENT_TYPE.TEXT));
        }

        File voicesDir = new File(filesVolPath + "/voice");
        if (voicesDir.exists() || voicesDir.isDirectory()) {
            File[] filesArr = voicesDir.listFiles();
            if (filesArr != null) {
                for (File file : filesArr) {
                    list.add(new ContentNode(file.getAbsolutePath(), CONTENT_TYPE.VOICE));
                }
            } else {
                log.error("Voices directory is null: {}", voicesDir.getAbsolutePath());
            }
        } else {
            log.error("Voices directory does not exist: {}", voicesDir.getAbsolutePath());
        }

        File videoNotesDir = new File(filesVolPath + "/videoNote");
        if (videoNotesDir.exists() || videoNotesDir.isDirectory()) {
            File[] filesArr = videoNotesDir.listFiles();
            if (filesArr != null) {
                for (File file : filesArr) {
                    list.add(new ContentNode(file.getAbsolutePath(), CONTENT_TYPE.VIDEO_NOTE));
                }
            } else {
                log.error("Video notes directory is null: {}", videoNotesDir.getAbsolutePath());
            }
        } else {
            log.error("Video notes directory does not exist: {}", videoNotesDir.getAbsolutePath());
        }

        return list;
    }

    private String randTextMsg(List<MessageModel> messageModelList) {
        int pos = randInt(0, messageModelList.size());
        return messageModelList.get(pos).getMessage();
    }

    @Nullable
    private String randVoiceMsg() {
        String dirPath = filesVolPath + "/voice";
        File dirFile = new File(dirPath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            log.error("Not a voices directory: {}", dirPath);
            return null;
        }

        File[] filesArr = dirFile.listFiles();
        if (filesArr == null) {
            log.error("List of files is null for voices directory: {}", dirPath);
            return null;
        } else if (filesArr.length == 0) {
            log.error("List of files is empty for voices directory: {}", dirPath);
            return null;
        }

        return (filesArr[randInt(0, filesArr.length)].getAbsolutePath());
    }

    @Nullable
    private String randVideoNote() {
        String dirPath = filesVolPath + "/videoNote";
        File dirFile = new File(dirPath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            log.error("Not a video notes directory: {}", dirPath);
            return null;
        }

        File[] filesArr = dirFile.listFiles();
        if (filesArr == null) {
            log.error("List of files is null for video notes directory: {}", dirPath);
            return null;
        } else if (filesArr.length == 0) {
            log.error("List of files is empty for video notes directory: {}", dirPath);
            return null;
        }

        return (filesArr[randInt(0, filesArr.length)].getAbsolutePath());
    }

    private CONTENT_TYPE randContentType() {
        return CONTENT_TYPE.values()[(int) (Math.random() * CONTENT_TYPE.values().length)];
    }

    @Nullable
    private byte[] uploadFile(String filePath) {
        int readCount = 0;
        int bufferSize = 4096;
        byte[] buffer = new byte[bufferSize];
        List<Byte> byteList = new ArrayList<>();

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filePath))) {
            while ((readCount = inputStream.read(buffer, 0, bufferSize)) != -1) {
                for (int i = 0; i < readCount; i++) {
                    byteList.add(buffer[i]);
                }
            }

            byte[] res = new byte[byteList.size()];
            for (int i = 0; i < byteList.size(); i++) {
                res[i] = byteList.get(i);
            }

            return res;
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    private int randInt(int min, int max) {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed((long) (Math.random() * 165185654));
        return secureRandom.nextInt(max - min) + min;
    }

    private long randLong(long min, long max) {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed((long) (Math.random() * 1981384731));
        return secureRandom.nextLong(max - min) + min;
    }

    private enum CONTENT_TYPE {
        TEXT,
        VOICE,
        VIDEO_NOTE
    };

    @AllArgsConstructor
    private static class ContentNode {
        String str;
        CONTENT_TYPE contentType;
    }
}

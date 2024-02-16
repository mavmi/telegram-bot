package mavmi.telegram_bot.async_task_manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.async_task_manager.config.Properties;
import mavmi.telegram_bot.common.dto.impl.task_manager.TaskManagerRs;
import mavmi.telegram_bot.common.taskManager.TASK_MANAGER_RQ_TYPE;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
@Component
@RequiredArgsConstructor
public class Service {

    private final Properties properties;

    @Nullable
    public TaskManagerRs execute(String taskName) {
        String shell = properties.getShell();
        String scriptPath = properties.getTaskToScript().get(taskName);
        TASK_MANAGER_RQ_TYPE taskManagerRqType = properties.getTaskToType().get(taskName);
        if (scriptPath == null || taskManagerRqType == null) {
            return null;
        }

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(shell, scriptPath);

        try {
            Process process = processBuilder.start();
            process.waitFor();

            String responseBody = getFirstLine(process.getInputStream());
            if (responseBody == null) {
                return null;
            }

            return getResponse(responseBody, taskManagerRqType);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    private TaskManagerRs getResponse(String responseBody, TASK_MANAGER_RQ_TYPE taskManagerRqType) {
        return TaskManagerRs
                .builder()
                .body(responseBody)
                .taskManagerRqType(taskManagerRqType)
                .build();
    }

    @Nullable
    private String getFirstLine(InputStream inputStream) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace(System.out);
            return null;
        }
    }
}

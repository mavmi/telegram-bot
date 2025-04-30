package mavmi.telegram_bot.monitoring.service.asyncTaskService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mavmi.telegram_bot.monitoring.service.asyncTaskService.exception.AsyncTaskServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to execute tasks from json file
 */
@Service
public class AsyncTaskService {

    private final File workingFile;

    private Map<String, List<ServiceTask>> targetToTaskList;

    public AsyncTaskService(@Value("${async-task-service.working-file}") String workingFilePath) {
        this.workingFile = new File(workingFilePath);
        this.targetToTaskList = new HashMap<>();

        readFromFile();
    }

    @Nullable
    public ServiceTask getNext(String target) {
        List<ServiceTask> serviceTaskList = targetToTaskList.get(target);

        if (serviceTaskList == null || serviceTaskList.isEmpty()) {
            return null;
        }

        ServiceTask serviceTask = serviceTaskList.remove(0);
        writeToFile();

        return serviceTask;
    }

    public void put(String target, ServiceTask serviceTask) {
        List<ServiceTask> serviceTaskList = targetToTaskList.get(target);

        if (serviceTaskList == null) {
            serviceTaskList = new ArrayList<>();
            targetToTaskList.put(target, serviceTaskList);
        }

        serviceTaskList.add(serviceTask);
        writeToFile();
    }

    private void readFromFile() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            targetToTaskList = objectMapper.readValue(workingFile, new TypeReference<Map<String, List<ServiceTask>>>() {});
        } catch (IOException e) {
            if (workingFile.exists()) {
                throw new RuntimeException(e);
            }
        }
    }

    private void writeToFile() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            objectMapper.writeValue(workingFile, targetToTaskList);
        } catch (IOException e) {
            throw new AsyncTaskServiceException(e);
        }
    }
}

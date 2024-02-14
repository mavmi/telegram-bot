package mavmi.telegram_bot.async_task_manager.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.async_task_manager.service.AsyncTaskService;
import mavmi.telegram_bot.async_task_manager.service.ServiceTask;
import mavmi.telegram_bot.common.dto.common.TaskManagerJson;
import mavmi.telegram_bot.common.dto.impl.task_manager.TaskManagerRq;
import mavmi.telegram_bot.common.dto.impl.task_manager.TaskManagerRs;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class Controller {

    private final AsyncTaskService asyncTaskService;

    public Controller(AsyncTaskService asyncTaskService) {
        this.asyncTaskService = asyncTaskService;
    }

    @PostMapping("/getNext")
    public ResponseEntity<TaskManagerRs> getNext(@RequestBody TaskManagerRq taskManagerRq) {
        log.info("/getNext request");

        TaskManagerJson taskManagerJson = taskManagerRq.getTaskManagerJson();
        String target = taskManagerJson.getTarget();
        ServiceTask serviceTask = asyncTaskService.getNext(target);

        if (serviceTask == null) {
            log.info("Target {} not found", target);
            return new ResponseEntity<TaskManagerRs>(HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return new ResponseEntity<TaskManagerRs>(
                    TaskManagerRs.builder().body(objectMapper.writeValueAsString(serviceTask)).build(),
                    HttpStatusCode.valueOf(HttpStatus.OK.value())
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace(System.out);
            return new ResponseEntity<TaskManagerRs>(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/put")
    public ResponseEntity<TaskManagerRs> put(@RequestBody TaskManagerRq taskManagerRq) {
        log.info("/put request");

        Long id = taskManagerRq.getChatId();
        TaskManagerJson taskManagerJson = taskManagerRq.getTaskManagerJson();

        ServiceTask serviceTask = ServiceTask
                .builder()
                .initiatorId(id)
                .target(taskManagerJson.getTarget())
                .message(taskManagerJson.getMessage())
                .build();

        asyncTaskService.put(serviceTask.getTarget(), serviceTask);

        return new ResponseEntity<TaskManagerRs>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }
}

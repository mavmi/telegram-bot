package mavmi.telegram_bot.async_task_manager.rest;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.async_task_manager.service.Service;
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

    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @PostMapping("/process")
    public ResponseEntity<TaskManagerRs> process(@RequestBody TaskManagerRq taskManagerRq) {
        log.info("/process request");

        TaskManagerJson taskManagerJson = taskManagerRq.getTaskManagerJson();
        String taskName = taskManagerJson.getMessage();

        TaskManagerRs taskManagerRs = service.execute(taskName);
        if (taskManagerRs != null) {
            return new ResponseEntity<TaskManagerRs>(
                    taskManagerRs,
                    HttpStatusCode.valueOf(HttpStatus.OK.value())
            );
        } else {
            return new ResponseEntity<TaskManagerRs>(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}

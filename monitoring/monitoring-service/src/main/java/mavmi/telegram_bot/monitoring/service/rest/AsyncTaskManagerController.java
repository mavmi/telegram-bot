package mavmi.telegram_bot.monitoring.service.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.common.AsyncTaskManagerJson;
import mavmi.telegram_bot.common.dto.impl.monitoring.async_tasc_service.AsyncTaskManagerRq;
import mavmi.telegram_bot.common.dto.impl.monitoring.async_tasc_service.AsyncTaskManagerRs;
import mavmi.telegram_bot.monitoring.service.asyncTaskService.AsyncTaskService;
import mavmi.telegram_bot.monitoring.service.asyncTaskService.ServiceTask;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AsyncTaskManagerController {

    private final AsyncTaskService asyncTaskService;

    @PostMapping("/getNext")
    public ResponseEntity<AsyncTaskManagerRs> getNext(@RequestBody AsyncTaskManagerRq asyncTaskManagerRq) {
        log.info("/getNext request");

        AsyncTaskManagerJson asyncTaskManagerJson = asyncTaskManagerRq.getAsyncTaskManagerJson();
        String target = asyncTaskManagerJson.getTarget();
        ServiceTask serviceTask = asyncTaskService.getNext(target);

        if (serviceTask == null) {
            log.info("Target {} not found", target);
            return new ResponseEntity<AsyncTaskManagerRs>(HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()));
        }

        return new ResponseEntity<AsyncTaskManagerRs>(
                AsyncTaskManagerRs
                        .builder()
                        .initiatorId(serviceTask.getInitiatorId())
                        .message(serviceTask.getMessage())
                        .build(),
                HttpStatusCode.valueOf(HttpStatus.OK.value())
        );
    }

    @PostMapping("/put")
    public ResponseEntity<AsyncTaskManagerRs> put(@RequestBody AsyncTaskManagerRq asyncTaskManagerRq) {
        log.info("/put request");

        Long id = asyncTaskManagerRq.getChatId();
        AsyncTaskManagerJson asyncTaskManagerJson = asyncTaskManagerRq.getAsyncTaskManagerJson();

        ServiceTask serviceTask = ServiceTask
                .builder()
                .initiatorId(id)
                .target(asyncTaskManagerJson.getTarget())
                .message(asyncTaskManagerJson.getMessage())
                .build();

        asyncTaskService.put(serviceTask.getTarget(), serviceTask);

        return new ResponseEntity<AsyncTaskManagerRs>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }
}

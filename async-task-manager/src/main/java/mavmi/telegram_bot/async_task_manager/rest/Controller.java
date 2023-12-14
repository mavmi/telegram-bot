package mavmi.telegram_bot.async_task_manager.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.async_task_manager.service.AsyncTaskService;
import mavmi.telegram_bot.async_task_manager.service.ServiceTask;
import mavmi.telegram_bot.common.utils.dto.json.service.ServiceRequestJson;
import mavmi.telegram_bot.common.utils.dto.json.service.inner.ServiceTaskManagerJson;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
public class Controller {

    private final AsyncTaskService asyncTaskService;

    public Controller(AsyncTaskService asyncTaskService) {
        this.asyncTaskService = asyncTaskService;
    }

    @PostMapping("/getNext")
    public ResponseEntity<String> getNext(@RequestBody String body) {
        body = decode(body);
        log.info("getNext request");

        ServiceTaskManagerJson serviceTaskManagerJson;
        ServiceRequestJson serviceRequestJson = getServiceRequestJson(body);

        if (serviceRequestJson == null ||
                (serviceTaskManagerJson = serviceRequestJson.getServiceTaskManagerJson()) == null) {
            log.error("Invalid request body: {}", body);
            return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }

        String target = serviceTaskManagerJson.getTarget();
        ServiceTask serviceTask = asyncTaskService.getNext(target);

        if (serviceTask == null) {
            log.info("Target {} not found", target);
            return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return new ResponseEntity<String>(
                    objectMapper.writeValueAsString(serviceTask),
                    HttpStatusCode.valueOf(HttpStatus.OK.value())
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace(System.out);
            return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/put")
    public ResponseEntity<Void> put(@RequestBody String body) {
        body = decode(body);
        log.info("put request: {}", body);

        Long id;
        ServiceTaskManagerJson serviceTaskManagerJson;
        ServiceRequestJson serviceRequestJson = getServiceRequestJson(body);

        if (serviceRequestJson == null ||
                (id = serviceRequestJson.getChatId()) == null ||
                (serviceTaskManagerJson = serviceRequestJson.getServiceTaskManagerJson()) == null) {
            log.error("Invalid request body: {}", body);
            return new ResponseEntity<Void>(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }

        ServiceTask serviceTask = ServiceTask
                .builder()
                .initiatorId(id)
                .target(serviceTaskManagerJson.getTarget())
                .message(serviceTaskManagerJson.getMessage())
                .build();

        asyncTaskService.put(serviceTask.getTarget(), serviceTask);

        return new ResponseEntity<Void>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }

    @Nullable
    private ServiceRequestJson getServiceRequestJson(String body) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(body, new TypeReference<ServiceRequestJson>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    private String decode(String str){
        return URLDecoder.decode(str, StandardCharsets.UTF_8);
    }
}

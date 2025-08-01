package mavmi.telegram_bot.lib.metric_starter.mteric.impl;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.service.ServiceRequest;
import mavmi.telegram_bot.lib.metric_starter.mteric.api.Metric;
import mavmi.telegram_bot.lib.metric_starter.service.database.MetricDatabaseService;
import mavmi.telegram_bot.lib.metric_starter.service.database.dto.MetricDto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.Date;

/**
 * AOP processor for {@link Metric}
 */
@Aspect
@Order(1)
@Component
@RequiredArgsConstructor
public class MetricProcessor {

    private final MetricDatabaseService databaseService;

    @Around("@annotation(metric)")
    public Object process(ProceedingJoinPoint joinPoint, Metric metric) throws Throwable {
        String botName = metric.value().name();
        ServiceRequest serviceRequest = (ServiceRequest) joinPoint.getArgs()[0];
        long chatId = serviceRequest.getChatId();
        boolean success = false;
        boolean error = false;
        Date date = new Date(System.currentTimeMillis());
        Object returnObject;

        boolean doUpdate;
        MetricDto dto = databaseService.find(botName, chatId, date);
        MetricDto metricDto;
        if (dto == null) {
            doUpdate = false;
            metricDto = MetricDto
                    .builder()
                    .botName(botName)
                    .telegramId(chatId)
                    .date(date)
                    .count(0)
                    .success(0)
                    .error(0)
                    .build();
        } else {
            doUpdate = true;
            metricDto = dto;
        }

        try {
            returnObject = joinPoint.proceed();
            success = true;
        } catch (Throwable throwable) {
            error = true;
            throw throwable;
        } finally {
            metricDto.setCount(metricDto.getCount() + 1);
            if (success) {
                metricDto.setSuccess(metricDto.getSuccess() + 1);
            }
            if (error) {
                metricDto.setError(metricDto.getError() + 1);
            }

            if (doUpdate) {
                databaseService.updateByTelegramIdAndDate(metricDto);
            } else {
                databaseService.save(metricDto);
            }
        }

        return returnObject;
    }
}

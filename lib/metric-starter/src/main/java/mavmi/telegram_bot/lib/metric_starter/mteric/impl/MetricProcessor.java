package mavmi.telegram_bot.lib.metric_starter.mteric.impl;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.database_starter.model.MetricModel;
import mavmi.telegram_bot.lib.database_starter.repository.MetricRepository;
import mavmi.telegram_bot.lib.dto.service.service.ServiceRequest;
import mavmi.telegram_bot.lib.metric_starter.mteric.api.Metric;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.Optional;

/**
 * AOP processor for {@link Metric}
 */
@Aspect
@Order(1)
@Component
@RequiredArgsConstructor
public class MetricProcessor {

    private final MetricRepository metricRepository;

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
        Optional<MetricModel> metricModelOptional = metricRepository.find(botName, chatId, date);
        MetricModel metricModel;
        if (metricModelOptional.isEmpty()) {
            doUpdate = false;
            metricModel = MetricModel
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
            metricModel = metricModelOptional.get();
        }

        try {
            returnObject = joinPoint.proceed();
            success = true;
        } catch (Throwable throwable) {
            error = true;
            throw throwable;
        } finally {
            metricModel.setCount(metricModel.getCount() + 1);
            if (success) {
                metricModel.setSuccess(metricModel.getSuccess() + 1);
            }
            if (error) {
                metricModel.setError(metricModel.getError() + 1);
            }

            if (doUpdate) {
                metricRepository.updateByTelegramIdAndDate(metricModel);
            } else {
                metricRepository.save(metricModel);
            }
        }

        return returnObject;
    }
}

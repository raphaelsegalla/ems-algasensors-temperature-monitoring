package com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.service.TemperatureMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

import static com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq.RabbitMQConfig.QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQListener {

    private final TemperatureMonitoringService temperatureMonitoringService;

    @RabbitListener(queues = QUEUE, concurrency = "2-3")
    @SneakyThrows
    public void handle(@Payload TemperatureLogData temperatureLogData, @Headers Map<String, Object> headers) {
        log.info("Temperature updated: SensorId {} Temp {}", temperatureLogData.getSensorId(), temperatureLogData.getValue());
        log.info("Headers: {}", headers);

        temperatureMonitoringService.processTemperatureReading(temperatureLogData);

        Thread.sleep(Duration.ofSeconds(5).toMillis()); // Simulating a delay for processing
    }

}

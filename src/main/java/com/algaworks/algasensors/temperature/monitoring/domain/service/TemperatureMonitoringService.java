package com.algaworks.algasensors.temperature.monitoring.domain.service;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.model.TemperatureLog;
import com.algaworks.algasensors.temperature.monitoring.domain.model.TemperatureLogId;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorMonitoringRepository;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.TemperatureLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemperatureMonitoringService {

    private final TemperatureLogRepository temperatureLogRepository;
    private final SensorMonitoringRepository sensorMonitoringRepository;

    @Transactional
    public void processTemperatureReading(TemperatureLogData temperatureLogData) {
        log.info("processTemperatureReading");
//        if (temperatureLogData.getValue().equals(10.5)) throw new RuntimeException("Test error");

        sensorMonitoringRepository.findById(new SensorId(temperatureLogData.getSensorId()))
                .ifPresentOrElse(sensor -> {
                    if (sensor.isEnabled()) {
                        sensor.setLastTemperature(temperatureLogData.getValue());
                        sensor.setUpdatedAt(OffsetDateTime.now());
                        sensorMonitoringRepository.save(sensor);

                        TemperatureLog temperatureLog = TemperatureLog.builder()
                                .id(new TemperatureLogId(temperatureLogData.getId()))
                                .sensorId(sensor.getId())
                                .value(temperatureLogData.getValue())
                                .registeredAt(temperatureLogData.getRegisteredAt())
                                .build();
                        temperatureLogRepository.save(temperatureLog);
                        log.info("Temperature updated: SensorId {} Temp {}", temperatureLogData.getSensorId(), temperatureLogData.getValue());
                    } else {
                        log.info("Temperature ignored: SensorId {} Temp {}, sensor disabled", temperatureLogData.getSensorId(), temperatureLogData.getValue());
                    }
                }, () -> log.info("Temperature ignored: SensorId {} Temp {}, not found", temperatureLogData.getSensorId(), temperatureLogData.getValue()));

    }
}

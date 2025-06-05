package com.algaworks.algasensors.temperature.monitoring.domain.service;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorAlertRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorAlertService {

    private final SensorAlertRepository sensorAlertRepository;

    @Transactional
    public void handleAlert(TemperatureLogData temperatureLogData) {
        sensorAlertRepository.findById(new SensorId(temperatureLogData.getSensorId()))
                .ifPresentOrElse(sensorAlert -> {
                    if (sensorAlert.getMaxTemperature() != null && temperatureLogData.getValue().compareTo(sensorAlert.getMaxTemperature()) >= 0) {
                        log.info("Alerting Max Temp: SensorId {} Temp {}", temperatureLogData.getSensorId(), temperatureLogData.getValue());
                    } else if (sensorAlert.getMinTemperature() != null && temperatureLogData.getValue().compareTo(sensorAlert.getMinTemperature()) <= 0) {
                        log.info("Alerting Min Temp: SensorId {} Temp {}", temperatureLogData.getSensorId(), temperatureLogData.getValue());
                    } else {
                        log.info("Alerting Ignored: SensorId {} Temp {}", temperatureLogData.getSensorId(), temperatureLogData.getValue());
                    }
                }, () -> log.info("Alerting Ignored: SensorId {} Temp {}, not found", temperatureLogData.getSensorId(), temperatureLogData.getValue()));
    }

}

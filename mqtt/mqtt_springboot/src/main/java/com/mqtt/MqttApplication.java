package com.mqtt;


import com.mqtt.config.MyMqtt;
import com.mqtt.controller.MqttClient;
import com.mqtt.controller.MqttDataController;
import com.mqtt.model.MqttDataModel;
import com.mqtt.repository.MqttDataRepository;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories("com.mqtt.repository")
@EnableScheduling
//@EntityScan("com.mqtt.model")
public class MqttApplication implements CommandLineRunner {
    @Autowired
    private MqttDataRepository repository;

    public static void main(String[] args) throws MqttException, InterruptedException {
        SpringApplication.run(MqttApplication.class, args);

    }

    @Override
    public void run(String... args) {

    }

    @Scheduled(fixedRate = 2000)
    public void scheduledTask() throws Exception {
        MqttClient mqttClient = new MqttClient();
        MqttDataController mqttDataController = new MqttDataController(mqttClient);

        List<MqttDataModel> dataModels = mqttDataController.subscribe_edison();
        repository.saveAll(dataModels);
    }
}

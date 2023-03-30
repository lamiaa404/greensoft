package com.mqtt.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.mqtt.model.MqttDataModel;
import com.mqtt.repository.MqttDataRepository;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Controller
public class MqttDataController {

    public MqttDataController(MqttClient connectedClient) {
        this.connectedClient = connectedClient;
    }

    @Autowired
    private MqttClient connectedClient;

    @Autowired
    private MqttDataRepository repository;

    public List<MqttDataModel> subscribe_edison() throws InterruptedException, MqttException {
        IMqttClient client = connectedClient.getClient();
        CountDownLatch countDownLatch = new CountDownLatch(1); //multithreading
        List<MqttDataModel> energyData = new ArrayList<>();
        String topic = "edison/#";
        long start_t = System.currentTimeMillis();

        while (System.currentTimeMillis() < start_t + (1000)) {
            client.subscribeWithResponse(topic, (concrete_topic, mqttMessage) -> {
                MqttDataModel mqttDataModel = new MqttDataModel(mqttMessage, concrete_topic);
                energyData.add(mqttDataModel);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await(1000, TimeUnit.MILLISECONDS);
        client.unsubscribe(topic);
        return energyData;
    }
}

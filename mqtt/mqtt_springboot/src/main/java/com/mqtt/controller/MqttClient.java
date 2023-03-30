package com.mqtt.controller;

import com.mqtt.config.MyMqtt;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.stereotype.Component;

@Component
public class MqttClient {
    private IMqttClient client = MyMqtt.getInstance();

    public IMqttClient getClient(){
        return client;
    }
}


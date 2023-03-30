package com.mqtt;

import com.mqtt.controller.MqttClient;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MqttClientTest {

    @Autowired
    MqttClient testClient;

    @Test
    void testMqttServerConnection() {
        IMqttClient client = testClient.getClient();
        assertEquals(client.isConnected(), true);
    }
}


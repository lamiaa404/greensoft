package com.mqtt.config;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;

public class MyMqtt {
    public static final String userName = "swtp22";
    private static final char[] password = "0jUkzJgxqrse5q".toCharArray();
    private static final String host = "tcp://pcai042.informatik.uni-leipzig.de:1883";
    private static final String clientId = UUID.randomUUID().toString();

    private static MemoryPersistence persistence = new MemoryPersistence();

    private static IMqttClient instance;

    private MyMqtt(){ }

    public static IMqttClient getInstance(){
        try{
            if(instance == null) {instance = new MqttClient(host, clientId,persistence);}

            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(userName);
            options.setPassword(password);
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(10);

            if(!instance.isConnected()){
                instance.connect(options);
            }
        } catch (MqttException e) {e.printStackTrace();}

        return instance;

    }

}

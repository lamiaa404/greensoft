package com.mqtt.model;

import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

@Entity
@Getter
@Setter
@IdClass(TimestampId.class)
@Table(name="mqtt_data")
@AllArgsConstructor
public class MqttDataModel {

    @Id
    @Column(name = "id")
    public Integer id;


    @Timestamp
    @Id
    @Column(name = "timestamp")
    public Integer timestamp;

    @Column(name = "energy_value")
    public double energy_value;

    public MqttDataModel() {
    }

    // für unit test
    public double getEnergy_value() {
        return energy_value;
    }

    public MqttDataModel(MqttMessage mqttMessage, String topic){
        StringBuilder stringBuilder = new StringBuilder(mqttMessage.toString());
        StringBuilder topicStringBuilder = new StringBuilder(topic);
        this.id = Integer.parseInt(topicStringBuilder.substring(topicStringBuilder.indexOf("/")+1,topicStringBuilder.indexOf("/")+3));
        this.timestamp = (int)Math.floor(Double.parseDouble(stringBuilder.substring(stringBuilder.indexOf("\"t\":")+5,stringBuilder.indexOf(","))));
        this.energy_value = Math.round(1000*Double.parseDouble(stringBuilder.substring(stringBuilder.indexOf("\"val\":")+7,stringBuilder.lastIndexOf("}"))))/1000.0;
    }

}

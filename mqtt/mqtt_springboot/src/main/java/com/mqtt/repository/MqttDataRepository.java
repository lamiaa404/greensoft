package com.mqtt.repository;

import com.mqtt.model.MqttDataModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MqttDataRepository extends JpaRepository<MqttDataModel,Integer> {

    // für unit test
    @Query(value = "SELECT * FROM mqtt_data WHERE id = :id AND timestamp = :timestamp", nativeQuery = true)
    MqttDataModel searchByTimestampAndId(@Param("id") int id, @Param("timestamp") int timestamp);

}

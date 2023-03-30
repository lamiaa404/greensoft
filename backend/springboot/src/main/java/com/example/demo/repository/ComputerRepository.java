package com.example.demo.repository;

import com.example.demo.entity.MqttDataModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;


@Repository
public interface ComputerRepository extends JpaRepository<MqttDataModel, Integer> {
    @Query(value = "SELECT * FROM mqtt_data WHERE :id = id AND timestamp BETWEEN :start AND :end AND (CAST(timestamp AS integer) % :precision) = 0 ORDER BY timestamp ASC", nativeQuery = true)
    List<MqttDataModel> returnRawDataForOneDevice(@Param("start") int start, @Param("end") int end, @Param("precision") int precision, @Param("id") int id );

    @Query(value = "SELECT * FROM mqtt_data WHERE timestamp >= :start AND :device = id ORDER BY timestamp ASC", nativeQuery = true)
    LinkedList<MqttDataModel> returnRawDataForSingleDevice(@Param("start") int start, @Param("device") Integer id);

    @Query(value = "SELECT id, MAX(energy_value) FROM mqtt_data WHERE timestamp > :yesterday GROUP BY id ORDER BY id ASC", nativeQuery = true)
    List<Object[]> findDailyMax(@Param("yesterday") int yesterday);

    @Query(value = "SELECT  id, ROUND(CAST(AVG(energy_value)AS numeric),2) FROM mqtt_data WHERE timestamp > :yesterday GROUP BY id ORDER BY id ASC", nativeQuery = true)
    List<Object[]> findDailyMean(@Param("yesterday") int yesterday);

    @Query(value = "SELECT  id, MIN(energy_value) FROM mqtt_data WHERE energy_value!=0 AND timestamp > :yesterday GROUP BY id ORDER BY id ASC", nativeQuery = true)
    List<Object[]> findIdle(@Param("yesterday") int yesterday);

    // für unit test
    @Query(value = "SELECT * FROM mqtt_data WHERE id = :id AND timestamp = :timestamp", nativeQuery = true)
    MqttDataModel searchByTimestampAndId(@Param("id") int id, @Param("timestamp") int timestamp);

}

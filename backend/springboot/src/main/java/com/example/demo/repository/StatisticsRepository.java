package com.example.demo.repository;

import com.example.demo.entity.MqttDataModel;
import com.example.demo.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
@Repository
public interface StatisticsRepository  extends JpaRepository<Statistics, Integer> {

    @Query(value = "SELECT id, timestamp, max FROM statistics WHERE timestamp BETWEEN :start AND :end ORDER BY id ASC,timestamp ASC", nativeQuery = true)
    List<Object[]> returnMax(@Param("start") int start, @Param("end") int end);

    @Query(value = "SELECT id, timestamp, average FROM statistics WHERE timestamp BETWEEN :start AND :end ORDER BY id ASC,timestamp ASC", nativeQuery = true)
    List<Object[]> returnMean(@Param("start") int start, @Param("end") int end);


    @Query(value = "SELECT id, timestamp, idle FROM statistics WHERE timestamp BETWEEN :start AND :end ORDER BY id ASC,timestamp ASC", nativeQuery = true)
    List<Object[]> returnIdle(@Param("start") int start, @Param("end") int end);

    @Query(value = "SELECT MAX(max) FROM statistics WHERE id = :device", nativeQuery = true)
    double returnHistoricalMax(@Param("device") Integer device);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM statistics WHERE id = :device AND timestamp  =:timestamp", nativeQuery = true)
    void deleteByIdAndTimestamp(@Param("device") Integer device,@Param("timestamp") Integer timestamp);


}

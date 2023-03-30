package com.example.demo.repository;

import com.example.demo.entity.DeviceInfo;
import com.example.demo.entity.MqttDataModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

@Repository
public interface DeviceInfoRepository extends JpaRepository<DeviceInfo, Integer> {
    @Query(value = "SELECT id FROM deviceinfo", nativeQuery = true)
    LinkedList<Integer> returnDeviceID();

    @Query(value = "SELECT * FROM deviceinfo ORDER BY id ASC", nativeQuery = true)
    List<DeviceInfo> returnDevices();

    @Query(value = "SELECT * FROM deviceinfo WHERE id = :id", nativeQuery = true)
    DeviceInfo returnOneDevice(@Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE deviceinfo SET name = :name, standort = :standort, users = :users, tags = :tags WHERE id = :id", nativeQuery = true)
    int updateDevice(@Param("id") int id, @Param("name")String name, @Param("standort") String standort, @Param("users") int
                                   users, @Param("tags") String tags);


}

package com.example.demo.repository;

import com.example.demo.entity.LastSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface LastSequenceRepository extends JpaRepository<LastSequence, Integer> {

    @Query(value = "SELECT MAX(id) FROM lastsequence", nativeQuery = true)
    Integer findLastId();

    @Query(value = "SELECT * FROM lastsequence WHERE :id = deviceId AND start BETWEEN :start AND :end ", nativeQuery = true)
    List<LastSequence> returnAllSequencesOfDevice(@Param("id") int id,@Param("start") int start, @Param("end") int end);


}

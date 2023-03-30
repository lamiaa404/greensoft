package com.example.demo.entity;


import jdk.jfr.Timestamp;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(TimestampId.class)
@Builder
@Entity
@Table(name = "statistics")
public class Statistics {

    @Id
    @Column(name = "id")
    private Integer id;

    @Timestamp
    @Id
    @Column(name = "timestamp")
    private Integer timestamp;

    @Column(name = "idle")
    private double idle;

    @Column(name = "max")
    private double max;

    @Column(name = "average")
    private BigDecimal average;

}

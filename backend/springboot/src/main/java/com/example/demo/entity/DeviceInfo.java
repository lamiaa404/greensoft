package com.example.demo.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigInteger;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "deviceinfo")
public class DeviceInfo {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "standort")
    private String standort;

    @Column(name = "users")
    private Integer users;

    //TODO: tags should be an array of strings
    @Column(name = "tags")
    private String tags;

   /* @Column(name = "gesamtverbrauch")
    private Double gesamtVerbrauch; */

}

package com.mitrais.cdc.covid19backend.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String email;
    private String covid19Status;
    private String name;
    private String mobilePhone;
    private String token;

    @OneToOne
    private User user;
}

package com.mitrais.cdc.covid19backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.*;

@Entity
@Getter
@Setter
public class User {

    @Id
    @NotNull
    private String uuid; /*Generated from mobile application*/

    @NotNull
    private String username; /*Filed with IMEI*/

    @NotNull
    private String password; /*Filed with MAC ADDRESS*/

    private boolean enabled;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    @NotNull
    @CollectionTable(name="user_roles", joinColumns = @JoinColumn(name="user_uuid"))
    private List<String> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade=CascadeType.ALL)
    @JsonIgnore
    private Set<UserDevice> userDevices = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private UserProfile userProfile;

    public User() {

    }
}

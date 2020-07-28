package com.mitrais.cdc.covid19backend.payload;

import com.mitrais.cdc.covid19backend.entity.User;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class UserProfilePayload implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank(message = "is mandatory")
    @NotEmpty(message = "must be not empty")
    private String email;

    @NotEmpty(message = "must be not empty")
    @NotBlank(message = "is mandatory")
    private String covid19Status;

    @NotEmpty(message = "must be not empty")
    @NotBlank(message = "is mandatory")
    private String name;

    @NotEmpty(message = "must be not empty")
    @NotBlank(message = "is mandatory")
    private String mobilePhone;

    private String token;
    private User user;
}

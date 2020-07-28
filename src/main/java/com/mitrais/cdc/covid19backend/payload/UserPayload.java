package com.mitrais.cdc.covid19backend.payload;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPayload implements Serializable {
    @Id
    @NotBlank(message = "is mandatory")
    private String uuid; /*Generated from mobile application*/

    @NotBlank(message = "is mandatory")
    private String username; /*Filed with IMEI*/

    @NotBlank(message = "is mandatory")
    private String password; /*Filed with MAC ADDRESS*/

    private boolean enabled;


    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    @NotNull(message = "is mandatory")
    @Size(min = 1, message = "should be an ADMIN or USER")
    private List<String> roles = new ArrayList<>();

    public UserPayload() {

    }
}

package com.mitrais.cdc.covid19backend.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class CovidPayload implements Serializable {

    @NotEmpty
    @NotNull
    @NotBlank(message = "is mandatory")
    private String uuid;

    @NotEmpty
    @NotNull
    @NotBlank(message = "is mandatory")
    private String name;

    @NotEmpty
    @NotNull
    @NotBlank(message = "is mandatory")
    private String mobilePhone;

    public CovidPayload(@NotEmpty @NotNull @NotBlank(message = "is mandatory") String uuid, @NotEmpty @NotNull @NotBlank(message = "is mandatory") String name, @NotEmpty @NotNull @NotBlank(message = "is mandatory") String mobilePhone) {
        this.uuid = uuid;
        this.name = name;
        this.mobilePhone = mobilePhone;
    }
}

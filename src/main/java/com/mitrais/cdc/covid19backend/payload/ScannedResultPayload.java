package com.mitrais.cdc.covid19backend.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ScannedResultPayload implements Serializable {

    @NotEmpty(message = "is mandatory")
    private List<UserDevicePayload> UserDevices;

    public ScannedResultPayload(@Valid @NotEmpty List<UserDevicePayload> userDevices) {
        UserDevices = userDevices;
    }

    public ScannedResultPayload() {

    }
}

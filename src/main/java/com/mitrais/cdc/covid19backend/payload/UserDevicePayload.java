package com.mitrais.cdc.covid19backend.payload;

import com.mitrais.cdc.covid19backend.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;

@Data
public class UserDevicePayload implements Serializable {

    @NotEmpty(message = "must be not empty")
    @NotBlank(message = "is mandatory")
    private ZonedDateTime foundDate;
    private Long id;

    @NotEmpty(message = "must be not empty")
    @NotBlank(message = "is mandatory")
    private String sender;

    @NotEmpty(message = "must be not empty")
    private User user;

    public UserDevicePayload(@NotBlank(message = "is mandatory") @NotEmpty(message = "must be not empty") ZonedDateTime foundDate, @NotEmpty(message = "must be not empty") User user) {
        this.foundDate = foundDate;
        this.user = user;
    }

    public UserDevicePayload(@NotBlank(message = "is mandatory") @NotEmpty(message = "must be not empty") ZonedDateTime foundDate, @NotBlank(message = "is mandatory") @NotEmpty(message = "must be not empty") String sender, @NotEmpty(message = "must be not empty") User user) {
        this.foundDate = foundDate;
        this.sender = sender;
        this.user = user;
    }

    public UserDevicePayload() {
    }
}

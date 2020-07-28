/**
 * <h1>Authentication Controller</h1>
 * Class to create API Controller for Authentication process
 *
 * @author Syarif Hidayat
 * @version 1.0
 * @since 2019-08-20
 * */

package com.mitrais.cdc.covid19backend.controller;

import com.mitrais.cdc.covid19backend.payload.AuthenticationPayload;
import com.mitrais.cdc.covid19backend.payload.LoginResponse;
import com.mitrais.cdc.covid19backend.service.impl.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api")
public class AuthenticationController extends CrossOriginController {

    private AuthenticationService authenticationService;

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * This method will be used as entry point to the application
     * Successfully authentication process will return username and token.
     *
     * @param authenticationPayload
     * @return It will return username and token.
     */
    @PostMapping("/user/authentication")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody @Nonnull AuthenticationPayload authenticationPayload){
        LoginResponse response = null;

        try {
            response = authenticationService.login(authenticationPayload);
        }catch (InvalidGrantException invalidGrantException){
            throw new InvalidGrantException("Bad Credentials, please use the valid password");
        }catch (NullPointerException nullPointerException){
            throw new NullPointerException("Username is not found, please use the valid username");
        }

        return ResponseEntity.ok(response);
    }
}

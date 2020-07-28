/**
 * <h1>Authentication Service</h1>
 * Class to handle Authentication Process .
 *
 * @author Syarif Hidayat
 * @version 1.0
 * @since 2019-08-20
 * */

package com.mitrais.cdc.covid19backend.service.impl;

import com.mitrais.cdc.covid19backend.payload.AuthenticationPayload;
import com.mitrais.cdc.covid19backend.payload.AuthenticationResponse;
import com.mitrais.cdc.covid19backend.payload.LoginResponse;
import com.mitrais.cdc.covid19backend.payload.TokenPayload;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.util.Collections;


@Service
@Slf4j
public class AuthenticationService {


    private RestTemplate restTemplate;

    @Value("${jwt.clientId}")
    private String clientId;

    @Value("${jwt.client-secret}")
    private String clientSecret;

    public AuthenticationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * This method will be used to authenticate
     * username and password in login process.
     *
     * @param user
     * @return will return username and token
     */
    public LoginResponse login(@Nonnull AuthenticationPayload user){
        String auth = clientId+":"+clientSecret;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        MultiValueMap<String,String> payload = new LinkedMultiValueMap<>();
        payload.add("grant_type", "password");
        payload.add("username", user.getUsername());
        payload.add("password", user.getPassword());
        payload.add("scope", "read");


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "Spring's RestTemplate" );  // value can be whatever
        headers.add("Authorization", authHeader);
        ResponseEntity<AuthenticationResponse> response = restTemplate.exchange("https://localhost:8080/oauth/token",
                HttpMethod.POST, new HttpEntity<>(payload, headers),  AuthenticationResponse.class);

        log.info("TOKEN:"+response.getBody().getAccess_token());

        return new LoginResponse(true, "You have login successfully", new TokenPayload(user.getUsername(), response.getBody().getAccess_token()));
    }
}

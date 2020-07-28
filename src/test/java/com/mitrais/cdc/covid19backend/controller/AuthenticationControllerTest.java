package com.mitrais.cdc.covid19backend.controller;

import com.mitrais.cdc.covid19backend.entity.User;
import com.mitrais.cdc.covid19backend.payload.AuthenticationPayload;
import com.mitrais.cdc.covid19backend.payload.LoginResponse;
import com.mitrais.cdc.covid19backend.service.impl.UserDetailsServices;
import com.mitrais.cdc.covid19backend.service.impl.UserServices;
import com.mitrais.cdc.covid19backend.utility.UserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class AuthenticationControllerTest {

    @Autowired
    AuthenticationController authenticationController;
    private UserDetails userDetails;
    private Authentication authToken;

    @Autowired
    UserDetailsServices userDetailsServices;

    final private String IMEI="351755555523888";
    final private String MACADDRESS="00-22-5D-10-6F-A6";
    final private String UUID="08d07014-5555-468b-9f26-871d43665fde";
    final private String EMAIL="test@email.com";

    @Autowired
    UserServices userServices;

    User createUser(){
        User user = new User();
        user.setUuid(UUID);
        user.setUsername(IMEI);
        user.setPassword(MACADDRESS);
        //user.setEmail(EMAIL);

        List<String> roles = new ArrayList<>();
        roles.add("USER");

        user.setRoles(roles);
        return user;
    }

    @BeforeEach
    void setUp() {
        userDetails = userDetailsServices.loadUserByUsername("admin");
        authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @Test
    void login() {
        userServices.userRegistration(createUser());
        ResponseEntity<LoginResponse> loginResponseResponseEntity = authenticationController.login(new AuthenticationPayload(IMEI, MACADDRESS));

        assertThat("You have login successfully", is(loginResponseResponseEntity.getBody().getMessage()));
        assertThat(IMEI, is(loginResponseResponseEntity.getBody().getData().getUsername()));
        userServices.deleteUserByUsername(IMEI);
    }
}
package com.mitrais.cdc.covid19backend.service.impl;

import com.mitrais.cdc.covid19backend.entity.User;
import com.mitrais.cdc.covid19backend.payload.AuthenticationPayload;
import com.mitrais.cdc.covid19backend.payload.LoginResponse;
import com.mitrais.cdc.covid19backend.utility.UserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class AuthenticationServiceTest {


    private UserDetails userDetails;
    private Authentication authToken;

    @Autowired
    UserDetailsServices userDetailsServices;

    @Autowired
    UserServices userServices;

    @Autowired
    AuthenticationService authenticationService;

    final private String IMEI="351755555523888";
    final private String MACADDRESS="00-22-5D-10-6F-A6";
    final private String UUID="08d07014-5555-468b-9f26-871d43665fde";
    final private String EMAIL="test@email.com";

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
        LoginResponse loginResponse = authenticationService.login(new AuthenticationPayload(IMEI, MACADDRESS));

        assertThat("You have login successfully", is(loginResponse.getMessage()));
        assertThat(IMEI, is(loginResponse.getData().getUsername()));
        userServices.deleteUserByUsername(IMEI);
    }

    @Test
    void loginNegativeTest() {
        userServices.userRegistration(createUser());

        try{
            LoginResponse loginResponse = authenticationService.login(new AuthenticationPayload(IMEI, "USER123"));

        }catch (Exception e){
            assertThat(e.getClass(), is(HttpClientErrorException.BadRequest.class));
        }

        userServices.deleteUserByUsername(IMEI);
    }
}
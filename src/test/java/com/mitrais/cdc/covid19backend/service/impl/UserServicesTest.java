package com.mitrais.cdc.covid19backend.service.impl;

import com.mitrais.cdc.covid19backend.entity.User;
import com.mitrais.cdc.covid19backend.payload.APIResponse;
import com.mitrais.cdc.covid19backend.payload.CovidPayload;
import com.mitrais.cdc.covid19backend.payload.UserProfilePayload;
import com.mitrais.cdc.covid19backend.utility.UserDetails;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServicesTest {

    private UserDetails userDetails;
    private Authentication authToken;

    @Autowired
    UserDetailsServices userDetailsServices;

    @Autowired
    UserServices userServices;

    @Autowired
    UserProfileServices userProfileServices;

    final private String IMEI="351755555523888";
    final private String MACADDRESS="00-22-5D-10-6F-A6";
    final private String UUID="08d07014-5555-468b-9f26-871d43665fde";
    final private String EMAIL="test@email.com";

    @BeforeEach
    void setUp() {
        userDetails = userDetailsServices.loadUserByUsername("admin");
        authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    User createUser(){
        User user = new User();
        user.setUuid(UUID);
        user.setUsername(IMEI);
        user.setPassword(MACADDRESS);

        List<String> roles = new ArrayList<>();
        roles.add("USER");

        user.setRoles(roles);
        return user;
    }

    @Test
    @Order(1)
    void userRegistration() {

        APIResponse response = userServices.userRegistration(createUser());
        User userResponse = (User) response.getData();

        assertThat("User has been registered successfully", is(response.getMessage()));
        assertThat(IMEI, is(userResponse.getUsername()));
        assertThat(UUID, is(userResponse.getUuid()));
    }

    @Test
    @Order(2)
    void updateUserData() {
        User user = (User) userServices.findUserByUsername(IMEI).getData();
        user.setUsername("user.update");
        APIResponse response = userServices.updateUserData(user);
        User userResponse = (User) response.getData();

        assertThat("Update user data has been updated successfully", is(response.getMessage()));
        assertThat("user.update", is(userResponse.getUsername()));
        assertThat(UUID, is(userResponse.getUuid()));

        user.setUsername(IMEI);
        userServices.updateUserData(user);
    }

    @Test
    void resetPassword() {
    }

    @Test
    @Order(8)
    void deleteUserByUsername() {
        User user = (User) userServices.findUserByUsername(IMEI).getData();

        APIResponse response = userServices.deleteUserByUsername(user.getUsername());
        User userResponse = (User) response.getData();

        assertThat("Delete user data has been executed successfully", is(response.getMessage()));
        assertThat(IMEI, is(userResponse.getUsername()));
        assertThat(UUID, is(userResponse.getUuid()));
    }


    @Test
    @Order(3)
    void findUserByUsername() {
        APIResponse response = userServices.findUserByUsername(IMEI);
        User userResponse = (User) response.getData();

        assertThat("User data was found", is(response.getMessage()));
        assertThat(IMEI, is(userResponse.getUsername()));
        assertThat(UUID, is(userResponse.getUuid()));
    }

   /* @Test
    @Order(4)
    void findUserByEmail() {
        User user = userServices.findUserByEmail(EMAIL);

        assertThat(IMEI, is(user.getUsername()));
        assertThat(UUID, is(user.getUuid()));
    }*/

    @Test
    @Order(5)
    void getAllUsers() {
        Pageable pageable = PageRequest.of(0, 5);
        APIResponse apiResponse = userServices.getAllUsers(pageable);
        List<User> users = (List<User>)apiResponse.getData();
        
        assertThat("Users data was founds", is(apiResponse.getMessage()));

    }

    @Test
    void generateUUID() {
    }

    @Test
    void getUserRepository() {
    }

    @Test
    void setUserRepository() {
    }

    @Test
    @Order(6)
    void findByUuid() {
        APIResponse apiResponse = userServices.findByUuid(UUID);
        User user = (User) apiResponse.getData();

        assertThat(IMEI, is(user.getUsername()));
        assertThat(UUID, is(user.getUuid()));
    }

    @Test
    @Order(7)
    void setCovidStatus(){
        User user = (User) userServices.findByUuid(UUID).getData();
        CovidPayload covidPayload = new CovidPayload(UUID, "John Lennon", "085287234827");
        UserProfilePayload userProfilePayload = new UserProfilePayload();
        userProfilePayload.setUser(user);
        userProfilePayload.setMobilePhone("085287234921");
        userProfilePayload.setName("John Lennon");
        userProfilePayload.setCovid19Status("ODP");
        APIResponse apiResponse = userProfileServices.setCovidStatus(userProfilePayload);

        UserProfilePayload userResponse = (UserProfilePayload) apiResponse.getData();

        assertThat(UUID, is(userResponse.getUser().getUuid()));
        assertThat("Covid Status for The User has been updated", is(apiResponse.getMessage()));

    }

}
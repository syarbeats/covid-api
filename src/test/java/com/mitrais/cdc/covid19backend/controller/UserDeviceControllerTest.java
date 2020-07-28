package com.mitrais.cdc.covid19backend.controller;

import com.mitrais.cdc.covid19backend.entity.User;
import com.mitrais.cdc.covid19backend.entity.UserDevice;
import com.mitrais.cdc.covid19backend.payload.*;
import com.mitrais.cdc.covid19backend.service.impl.UserDetailsServices;
import com.mitrais.cdc.covid19backend.utility.UserDetails;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDeviceControllerTest {

    private UserDetails userDetails;
    private Authentication authToken;

    @Autowired
    UserController userController;

    @Autowired
    UserDeviceController userDeviceController;

    @Autowired
    UserDetailsServices userDetailsServices;

    final private String IMEI="351755555523888";
    final private String MACADDRESS="00-22-5D-10-6F-A6";
    final private String UUID="08d07014-5555-468b-9f26-871d43665fde";


    UserPayload createUser(){
        UserPayload user = new UserPayload();
        user.setUuid(UUID);
        user.setUsername(IMEI);
        user.setPassword(MACADDRESS);

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

    void setupData(){
        userController.userRegister(createUser());

        APIResponse apiResponsedata = (APIResponse) userController.findUserByUUID(UUID).getBody().getData();
        User user = (User) apiResponsedata.getData();
        CovidPayload covidPayload = new CovidPayload(user.getUuid(), "John Lennon", "085287234827");
        userController.setCovid19Status(covidPayload).getBody().getData();

        ZonedDateTime date1 = ZonedDateTime.now();
        ZonedDateTime date2 = ZonedDateTime.now().plusMinutes(10);
        ZonedDateTime date3 = ZonedDateTime.now().plusMinutes(15);
        UserDevicePayload userDevicePayload1 =  new UserDevicePayload(date1, "08d07022-5115-468b-9f26-871d43665fde",user);
        UserDevicePayload userDevicePayload2 =  new UserDevicePayload(date2, "08d07022-5115-468b-9f26-871d43665fde",user);
        UserDevicePayload userDevicePayload3 =  new UserDevicePayload(date3, "08d07022-5115-468b-9f26-871d43665fde",user);
        List<UserDevicePayload> userDevicePayloads = new ArrayList<>();
        userDevicePayloads.add(userDevicePayload1);
        userDevicePayloads.add(userDevicePayload2);
        userDevicePayloads.add(userDevicePayload3);
        ScannedResultPayload scannedResultPayload = new ScannedResultPayload(userDevicePayloads);
        userController.saveScannedResult(scannedResultPayload).getBody();
    }

    @Test
    @Order(1)
    void getAllUserDevices() {
        setupData();
        Pageable pageable = PageRequest.of(0, 5);
        ResponseWrapper responseWrapper = userDeviceController.getAllUserDevices(pageable).getBody();
        List<UserDevice> userDevices = (List<UserDevice>) responseWrapper.getData();

        assertThat("User Devices is found", is(responseWrapper.getMessage()));
        assertThat("08d07022-5115-468b-9f26-871d43665fde", is(userDevices.get(0).getSender()));

    }

    @Test
    @Order(2)
    void getAllODPContactedWithHeathyUUID() {
        Pageable pageable = PageRequest.of(0, 5);
        ResponseWrapper responseWrapper = userController.findUserByUsername("admin").getBody();
        APIResponse apiResponse = (APIResponse) responseWrapper.getData();
        User user = (User) apiResponse.getData();
        ResponseWrapper responseWrapper1 = userDeviceController.getAllODPContactedWithHeathyUUID("08d07022-5115-468b-9f26-871d43665fde", pageable).getBody();
        List<UserDevice> userDevices = (List<UserDevice>) responseWrapper1.getData();

        assertThat("ALL ODP User Devices that contacted with the given healthy uuid is found", is(responseWrapper1.getMessage()));
        assertThat("08d07022-5115-468b-9f26-871d43665fde", is(userDevices.get(0).getSender()));

    }

    @Test
    @Order(3)
    void getAllUserContactedByODPUUID() {
        Pageable pageable = PageRequest.of(0, 5);
        ResponseWrapper responseWrapper = userController.findUserByUsername(IMEI).getBody();
        APIResponse apiResponse = (APIResponse) responseWrapper.getData();
        User user = (User) apiResponse.getData();

        ResponseWrapper responseWrapper1 = userDeviceController.getAllUserContactedByODPUUID(user.getUuid(),pageable).getBody();
        List<UserDevice> userDevices = (List<UserDevice>) responseWrapper1.getData();

        ResponseWrapper responseWrapper2 = userController.findUserByUsername("admin").getBody();
        APIResponse apiResponse1 = (APIResponse) responseWrapper2.getData();
        User userData = (User) apiResponse1.getData();

        assertThat("User Devices that contacted with the given ODP UUID is found", is(responseWrapper1.getMessage()));
        assertThat(userData.getUuid(), is(userDevices.get(0).getSender()));

        userController.deleteUserDataByUsername(IMEI);
    }
}
package com.mitrais.cdc.covid19backend.service.impl;

import com.mitrais.cdc.covid19backend.entity.User;
import com.mitrais.cdc.covid19backend.entity.UserDevice;
import com.mitrais.cdc.covid19backend.payload.APIResponse;
import com.mitrais.cdc.covid19backend.payload.ScannedResultPayload;
import com.mitrais.cdc.covid19backend.payload.UserDevicePayload;
import com.mitrais.cdc.covid19backend.repository.UserDeviceRepository;
import com.mitrais.cdc.covid19backend.utility.UserDetails;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDeviceServiceTest {

    final private String IMEI="351755555523888";
    final private String MACADDRESS="00-22-5D-10-6F-A6";
    final private String UUID="08d07014-5555-468b-9f26-871d43665fde";
    final private String EMAIL="test@email.com";

    private UserDetails userDetails;
    private Authentication authToken;

    @Autowired
    UserDetailsServices userDetailsServices;

    @Autowired
    UserServices userServices;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserDeviceService userDeviceService;

    @Autowired
    UserDeviceRepository userDeviceRepository;

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

    @BeforeEach
    void setUp() {
        userDetails = userDetailsServices.loadUserByUsername("admin");
        authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @Test
    @Order(1)
    void saveScannedResult() {
        userServices.userRegistration(createUser());
        User user = (User) userServices.findByUuid(UUID).getData();
        ZonedDateTime date1 = ZonedDateTime.now();
        ZonedDateTime date2 = ZonedDateTime.now().plusMinutes(10);
        ZonedDateTime date3 = ZonedDateTime.now().plusMinutes(15);
        UserDevicePayload userDevicePayload1 =  new UserDevicePayload(date1,"08d07022-5115-468b-9f26-871d43665fde", user);
        UserDevicePayload userDevicePayload2 =  new UserDevicePayload(date2,"08d07022-5115-468b-9f26-871d43665fde", user);
        UserDevicePayload userDevicePayload3 =  new UserDevicePayload(date3,"08d07022-5115-468b-9f26-871d43665fde", user);
        List<UserDevicePayload> userDevicePayloads = new ArrayList<>();
        userDevicePayloads.add(userDevicePayload1);
        userDevicePayloads.add(userDevicePayload2);
        userDevicePayloads.add(userDevicePayload3);
        ScannedResultPayload scannedResultPayload = new ScannedResultPayload(userDevicePayloads);

        APIResponse apiResponse = userDeviceService.saveScannedResult(scannedResultPayload);
        List<UserDevice> userDevices = (List<UserDevice>) apiResponse.getData();

        assertThat("Scanned result has been saved successfully", is(apiResponse.getMessage()));

    }

    @Test
    @Order(2)
    void dtoToEntity() {
        User user = (User) userServices.findByUuid(UUID).getData();
        ZonedDateTime date = ZonedDateTime.now();
        UserDevicePayload userDevicePayload =  new UserDevicePayload(date, user);
        UserDevice userDevice = new UserDevice();
        UserDevice userDeviceData = userDeviceService.dtoToEntity(userDevice, userDevicePayload);

        assertThat(user.getUuid(), is(userDeviceData.getUser().getUuid()));
        assertThat(date, is(userDeviceData.getFoundDate()));
        userServices.deleteUserByUsername(IMEI);
    }
}
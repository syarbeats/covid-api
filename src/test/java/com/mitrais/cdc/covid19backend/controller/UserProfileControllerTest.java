package com.mitrais.cdc.covid19backend.controller;

import com.mitrais.cdc.covid19backend.entity.User;
import com.mitrais.cdc.covid19backend.entity.UserProfile;
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

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserProfileControllerTest {

    private UserDetails userDetails;
    private Authentication authToken;

    @Autowired
    UserController userController;

    @Autowired
    UserProfileController userProfileController;

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
    void createUserProfile() {

        setupData();
        ResponseWrapper responseWrapper = userController.findUserByUsername(IMEI).getBody();
        APIResponse apiResponse = (APIResponse) responseWrapper.getData();
        User user = (User) apiResponse.getData();

        UserProfilePayload userProfilePayload = new UserProfilePayload();
        userProfilePayload.setCovid19Status("Healthy");
        userProfilePayload.setUser(user);
        userProfilePayload.setMobilePhone("000000000000");
        userProfilePayload.setName("Anonymous");
        userProfilePayload.setToken("");
        ResponseWrapper responseWrapper1 = userProfileController.createUserProfile(userProfilePayload).getBody();
        APIResponse apiResponse1 = (APIResponse) responseWrapper1.getData();
        UserProfile userProfile = (UserProfile) apiResponse1.getData();

        assertThat("User Profile has been created successfully", is(responseWrapper1.getMessage()));
        assertThat(IMEI, is(userProfile.getUser().getUsername()));

        userController.deleteUserDataByUsername(IMEI);
    }

    @Test
    @Order(2)
    void updateUserProfile() {
        setupData();
        //ResponseWrapper responseWrapper = userProfileController.findUserProfileByUUID(UUID).getBody();
        //UserProfile userProfile = (UserProfile) responseWrapper.getData();

        ResponseWrapper responseWrapper1 = userController.findUserByUsername(IMEI).getBody();
        APIResponse apiResponse = (APIResponse) responseWrapper1.getData();
        User user = (User) apiResponse.getData();

        UserProfilePayload userProfilePayload = new UserProfilePayload();
        userProfilePayload.setCovid19Status("Healthy");
        userProfilePayload.setUser(user);
        userProfilePayload.setMobilePhone("000000000000");
        userProfilePayload.setName("John");
        userProfilePayload.setToken("");

        ResponseWrapper responseWrapper3 = userProfileController.updateUserProfile(userProfilePayload).getBody();
        APIResponse apiResponse1 = (APIResponse) responseWrapper3.getData();
        UserProfile userProfile1 = (UserProfile) apiResponse1.getData();

        assertThat("User Profile has been updated successfully", is(responseWrapper3.getMessage()));
        assertThat("John", is(userProfile1.getName()));

        userController.deleteUserDataByUsername(IMEI);
    }

    @Test
    @Order(3)
    void findUserProfileByUUID() {
        setupData();
        ResponseWrapper responseWrapper = userProfileController.findUserProfileByUUID(UUID).getBody();
        UserProfile userProfile = (UserProfile) responseWrapper.getData();

        assertThat("User Profile is found", is(responseWrapper.getMessage()));
        assertThat("ODP", is(userProfile.getCovid19Status()));

        userController.deleteUserDataByUsername(IMEI);

    }

    @Test
    @Order(4)
    void deleteUserProfile() {
        setupData();
        ResponseWrapper responseWrapper = userProfileController.deleteUserProfile(UUID).getBody();

        assertThat("User Profile is delete successfully", is(responseWrapper.getMessage()));

        userController.deleteUserDataByUsername(IMEI);
    }

    @Test
    @Order(5)
    void findAllUserProfiles() {
        setupData();
        Pageable pageable = PageRequest.of(0, 5);
        ResponseWrapper responseWrapper = userProfileController.findAllUserProfiles(pageable).getBody();
        List<UserProfile> userProfiles = (List<UserProfile>) responseWrapper.getData();

        assertThat("User Profiles is found", is(responseWrapper.getMessage()));
        assertThat("Healthy", is(userProfiles.get(0).getCovid19Status()));

        userController.deleteUserDataByUsername(IMEI);
    }

    @Test
    @Order(6)
    void findAllCovidStatusUser() {
        setupData();
        String status = "ODP";
        Pageable pageable = PageRequest.of(0, 5);
        ResponseWrapper responseWrapper = userProfileController.findAllCovidStatusUser(status, pageable).getBody();
        List<UserProfile> userProfileList = (List<UserProfile>) responseWrapper.getData();

        assertThat("User Profiles for status " + status+ " is found", is(responseWrapper.getMessage()));
        assertThat(UUID, is(userProfileList.get(0).getUser().getUuid()));
        userController.deleteUserDataByUsername(IMEI);
    }

    @Test
    @Order(7)
    void findAllHealthyUserContacted() {
        setupData();
        Pageable pageable = PageRequest.of(0, 5);
        ResponseWrapper responseWrapper = userProfileController.findAllHealthyUserContacted(pageable).getBody();
        List<UserProfile> userProfileList = (List<UserProfile>) responseWrapper.getData();

        assertThat("Healthy User profile that contacted with ODP is found.", is(responseWrapper.getMessage()));
        assertThat("08d07022-5115-468b-9f26-871d43665fde", is(userProfileList.get(0).getUser().getUuid()));
        userController.deleteUserDataByUsername(IMEI);
    }

    @Test
    void dtoToEntity() {
        User user = new User();
        user.setEnabled(true);
        user.setUuid(UUID);
        user.setUsername(IMEI);

        UserProfilePayload userProfilePayload = new UserProfilePayload();
        userProfilePayload.setCovid19Status("Healthy");
        userProfilePayload.setUser(user);
        userProfilePayload.setMobilePhone("000000000000");
        userProfilePayload.setName("Anonymous");
        userProfilePayload.setToken("aZwqdda111");

        UserProfile userProfile = userProfileController.dtoToEntity(new UserProfile(), userProfilePayload);

        assertThat("Healthy", is(userProfile.getCovid19Status()));
        assertThat("aZwqdda111", is(userProfile.getToken()));
    }

    @Test
    void isValid() {
        boolean isValid = userProfileController.isValid(UUID);
        assertThat(true, is(isValid));
    }
}
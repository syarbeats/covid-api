package com.mitrais.cdc.covid19backend.service.impl;

import com.mitrais.cdc.covid19backend.controller.UserController;
import com.mitrais.cdc.covid19backend.controller.UserProfileController;
import com.mitrais.cdc.covid19backend.entity.User;
import com.mitrais.cdc.covid19backend.entity.UserProfile;
import com.mitrais.cdc.covid19backend.payload.*;
import com.mitrais.cdc.covid19backend.utility.UserDetails;
import org.apiguardian.api.API;
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
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserProfileServicesTest {

    private UserDetails userDetails;
    private Authentication authToken;

    @Autowired
    UserController userController;

    @Autowired
    UserProfileServices userProfileServices;

    @Autowired
    UserDeviceService userDeviceService;

    @Autowired
    UserDetailsServices userDetailsServices;

    final private String IMEI="351755555523888";
    final private String MACADDRESS="00-22-5D-10-6F-A6";
    final private String UUID="08d07014-5555-468b-9f26-871d43665fde";
    final private String ODP = "ODP";
    final private String HEALTHY = "Healthy";
    final private String MOBILEPHONE = "000000000000";
    final private String TOKEN = "zSxaD1111";

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

    UserProfile setUpUserProfile(){

        UserProfile userProfile = new UserProfile();
        userProfile.setName(IMEI);
        userProfile.setCovid19Status(HEALTHY);
        userProfile.setMobilePhone(MACADDRESS);
        userProfile.setToken(TOKEN);

        return userProfile;
    }

    void setupData(User user){

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
        userDeviceService.saveScannedResult(scannedResultPayload);
    }


    @BeforeEach
    void setUp() {
        userDetails = userDetailsServices.loadUserByUsername("admin");
        authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @Test
    void findUserProfileById() {
    }

    @Test
    @Order(1)
    void createUserProfile() {

        ResponseWrapper responseWrapper = userController.userRegister(createUser()).getBody();
        User user = (User) responseWrapper.getData();

        UserProfile userProfile = setUpUserProfile();
        userProfile.setUser(user);

        UserProfile userProfile1 = (UserProfile) userProfileServices.createUserProfile(userProfile).getData();

        assertThat(HEALTHY, is(userProfile1.getCovid19Status()));
        assertThat(IMEI, is(userProfile1.getUser().getUsername()));
        userController.deleteUserDataByUsername(IMEI);
    }

    @Test
    @Order(2)
    void updateUserProfile() {
        ResponseWrapper responseWrapper = userController.userRegister(createUser()).getBody();
        User user = (User) responseWrapper.getData();

        UserProfile userProfile = setUpUserProfile();
        userProfile.setName("John");
        userProfile.setUser(user);
        userProfile = (UserProfile) userProfileServices.updateUserProfile(userProfile).getData();

        assertThat("John", is(userProfile.getName()));
        assertThat(IMEI, is(userProfile.getUser().getUsername()));
        userController.deleteUserDataByUsername(IMEI);
    }

    @Test
    @Order(3)
    void deleteUserProfileByUUID() {
        ResponseWrapper responseWrapper = userController.userRegister(createUser()).getBody();
        User user = (User) responseWrapper.getData();

        UserProfile userProfile = setUpUserProfile();
        userProfile.setName("John");
        userProfile.setUser(user);
        APIResponse apiResponse = userProfileServices.deleteUserProfileByUUID(UUID);

        assertThat("User Profile has been deleted successfully", is(apiResponse.getMessage()));
        userController.deleteUserDataByUsername(IMEI);
    }

    @Test
    @Order(4)
    void getAllUserProfile() {
        Pageable pageable = PageRequest.of(0, 5);
        ResponseWrapper responseWrapper = userController.userRegister(createUser()).getBody();
        User user = (User) responseWrapper.getData();

        UserProfile userProfile = setUpUserProfile();
        userProfile.setUser(user);
        APIResponse apiResponse = userProfileServices.getAllUserProfile(pageable);
        List<UserProfile> userProfiles = (List<UserProfile>) apiResponse.getData();

        assertThat("User Profile is found", is(apiResponse.getMessage()));
        assertThat(HEALTHY, is(userProfiles.get(0).getCovid19Status()));
        userController.deleteUserDataByUsername(IMEI);
    }

    @Test
    @Order(5)
    void getAllUserProfileData() {
        Pageable pageable = PageRequest.of(0, 5);
        ResponseWrapper responseWrapper = userController.userRegister(createUser()).getBody();
        User user = (User) responseWrapper.getData();

        UserProfile userProfile = setUpUserProfile();
        userProfile.setUser(user);
        List<UserProfile> userProfiles = userProfileServices.getAllUserProfileData(pageable);

        assertThat(HEALTHY, is(userProfiles.get(0).getCovid19Status()));
        userController.deleteUserDataByUsername(IMEI);
    }

    @Test
    @Order(6)
    void getAllCovidStatusUser() {
        Pageable pageable = PageRequest.of(0, 5);
        ResponseWrapper responseWrapper = userController.userRegister(createUser()).getBody();
        User user = (User) responseWrapper.getData();

        UserProfile userProfile = setUpUserProfile();
        userProfile.setUser(user);
        List<UserProfile> userProfiles = userProfileServices.getAllCovidStatusUser(pageable, HEALTHY);

        assertThat("08d07022-5115-468b-9f26-871d43665fde", is(userProfiles.get(0).getUser().getUuid()));
        userController.deleteUserDataByUsername(IMEI);
    }

    @Test
    @Order(7)
    void getAllHealthyUserContactedODP() {

        Pageable pageable = PageRequest.of(0, 5);
        ResponseWrapper responseWrapper = userController.userRegister(createUser()).getBody();
        User user = (User) responseWrapper.getData();
        setupData(user);
        UserProfile userProfile = setUpUserProfile();
        userProfile.setUser(user);
        List<UserProfile> userProfiles = userProfileServices.getAllHealthyUserContactedODP(pageable);

        assertThat("08d07022-5115-468b-9f26-871d43665fde", is(userProfiles.get(0).getUser().getUuid()));
        userController.deleteUserDataByUsername(IMEI);
    }
}
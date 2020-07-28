package com.mitrais.cdc.covid19backend.controller;

import com.mitrais.cdc.covid19backend.entity.User;
import com.mitrais.cdc.covid19backend.entity.UserDevice;
import com.mitrais.cdc.covid19backend.payload.*;
import com.mitrais.cdc.covid19backend.service.impl.UserDetailsServices;
import com.mitrais.cdc.covid19backend.service.impl.UserServices;
import com.mitrais.cdc.covid19backend.utility.UserDetails;
import org.apache.catalina.Server;
import org.junit.Rule;
import org.junit.jupiter.api.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ServerErrorException;

import javax.persistence.EntityExistsException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

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

    @Autowired
    UserController userController;

    UserPayload createUser(){
        UserPayload user = new UserPayload();
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
    @Order(1)
    void userRegister() {
        ResponseEntity<ResponseWrapper> responseWrapperResponseEntity = userController.userRegister(createUser());
        ResponseWrapper responseWrapper = responseWrapperResponseEntity.getBody();
        User user = (User) responseWrapper.getData();

        assertThat("You have been registered successfully", is(responseWrapper.getMessage()));
        assertThat(IMEI, is(user.getUsername()));
        assertThat(UUID, is(user.getUuid()));
    }

    @Test
    @Order(20)
    void userRegisterV1() {
        ResponseEntity<ResponseWrapper> responseWrapperResponseEntity = userController.userRegisterV1(createUser());
        ResponseWrapper responseWrapper = responseWrapperResponseEntity.getBody();
        LoginResponse loginResponse = (LoginResponse) responseWrapper.getData();
        TokenPayload tokenPayload = loginResponse.getData();

        assertThat("You have been registered and get token successfully", is(responseWrapper.getMessage()));
        assertThat(IMEI, is(tokenPayload.getUsername()));
    }

    @Test
    @Order(2)
    void userRegisterNegativeExistingUsername() {
        try {
            ResponseEntity<ResponseWrapper> responseWrapperResponseEntity = userController.userRegister(createUser());
            ResponseWrapper responseWrapper = responseWrapperResponseEntity.getBody();

        } catch (EntityExistsException e) {
            assertThat(e.getClass(), is(EntityExistsException.class));
            assertThat(e.getLocalizedMessage(), is("Your username is not available, please find the new one"));
        }

    }

    @Test
    @Order(21)
    void userRegisterV1NegativeExistingUsername() {

        try {
            ResponseEntity<ResponseWrapper> responseWrapperResponseEntity = userController.userRegisterV1(createUser());
            ResponseWrapper responseWrapper = responseWrapperResponseEntity.getBody();
            User user = (User) responseWrapper.getData();
        } catch (EntityExistsException e) {
            assertThat(e.getClass(), is(EntityExistsException.class));
            assertThat(e.getLocalizedMessage(), is("Your username is not available, please find the new one"));
        }

    }

    @Test
    @Order(3)
    void userRegisterNegativeExistingUUID() {
        try {
            UserPayload userPayload = createUser();
            userPayload.setUsername("user1");
            ResponseEntity<ResponseWrapper> responseWrapperResponseEntity = userController.userRegister(userPayload);
            ResponseWrapper responseWrapper = responseWrapperResponseEntity.getBody();

        } catch (EntityExistsException e) {
            assertThat(e.getClass(), is(EntityExistsException.class));
            assertThat(e.getLocalizedMessage(), is("Your UUID has been registered, please register with the new one"));
        }

    }

    @Test
    @Order(22)
    void userRegisterV1NegativeExistingUUID() {

        try {
            UserPayload userPayload = createUser();
            userPayload.setUsername("user1");
            ResponseEntity<ResponseWrapper> responseWrapperResponseEntity = userController.userRegisterV1(userPayload);
            ResponseWrapper responseWrapper = responseWrapperResponseEntity.getBody();
            User user = (User) responseWrapper.getData();

        } catch (EntityExistsException e) {
            assertThat(e.getClass(), is(EntityExistsException.class));
            assertThat(e.getLocalizedMessage(), is("Your UUID has been registered, please register with the new one"));
        }

    }

    @Test
    @Order(4)
    void userRegisterNegativeUsernameEmpty() {

        try {
            UserPayload userPayload = new UserPayload();
            userPayload.setUuid("08d07014-5555-468b-9f26-871d43665fff");
            userPayload.setUsername("");
            userPayload.setPassword("pass123");

            List<String> roles = new ArrayList<>();
            roles.add("USER");

            userPayload.setRoles(roles);

            ResponseEntity<ResponseWrapper> responseWrapperResponseEntity = userController.userRegister(userPayload);


        } catch (ServerErrorException e) {
            assertThat(e.getClass(), is(ServerErrorException.class));
            assertThat(e.getMessage(), is("500 INTERNAL_SERVER_ERROR \"Please insert the empty field, username is mandatory\""));
        }

    }

    @Test
    @Order(23)
    void userRegisterV1NegativeUsernameEmpty() {

        try {
            UserPayload userPayload = new UserPayload();
            userPayload.setUuid("08d07014-5555-468b-9f26-871d43665fff");
            userPayload.setUsername("");
            userPayload.setPassword("pass123");

            List<String> roles = new ArrayList<>();
            roles.add("USER");

            userPayload.setRoles(roles);

            ResponseEntity<ResponseWrapper> responseWrapperResponseEntity = userController.userRegisterV1(userPayload);
            ResponseWrapper responseWrapper = responseWrapperResponseEntity.getBody();

        } catch (ServerErrorException e) {
            assertThat(e.getClass(), is(ServerErrorException.class));
            assertThat(e.getMessage(), is("500 INTERNAL_SERVER_ERROR \"Please insert the empty field, username is mandatory\""));
        }
    }


    @Test
    @Order(5)
    void userRegisterNegativeUUIDEmpty() {

        try {
            UserPayload userPayload = new UserPayload();
            userPayload.setUuid("");
            userPayload.setUsername("user1");
            userPayload.setPassword("pass123");

            List<String> roles = new ArrayList<>();
            roles.add("USER");

            userPayload.setRoles(roles);

            userController.userRegister(userPayload);

        } catch (ServerErrorException e) {
            assertThat(e.getClass(), is(ServerErrorException.class));
            assertThat(e.getLocalizedMessage(), is("500 INTERNAL_SERVER_ERROR \"Please use the valid UUID\""));
        }

    }

    @Test
    @Order(24)
    void userRegisterV1NegativeUUIDEmpty() {

        try {
            UserPayload userPayload = new UserPayload();
            userPayload.setUuid("");
            userPayload.setUsername("user1");
            userPayload.setPassword("pass123");

            List<String> roles = new ArrayList<>();
            roles.add("USER");

            userPayload.setRoles(roles);

            ResponseEntity<ResponseWrapper> responseWrapperResponseEntity = userController.userRegisterV1(userPayload);
            ResponseWrapper responseWrapper = responseWrapperResponseEntity.getBody();
            User user = (User) responseWrapper.getData();

        } catch (ServerErrorException e) {
            assertThat(e.getClass(), is(ServerErrorException.class));
            assertThat(e.getLocalizedMessage(), is("500 INTERNAL_SERVER_ERROR \"Please use the valid UUID\""));
        }
    }


    @Test
    @Order(6)
    void userRegisterNegativePasswordEmpty() {

        try {
            UserPayload userPayload = new UserPayload();
            userPayload.setUuid("08d07014-5555-468b-9f26-871d43665fff");
            userPayload.setUsername("user1");
            userPayload.setPassword("");

            List<String> roles = new ArrayList<>();
            roles.add("USER");

            userPayload.setRoles(roles);

            ResponseEntity<ResponseWrapper> responseWrapperResponseEntity = userController.userRegister(userPayload);

        } catch (ServerErrorException e) {
            assertThat(e.getClass(), is(ServerErrorException.class));
            assertThat(e.getLocalizedMessage(), is("500 INTERNAL_SERVER_ERROR \"Please insert the empty field, password is mandatory\""));
        }

    }

    @Test
    @Order(25)
    void userRegisterV1NegativePasswordEmpty() {

        try {
            UserPayload userPayload = new UserPayload();
            userPayload.setUuid("08d07014-5555-468b-9f26-871d43665fff");
            userPayload.setUsername("user1");
            userPayload.setPassword("");

            List<String> roles = new ArrayList<>();
            roles.add("USER");

            userPayload.setRoles(roles);

            ResponseEntity<ResponseWrapper> responseWrapperResponseEntity = userController.userRegisterV1(userPayload);
            ResponseWrapper responseWrapper = responseWrapperResponseEntity.getBody();

        } catch (ServerErrorException e) {
            assertThat(e.getClass(), is(ServerErrorException.class));
            assertThat(e.getLocalizedMessage(), is("500 INTERNAL_SERVER_ERROR \"Please insert the empty field, password is mandatory\""));
        }
    }

    @Test
    @Order(7)
    void userRegisterNegativeRolesEmpty() {

        try {
            UserPayload userPayload = new UserPayload();
            userPayload.setUuid("08d07014-5555-468b-9f26-871d43665fff");
            userPayload.setUsername("user1");
            userPayload.setPassword("pass123");

            List<String> roles = new ArrayList<>();
            roles.add("");

            userPayload.setRoles(roles);

            ResponseEntity<ResponseWrapper> responseWrapperResponseEntity = userController.userRegister(userPayload);

        } catch (ControllerException e) {
            assertThat(e.getClass(), is(ControllerException.class));
            assertThat(e.getLocalizedMessage(), is("Please insert the empty field, roles is mandatory"));
        }

    }

    @Test
    @Order(26)
    void userRegisterV1NegativeRolesEmpty() {

        try {
            UserPayload userPayload = new UserPayload();
            userPayload.setUuid("08d07014-5555-468b-9f26-871d43665fff");
            userPayload.setUsername("user1");
            userPayload.setPassword("pass123");

            List<String> roles = new ArrayList<>();
            roles.add("");

            userPayload.setRoles(roles);

            ResponseEntity<ResponseWrapper> responseWrapperResponseEntity = userController.userRegisterV1(userPayload);
            ResponseWrapper responseWrapper = responseWrapperResponseEntity.getBody();


        } catch (ControllerException e) {
            assertThat(e.getClass(), is(ControllerException.class));
            assertThat(e.getLocalizedMessage(), is("Please insert the empty field, roles is mandatory"));
        }
    }

    @Test
    @Order(8)
    void userRegisterNegativeRoles2Empty() {

        try {
            UserPayload userPayload = new UserPayload();
            userPayload.setUuid("08d07014-5555-468b-9f26-871d43665fff");
            userPayload.setUsername("user1");
            userPayload.setPassword("pass123");

            List<String> roles = new ArrayList<>();
            roles.add("ROOT");

            userPayload.setRoles(roles);

            ResponseEntity<ResponseWrapper> responseWrapperResponseEntity = userController.userRegister(userPayload);

        } catch (ControllerException e) {
            assertThat(e.getClass(), is(ControllerException.class));
            assertThat(e.getLocalizedMessage(), is("Please use the roles either as USER or ADMIN"));
        }

    }

    @Test
    @Order(27)
    void userRegisterV1NegativeRoles2Empty() {

        try {
            UserPayload userPayload = new UserPayload();
            userPayload.setUuid("08d07014-5555-468b-9f26-871d43665fff");
            userPayload.setUsername("user1");
            userPayload.setPassword("pass123");

            List<String> roles = new ArrayList<>();
            roles.add("ROOT");

            userPayload.setRoles(roles);

            ResponseEntity<ResponseWrapper> responseWrapperResponseEntity = userController.userRegisterV1(userPayload);
            ResponseWrapper responseWrapper = responseWrapperResponseEntity.getBody();

        } catch (ControllerException e) {
            assertThat(e.getClass(), is(ControllerException.class));
            assertThat(e.getLocalizedMessage(), is("Please use the roles either as USER or ADMIN"));
        }

        userController.deleteUserDataByUsername(IMEI);
    }

    @Test
    @Order(9)
    void updateUserData() {
        APIResponse apiResponsedata = (APIResponse) userController.findUserByUsername(IMEI).getBody().getData();
        User user = (User) apiResponsedata.getData();

        UserPayload userPayload = EntityToDTO(user, new UserPayload());
        userPayload.setUsername("update.user");

        ResponseEntity<ResponseWrapper> responseWrapperResponseEntity = userController.updateUserData(userPayload);
        ResponseWrapper responseWrapper = responseWrapperResponseEntity.getBody();
        User userdata = (User) responseWrapper.getData();
        //User userdata = (User) apiResponse.getData();

        assertThat("Update User Data", is(responseWrapper.getMessage()));
        assertThat("update.user", is(userdata.getUsername()));
        assertThat(UUID, is(userdata.getUuid()));

        userPayload.setUsername(IMEI);
        userController.updateUserData(userPayload);

    }

    @Test
    @Order(19)
    void deleteUserDataByUsername() {
        APIResponse apiResponsedata = (APIResponse) userController.findUserByUsername(IMEI).getBody().getData();
        User user = (User) apiResponsedata.getData();

        ResponseEntity<ResponseWrapper> responseWrapperResponseEntity = userController.deleteUserDataByUsername(user.getUsername());
        ResponseWrapper responseWrapper = responseWrapperResponseEntity.getBody();
        APIResponse apiResponse = (APIResponse) responseWrapper.getData();
        User userdata = (User) apiResponse.getData();

        assertThat("Delete User Data", is(responseWrapper.getMessage()));
        assertThat(IMEI, is(userdata.getUsername()));
        assertThat(UUID, is(userdata.getUuid()));

    }

    @Test
    @Order(10)
    void findUserByUsername() {
        APIResponse apiResponsedata = (APIResponse) userController.findUserByUsername(IMEI).getBody().getData();
        User user = (User) apiResponsedata.getData();

        assertThat("User data was found", is(apiResponsedata.getMessage()));
        assertThat(IMEI, is(user.getUsername()));
        assertThat(UUID, is(user.getUuid()));
    }

    @Test
    @Order(11)
    void getAllUsers() {
        Pageable pageable = PageRequest.of(0, 5);
        APIResponse apiResponsedata = (APIResponse) userController.getAllUsers(pageable).getBody().getData();
        List<User> users = (List<User>) apiResponsedata.getData();

        assertThat("Users data was founds", is(apiResponsedata.getMessage()));
        assertThat(IMEI, is(users.get(0).getUsername()));
        assertThat(UUID, is(users.get(0).getUuid()));
    }


    @Test
    @Order(12)
    void findUserByUUID() {
        APIResponse apiResponsedata = (APIResponse) userController.findUserByUUID(UUID).getBody().getData();
        User user = (User) apiResponsedata.getData();

        assertThat("User data is found", is(apiResponsedata.getMessage()));
        assertThat(IMEI, is(user.getUsername()));
        assertThat(UUID, is(user.getUuid()));
    }

    @Test
    @Order(13)
    void isValidUUID() {
        UserPayload userPayload = createUser();
        boolean isValid = userController.isValid(userPayload.getUuid());

        assertThat(true, is(isValid));
    }

    @Test
    @Order(14)
    void isValidNegativeWrongUUIDFormat() {
        boolean isValid = userController.isValid("08d07014-5555777-468b-9f26-871d43665fde");

        assertThat(false, is(isValid));
    }

    @Test
    @Order(15)
    void setCovid19Status() {
        APIResponse apiResponsedata = (APIResponse) userController.findUserByUUID(UUID).getBody().getData();
        User user = (User) apiResponsedata.getData();
        CovidPayload covidPayload = new CovidPayload(user.getUuid(), "John Lennon", "085287234827");
        APIResponse apiResponse = (APIResponse) userController.setCovid19Status(covidPayload).getBody().getData();
        UserProfilePayload userResponse = (UserProfilePayload) apiResponse.getData();
        assertThat("Covid Status for The User has been updated", is(apiResponse.getMessage()));
        assertThat(IMEI, is(userResponse.getUser().getUsername()));
        assertThat(UUID, is(userResponse.getUser().getUuid()));
    }

    @Test
    @Order(16)
    void setCovid19StatusNegativePhone() {
        APIResponse apiResponsedata = (APIResponse) userController.findUserByUUID(UUID).getBody().getData();
        User user = (User) apiResponsedata.getData();
        CovidPayload covidPayload = new CovidPayload(user.getUuid(), "John Lennon", "08528723");
        ResponseWrapper responseWrapper = (ResponseWrapper) userController.setCovid19Status(covidPayload).getBody();

        assertThat("Please use the correct mobile phone format e.x: 085287234598", is(responseWrapper.getMessage()));
    }

    @Test
    @Order(17)
    void setCovid19StatusNegativeUUIDNOTFOUND() {
        CovidPayload covidPayload = new CovidPayload("08d07111-5555-468b-9f26-871d43665fff", "John Lennon", "08528723");
        ResponseWrapper responseWrapper = (ResponseWrapper) userController.setCovid19Status(covidPayload).getBody();

        assertThat("Please use the correct mobile phone format e.x: 085287234598", is(responseWrapper.getMessage()));
    }

    @Test
    @Order(18)
    void saveScannedResult(){
        APIResponse apiResponsedata = (APIResponse) userController.findUserByUUID(UUID).getBody().getData();
        User user = (User) apiResponsedata.getData();

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
        ResponseWrapper responseWrapper = userController.saveScannedResult(scannedResultPayload).getBody();
        List<UserDevice> userDeviceList = (List<UserDevice>) responseWrapper.getData();

        assertThat("Scanned Result has been saved successfully", is(responseWrapper.getMessage()));
        assertThat(date1, is(userDeviceList.get(0).getFoundDate()));

    }

    public User DTOToEntity(User user, UserPayload userPayload){

        user.setUsername(userPayload.getUsername());
        user.setRoles(userPayload.getRoles());
        user.setPassword(userPayload.getPassword());
        user.setUuid(userPayload.getUuid());
        user.setEnabled(userPayload.isEnabled());

        return user;
    }

    public UserPayload EntityToDTO(User user, UserPayload userPayload){

        userPayload.setUsername(user.getUsername());
        userPayload.setRoles(user.getRoles());
        userPayload.setPassword(user.getPassword());
        userPayload.setUuid(user.getUuid());
        userPayload.setEnabled(user.isEnabled());

        return userPayload;
    }
}
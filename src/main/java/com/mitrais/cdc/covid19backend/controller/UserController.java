/**
 * <h1>User Controller</h1>
 * Class to create API Controller for User related activities
 * like UserRegistration, User delete, Update and read user data
 * as well.
 *
 * @author Syarif Hidayat
 * @version 1.0
 * @since 2019-08-20
 * */
package com.mitrais.cdc.covid19backend.controller;

import com.mitrais.cdc.covid19backend.entity.User;
import com.mitrais.cdc.covid19backend.entity.UserDevice;
import com.mitrais.cdc.covid19backend.payload.*;
import com.mitrais.cdc.covid19backend.service.impl.AuthenticationService;
import com.mitrais.cdc.covid19backend.service.impl.UserDeviceService;
import com.mitrais.cdc.covid19backend.service.impl.UserProfileServices;
import com.mitrais.cdc.covid19backend.service.impl.UserServices;
import com.mitrais.cdc.covid19backend.utility.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController extends CrossOriginController{

    private UserServices userService;
    private AuthenticationService authenticationService;
    private UserDeviceService userDeviceService;
    private UserProfileServices userProfileServices;

    private static final  String regexp = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}";
    private static final  String PLEASE_INSERT_THE_EMPTY_FIELD_USERNAME_IS_MANDATORY = "Please insert the empty field, username is mandatory";
    private static final  String PLEASE_INSERT_THE_EMPTY_FIELD_UUID_IS_MANDATORY = "Please insert the empty field, UUID is mandatory";
    private static final  String PLEASE_INSERT_THE_EMPTY_FIELD_PASSWORD_IS_MANDATORY = "Please insert the empty field, password is mandatory";
    private static final  String PLEASE_INSERT_THE_EMPTY_FIELD_ROLES_IS_MANDATORY = "Please insert the empty field, roles is mandatory";
    private static final  String PLEASE_USE_THE_ROLES_EITHER_AS_USER_OR_ADMIN = "Please use the roles either as USER or ADMIN";
    private static final  String YOUR_USERNAME_IS_NOT_AVAILABLE_PLEASE_FIND_THE_NEW_ONE = "Your username is not available, please find the new one";
    private static final  String YOUR_UUID_HAS_BEEN_REGISTERED_PLEASE_REGISTER_WITH_THE_NEW_ONE = "Your UUID has been registered, please register with the new one";
    private static final  String PLEASE_USE_THE_VALID_UUID = "Please use the valid UUID";
    private static final  String USER_HAS_BEEN_REGISTERED_SUCCESSFULLY = "User has been registered successfully";
    private static final  String USER_REGISTRATION_IS_FAILED = "User registration is failed";
    private static final  String YOU_HAVE_LOGIN_SUCCESSFULLY = "You have login successfully";
    private static final  String GET_TOKEN_OR_LOGIN_PROCESS_IS_FAILED = "Get Token or login process is failed";
    private static final  String YOU_HAVE_BEEN_REGISTERED_AND_GET_TOKEN_SUCCESSFULLY = "You have been registered and get token successfully";

    public UserController(UserServices userService, AuthenticationService authenticationService, UserDeviceService userDeviceService, UserProfileServices userProfileServices) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.userDeviceService = userDeviceService;
        this.userProfileServices = userProfileServices;
    }

    /**
     * This method will be used to Create User,
     * For successful user creation, the system will send email
     * that contain link to activate User.
     *
     * @param userPayload
     * @return ResponseEntity that contain text confirmation to check email.
     */
    @PostMapping("/user/register-v1")
    public ResponseEntity<ResponseWrapper> userRegisterV1(@Valid @RequestBody UserPayload userPayload){
        String password=userPayload.getPassword();
        User userdata = new User();
        User user = dtoToEntity(userdata, userPayload);


        if(user.getRoles().get(0).isEmpty()){
            throw new ControllerException(PLEASE_INSERT_THE_EMPTY_FIELD_ROLES_IS_MANDATORY);
        }else {
            if(!user.getRoles().get(0).equals("USER") && !user.getRoles().get(0).equals("ADMIN")){
                throw new ControllerException(PLEASE_USE_THE_ROLES_EITHER_AS_USER_OR_ADMIN);
            }
        }

        if(user.getUsername().isEmpty()){
            throw new ServerErrorException(PLEASE_INSERT_THE_EMPTY_FIELD_USERNAME_IS_MANDATORY);
        }

        if(user.getPassword().isEmpty()){
            throw new ServerErrorException(PLEASE_INSERT_THE_EMPTY_FIELD_PASSWORD_IS_MANDATORY);
        }

        if(userService.findUserByUsername(user.getUsername()).getMessage().equals("User data was found")){
            throw new EntityExistsException(YOUR_USERNAME_IS_NOT_AVAILABLE_PLEASE_FIND_THE_NEW_ONE);
        }

        if(userService.findByUuid(user.getUuid()).getMessage().equals("User data is found")){
            throw new EntityExistsException(YOUR_UUID_HAS_BEEN_REGISTERED_PLEASE_REGISTER_WITH_THE_NEW_ONE);
        }

        if(!isValid(user.getUuid())){
            throw new ServerErrorException(PLEASE_USE_THE_VALID_UUID);
        }

        if(!userService.userRegistration(user).getMessage().equals(USER_HAS_BEEN_REGISTERED_SUCCESSFULLY)){
            throw new ServerErrorException(USER_REGISTRATION_IS_FAILED);
        }

        AuthenticationPayload authenticationPayload = new AuthenticationPayload(user.getUsername(), password);
        LoginResponse loginResponse = authenticationService.login(authenticationPayload);

        if(!loginResponse.getMessage().equals(YOU_HAVE_LOGIN_SUCCESSFULLY)){
            throw new ServerErrorException(GET_TOKEN_OR_LOGIN_PROCESS_IS_FAILED);
        }

        return ResponseEntity.ok(new Utility(YOU_HAVE_BEEN_REGISTERED_AND_GET_TOKEN_SUCCESSFULLY, loginResponse).getResponseData());
    }

    /**
     * This method will be used to Create User,
     * For successful user creation, the system will send email
     * that contain link to activate User.
     *
     * @param userPayload
     * @return ResponseEntity that contain text confirmation to check email.
     */
    @PostMapping("/user/register")
    public ResponseEntity<ResponseWrapper> userRegister(@Valid @RequestBody UserPayload userPayload){
        String password=userPayload.getPassword();
        User userdata = new User();
        User user = dtoToEntity(userdata, userPayload);

        if(user.getRoles().get(0).isEmpty()){
            throw new ControllerException(PLEASE_INSERT_THE_EMPTY_FIELD_ROLES_IS_MANDATORY);
        }else {
            if(!user.getRoles().get(0).equals("USER") && !user.getRoles().get(0).equals("ADMIN")){
                throw new ControllerException(PLEASE_USE_THE_ROLES_EITHER_AS_USER_OR_ADMIN);
            }
        }

        if(user.getUsername().isEmpty()){
            throw new ServerErrorException(PLEASE_INSERT_THE_EMPTY_FIELD_USERNAME_IS_MANDATORY);
        }

        if(user.getPassword().isEmpty()){
            throw new ServerErrorException(PLEASE_INSERT_THE_EMPTY_FIELD_PASSWORD_IS_MANDATORY);
        }

        if(userService.findUserByUsername(user.getUsername()).getMessage().equals("User data was found")){
            throw new EntityExistsException(YOUR_USERNAME_IS_NOT_AVAILABLE_PLEASE_FIND_THE_NEW_ONE);
        }

        if(userService.findByUuid(user.getUuid()).getMessage().equals("User data is found")){
            throw new EntityExistsException(YOUR_UUID_HAS_BEEN_REGISTERED_PLEASE_REGISTER_WITH_THE_NEW_ONE);
        }

        if(!isValid(user.getUuid())){
            throw new ServerErrorException(PLEASE_USE_THE_VALID_UUID);
        }

        if(!userService.userRegistration(user).getMessage().equals(USER_HAS_BEEN_REGISTERED_SUCCESSFULLY)){
            throw new ServerErrorException(USER_REGISTRATION_IS_FAILED);
        }

        return ResponseEntity.ok(new Utility("You have been registered successfully", user).getResponseData());
    }

    /**
     * This method will be used to update user data,
     * and will return the updated user data for successfully update process.
     *
     * @param userPayload
     * @return Updated User Data
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PatchMapping("/user/update")
    public ResponseEntity<ResponseWrapper> updateUserData(@Valid @RequestBody UserPayload userPayload){

        User userdata = new User();
        User user = dtoToEntity(userdata, userPayload);

        if(user.getRoles().get(0).isEmpty()){
            throw new ControllerException(PLEASE_INSERT_THE_EMPTY_FIELD_ROLES_IS_MANDATORY);
        }else {
            if(!user.getRoles().get(0).equals("USER") && !user.getRoles().get(0).equals("ADMIN")){
                throw new ControllerException(PLEASE_USE_THE_ROLES_EITHER_AS_USER_OR_ADMIN);
            }
        }

        if(!isValid(user.getUuid())){
            throw new ServerErrorException(PLEASE_USE_THE_VALID_UUID);
        }

        APIResponse apiResponse = userService.updateUserData(user);
        if(apiResponse.getMessage().equals("Update user data was failed, username is not available")){
            throw new EntityExistsException(YOUR_USERNAME_IS_NOT_AVAILABLE_PLEASE_FIND_THE_NEW_ONE);
        }

        return ResponseEntity.ok(new Utility("Update User Data", apiResponse.getData()).getResponseData());
    }

    /**
     * This method will be used to delete user data,
     * and will return deleted user data for successfully deletion process.
     *
     * @param username
     * @return Deleted user data
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/user/delete/{username}")
    public ResponseEntity<ResponseWrapper> deleteUserDataByUsername(@PathVariable("username") String username){

        APIResponse apiResponse = userService.deleteUserByUsername(username);

        if(!apiResponse.isSuccess()){
            throw new ServerErrorException("Delete user data was failed");
        }

        return ResponseEntity.ok(new Utility("Delete User Data", apiResponse).getResponseData());
    }


    /**
     * This method will be used to find user data based on the given username
     *
     * @param username
     * @return User data
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/user/username/{username}")
    public ResponseEntity<ResponseWrapper> findUserByUsername(@PathVariable("username") String username){

        APIResponse apiResponse = userService.findUserByUsername(username);

        if(!apiResponse.isSuccess()){
            if(apiResponse.getMessage().equals("User data was not found")){
                throw new EntityNotFoundException("User data was not found");
            }else{
                throw new ServerErrorException("Internal Server Error");
            }
        }
        return ResponseEntity.ok(new Utility("Find User Data By Username", apiResponse).getResponseData());
    }

    /**
     * This method will be used to get All User Data
     *
     * @return User Data list
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/users")
    public ResponseEntity<ResponseWrapper> getAllUsers(Pageable pageable){

        return ResponseEntity.ok(new Utility("Find User Data", userService.getAllUsers(pageable)).getResponseData());
    }



    /**
     * This method will be used to find user data based on the given username
     *
     * @param uuid
     * @return User data
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/user/uuid/{uuid}")
    public ResponseEntity<ResponseWrapper> findUserByUUID(@PathVariable("uuid") String uuid){

        APIResponse apiResponse =  userService.findByUuid(uuid);

        if(!apiResponse.isSuccess()){
            throw new EntityNotFoundException("User data for that UUID was not found");
        }

        return ResponseEntity.ok(new Utility("Find User Data By UUID",apiResponse).getResponseData());
    }

    public boolean isValid(String uuid) {
        return uuid.matches(regexp);
    }

    public User dtoToEntity(User user, UserPayload userPayload){

        user.setUsername(userPayload.getUsername());
        user.setRoles(userPayload.getRoles());
        user.setPassword(userPayload.getPassword());
        user.setUuid(userPayload.getUuid());
        user.setEnabled(userPayload.isEnabled());

        return user;
    }

    /**
     * This method will be used to update the covid19 status for the given uuid
     *
     * @param covidPayload
     * @return User data
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping("/user/setcovidstatus")
    public ResponseEntity<ResponseWrapper> setCovid19Status(@Valid @RequestBody CovidPayload covidPayload){

        if(!validatePhoneNumber(covidPayload.getMobilePhone()))
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Utility("Please use the correct mobile phone format e.x: 085287234598", null).getResponseData());
        }

        if(!valdiateName(covidPayload.getName())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Utility("Please use letters only for name e.x: John Lennon", null).getResponseData());
        }

        User user = (User) userService.findByUuid(covidPayload.getUuid()).getData();

        UserProfilePayload userProfilePayload = new UserProfilePayload();
        userProfilePayload.setName(covidPayload.getName());
        userProfilePayload.setCovid19Status("ODP");
        userProfilePayload.setMobilePhone(covidPayload.getMobilePhone());
        userProfilePayload.setUser(user);

        APIResponse apiResponse = userProfileServices.setCovidStatus(userProfilePayload);

        if(!apiResponse.isSuccess()){
            if(apiResponse.getMessage().equals("User data for that uuid is not found")){
                throw new NullPointerException("User data for that uuid is not found");
            }else if(apiResponse.getMessage().equals("Covid Status for The User failed to updated")){
                throw new NullPointerException("Covid Status for The User failed to updated");
            }

        }

        return ResponseEntity.ok(new Utility("Set Covid19 Status has been done successfully", apiResponse).getResponseData());
    }

    private static boolean validatePhoneNumber(String phoneNo) {

        int phoneLength = phoneNo.length();
        if((phoneLength >= 11 && phoneLength <= 13) && phoneNo.matches("\\d{"+phoneLength+"}")){
            return true;
        }else {
            return false;
        }
    }

    private static boolean valdiateName(String name){
        String[] buffer = name.split(" ");

        for(String strName : buffer){
            if(!((strName.length() > 0 && strName.length() <= 50) && strName.matches("^[a-zA-Z]*$"))){
                return false;
            }
        }

        return true;
    }

    /**
     * This method will be used to save the scanned result from user device
     *
     * @param scannedResultPayload
     * @return User data
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping("/user/save-scanned-result")
    public ResponseEntity<ResponseWrapper> saveScannedResult(@Valid @RequestBody ScannedResultPayload scannedResultPayload){

        APIResponse apiResponse = userDeviceService.saveScannedResult(scannedResultPayload);

        if(!apiResponse.getMessage().equals("Scanned result has been saved successfully")){
            if(apiResponse.getMessage().equals("data is empty")){
                throw new ServerErrorException("Data that want to save is empty or not passed data validity checking, please fill the mandatry field foundate, sender and uuid");
            }else{
                throw new ServerErrorException("Failed to save scanned result");
            }

        }

        return ResponseEntity.ok(new Utility("Scanned Result has been saved successfully", apiResponse.getData()).getResponseData());
    }



}
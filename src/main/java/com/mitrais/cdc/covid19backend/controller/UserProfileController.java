package com.mitrais.cdc.covid19backend.controller;

import com.mitrais.cdc.covid19backend.config.Decrypt;
import com.mitrais.cdc.covid19backend.config.Encrypt;
import com.mitrais.cdc.covid19backend.entity.User;
import com.mitrais.cdc.covid19backend.entity.UserProfile;
import com.mitrais.cdc.covid19backend.payload.APIResponse;
import com.mitrais.cdc.covid19backend.payload.ResponseWrapper;
import com.mitrais.cdc.covid19backend.payload.UserProfilePayload;
import com.mitrais.cdc.covid19backend.service.impl.UserProfileServices;
import com.mitrais.cdc.covid19backend.service.impl.UserServices;
import com.mitrais.cdc.covid19backend.utility.Utility;
import io.cucumber.java.bs.A;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserProfileController {

    private UserProfileServices userProfileServices;
    private UserServices userServices;
    private static final  String regexp = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}";


    public UserProfileController(UserProfileServices userProfileServices, UserServices userServices) {
        this.userProfileServices = userProfileServices;
        this.userServices = userServices;
    }


    @PostMapping("/user/profile")
    public ResponseEntity<ResponseWrapper> createUserProfile(@Valid @RequestBody UserProfilePayload userProfilePayload){
        log.info("User Profile Payload Data:"+userProfilePayload.getUser().getUuid());
        APIResponse response = userServices.findByUuid(userProfilePayload.getUser().getUuid());

        if(!response.isSuccess()){
            throw new EntityNotFoundException("User uuid has been not registered.");
        }

        APIResponse searchProfileResponse = userProfileServices.findUserProfileByUUID(userProfilePayload.getUser().getUuid());
        if(searchProfileResponse.isSuccess()){
            throw new EntityExistsException("User profile for the given UUID is already exist, please use PATCH mode to update user profile");
        }

        UserProfile userProfile = dtoToEntity(new UserProfile(), userProfilePayload);
        APIResponse apiResponse = userProfileServices.createUserProfile(userProfile);

        if(!isValid(userProfilePayload.getUser().getUuid()) && !valdiateName(userProfilePayload.getName()) && !validatePhoneNumber(userProfilePayload.getMobilePhone())){
            throw new ServerErrorException("Please user the valid uuid!");
        }

        if(!valdiateName(userProfilePayload.getName())){
            throw new ServerErrorException("Please use the letter only for the name");
        }

        if(!validatePhoneNumber(userProfilePayload.getMobilePhone())){
            throw new ServerErrorException("Please use the valid phone number!");
        }


        if(apiResponse.isSuccess()){
            return ResponseEntity.ok(new Utility("User Profile has been created successfully",apiResponse).getResponseData());
        }else {
            throw new ServerErrorException("User Profile creation is failed");
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PatchMapping("/user/profile")
    public ResponseEntity<ResponseWrapper> updateUserProfile(@Valid @RequestBody UserProfilePayload userProfilePayload){

        if(!isValid(userProfilePayload.getUser().getUuid()) || userProfilePayload.getUser().getUuid().isEmpty()){
            throw new ServerErrorException("Please use the valid uuid!");
        }

        if(!valdiateName(userProfilePayload.getName())){
            throw new ServerErrorException("Please use the letter only for the name and greater than 2 character");
        }

        if(!valdiateName(userProfilePayload.getCovid19Status())){
            throw new ServerErrorException("Please use the letter only for the covid status");
        }

        if(!validatePhoneNumber(userProfilePayload.getMobilePhone())){
            throw new ServerErrorException("Please use the valid phone number!");
        }

        APIResponse searchResponse = userProfileServices.findUserProfileByUUID(userProfilePayload.getUser().getUuid());

        if(!searchResponse.isSuccess()){
            throw new EntityNotFoundException("UUID that the data want to update is not found");
        }


        UserProfile userProfile = dtoToEntity(new UserProfile(), userProfilePayload);
        APIResponse apiResponse = userProfileServices.updateUserProfile(userProfile);

        if(apiResponse.isSuccess()){
            return ResponseEntity.ok(new Utility("User Profile has been updated successfully",apiResponse).getResponseData());
        }else {
            throw new EntityNotFoundException("User Profile that want to update is not found");
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/user/profile/{uuid}")
    public ResponseEntity<ResponseWrapper> findUserProfileByUUID(@PathVariable("uuid") String uuid){

        UserProfile userProfile = null;
        try{
            userProfile = userProfileServices.findUserProfileDataByUUID(uuid);
        }catch (NullPointerException e){
            throw new EntityNotFoundException("User Profile is not found for the given uuid");
        }

        return ResponseEntity.ok(new Utility("User Profile is found",userProfile).getResponseData());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper> deleteUserProfile(@PathVariable("uuid") String uuid){
        APIResponse apiResponse = userProfileServices.findUserProfileByUUID(uuid);

        if(apiResponse.isSuccess()){
            APIResponse deleteResponse = userProfileServices.deleteUserProfileByUUID(uuid);
            if(deleteResponse.isSuccess()){
                return ResponseEntity.ok(new Utility("User Profile is delete successfully",deleteResponse).getResponseData());
            }else {
                throw new ServerErrorException("User Deletion is failed");
            }
        }else {
            throw new EntityNotFoundException("User Profile that want to delete is not found");
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/user/profiles")
    public ResponseEntity<ResponseWrapper> findAllUserProfiles(Pageable pageable){
       /* APIResponse apiResponse = userProfileServices.getAllUserProfile(pageable);
        List<UserProfile> userProfileList = (List<UserProfile>) apiResponse.getData();*/
        List<UserProfile> userProfileList = userProfileServices.getAllUserProfileData(pageable);

        if(userProfileList.size() > 0){
            return ResponseEntity.ok(new Utility("User Profiles is found",userProfileList).getResponseData());
        }else {
            throw new EntityNotFoundException("Data is not found");
        }

    }

    @Validated
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/user/profile/status")
    public ResponseEntity<ResponseWrapper> findAllCovidStatusUser(@RequestParam("status") @NotEmpty @NotBlank String status, Pageable pageable){

        log.info("Status: "+status);
        if(status.isEmpty() || status == null){
            log.info("Status empty");
            throw new ServerErrorException("Please set status, either ODP or Healthy for covid status");
        }

        if(!status.equals("ODP") && !status.equals("Healthy")){
            throw new ServerErrorException("Please use ODP or Healthy for covid status");
        }

        List<UserProfile> userProfileList = (List<UserProfile>) userProfileServices.getAllCovidStatusUser(pageable, status);
        if(userProfileList.size() > 0){
            return ResponseEntity.ok(new Utility("User Profiles for status " + status+ " is found", userProfileList).getResponseData());
        }else {
            throw new EntityNotFoundException("User Profiles for status " + status+ " is not found");
        }
    }

    @GetMapping("/user/profile/contacted")
    public ResponseEntity<ResponseWrapper> findAllHealthyUserContacted(Pageable pageable){
        List<UserProfile> userProfiles = userProfileServices.getAllHealthyUserContactedODP(pageable);

        if(userProfiles.size() > 0){
            return ResponseEntity.ok(new Utility("Healthy User profile that contacted with ODP is found.", userProfiles).getResponseData());
        }else {
            throw new EntityNotFoundException("Healthy User profile that contacted with ODP is not found.");
        }
    }

    public UserProfile dtoToEntity(UserProfile userProfile, UserProfilePayload userProfilePayload){
        log.info("DtoToEntity....");
        if(!userProfilePayload.getToken().isEmpty()){
            userProfile.setToken(userProfilePayload.getToken());
        }

        User user = (User) userServices.findByUuid(userProfilePayload.getUser().getUuid()).getData();

        userProfile.setUser(user);
        userProfile.setMobilePhone(userProfilePayload.getMobilePhone());
        userProfile.setName(userProfilePayload.getName());
        userProfile.setEmail(userProfilePayload.getEmail());
        userProfile.setCovid19Status(userProfilePayload.getCovid19Status());

        return userProfile;
    }

    public boolean isValid(String uuid) {
        return uuid.matches(regexp);
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
            if(!((strName.length() > 2 && strName.length() <= 50) && strName.matches("^[a-zA-Z]*$"))){
                return false;
            }
        }

        return true;
    }
}

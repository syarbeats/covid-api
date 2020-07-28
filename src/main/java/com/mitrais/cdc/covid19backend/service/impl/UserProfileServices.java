package com.mitrais.cdc.covid19backend.service.impl;

import com.mitrais.cdc.covid19backend.config.Decrypt;
import com.mitrais.cdc.covid19backend.config.Encrypt;
import com.mitrais.cdc.covid19backend.entity.User;
import com.mitrais.cdc.covid19backend.entity.UserDevice;
import com.mitrais.cdc.covid19backend.entity.UserProfile;
import com.mitrais.cdc.covid19backend.payload.APIResponse;
import com.mitrais.cdc.covid19backend.payload.UserProfilePayload;
import com.mitrais.cdc.covid19backend.repository.UserDeviceRepository;
import com.mitrais.cdc.covid19backend.repository.UserProfileRepository;
import com.mitrais.cdc.covid19backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.server.ServerErrorException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserProfileServices {

    private static final String ODP = "ODP";

    private UserRepository userRepository;
    private UserProfileRepository userProfileRepository;
    private UserDeviceRepository userDeviceRepository;

    public UserProfileServices(UserRepository userRepository, UserProfileRepository userProfileRepository, UserDeviceRepository userDeviceRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.userDeviceRepository = userDeviceRepository;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Encrypt(values = {"name", "mobilePhone"})
    public APIResponse setCovidStatus(UserProfilePayload userProfilePayload){


        if(userProfilePayload.getUser() == null){
            return new APIResponse(false, "User data for that uuid is not found", null);
        }

        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUUID(userProfilePayload.getUser().getUuid());
        UserProfile userProfile = new UserProfile();

        if(userProfileOptional.isPresent()){
            userProfile = userProfileOptional.get();
            userProfile.setCovid19Status(userProfilePayload.getCovid19Status());
            userProfile.setName(userProfilePayload.getName());
            userProfile.setMobilePhone(userProfilePayload.getMobilePhone());
        }else{
            userProfile.setCovid19Status(userProfilePayload.getCovid19Status());
            userProfile.setName(userProfilePayload.getName());
            userProfile.setMobilePhone(userProfilePayload.getMobilePhone());
            userProfile.setUser(userProfilePayload.getUser());
        }

        UserProfile userProfileData =  userProfileRepository.save(userProfile);

        if(userProfileData == null){
            return new APIResponse(false, "Covid Status for The User failed to updated", userProfilePayload);
        }

        return new APIResponse(true, "Covid Status for The User has been updated", userProfilePayload);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public APIResponse findUserProfileByUUID(String uuid){
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUUID(uuid);

        if(userProfileOptional.isPresent()){
            return new APIResponse(true, "User Profile is found", userProfileOptional.get());
        }

        return new APIResponse(false, "User Profile is not found", null);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Decrypt(values={"name", "mobilePhone"})
    public UserProfile findUserProfileDataByUUID(String uuid){
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUUID(uuid);

        if(userProfileOptional.isPresent()){
            return userProfileOptional.get();
        }

        return null;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public APIResponse findUserProfileById(int id){
        Optional<UserProfile> userProfileOptional = userProfileRepository.findById(id);

        if(userProfileOptional.isPresent()){
            return new APIResponse(true, "User Profile is found", userProfileOptional.get());
        }

        return new APIResponse(false, "User Profile is not found", null);
    }

    @Encrypt(values = {"name", "mobilePhone"})
    public APIResponse createUserProfile(UserProfile userProfile){
        log.info("CreateUserProfile Service...:"+userProfile.getUser().getUuid());

        userProfileRepository.save(userProfile);
        return new APIResponse(true, "User Profile has been created successfully", userProfileRepository.save(userProfile));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Encrypt(values = {"name", "mobilePhone"})
    public APIResponse updateUserProfile(UserProfile userProfile){
        log.info("Update user profile services");
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUUID(userProfile.getUser().getUuid());
        log.info("User Profile Data:"+userProfileOptional.get().getName());

        if(userProfileOptional.isPresent()){
            log.info("User profile is found...");
            UserProfile userProfileData = userProfileOptional.get();
            userProfileData.setName(userProfile.getName());
            userProfileData.setCovid19Status(userProfile.getCovid19Status());
            userProfileData.setEmail(userProfile.getEmail());
            userProfileData.setMobilePhone(userProfile.getMobilePhone());
            return new APIResponse(true, "User Profile has been updated successfully", userProfileRepository.save(userProfileData));
        }else{
            log.info("User profile is not found...");
            return new APIResponse(false, "User Profile that want to update is not found", null);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public APIResponse deleteUserProfileByUUID(String uuid){
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUUID(uuid);

        if(userProfileOptional.isPresent()){
            UserProfile userProfile = userProfileOptional.get();
            userProfileRepository.delete(userProfile);
            userProfile.setUser(null);
            return new APIResponse(true, "User Profile has been deleted successfully", userProfile);
        }else {
            return new APIResponse(false, "User Profile that want to delete is not found", null);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public APIResponse getAllUserProfile(Pageable pageable){
        Page<UserProfile> userProfilePage = userProfileRepository.findAll(pageable);
        return new APIResponse(true, "User Profile is found", userProfilePage.getContent());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Decrypt(values = {"name", "mobilePhone"})
    public List<UserProfile> getAllUserProfileData(Pageable pageable){
        Page<UserProfile> userProfilePage = userProfileRepository.findAll(pageable);
        return userProfilePage.getContent();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Decrypt(values = {"name", "mobilePhone"})
    public List<UserProfile> getAllCovidStatusUser(Pageable pageable, String status){
        Page<UserProfile> userProfilePage = userProfileRepository.findAllCovidStatusUser(pageable, status);
        return userProfilePage.getContent();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Decrypt(values = {"name", "mobilePhone"})
    public List<UserProfile> getAllHealthyUserContactedODP(Pageable pageable){
        Page<UserProfile> userProfilePage = userDeviceRepository.findAllHealtyUserContactedODP(pageable);
        return  userProfilePage.getContent();
    }

}

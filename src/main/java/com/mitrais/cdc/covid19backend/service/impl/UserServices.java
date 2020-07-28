/**
 * <h1>UserServices</h1>
 * Class to handle User CRUD.
 *
 * @author Syarif Hidayat
 * @version 1.0
 * @since 2019-08-20
 * */

package com.mitrais.cdc.covid19backend.service.impl;

import com.mitrais.cdc.covid19backend.entity.User;
import com.mitrais.cdc.covid19backend.entity.UserProfile;
import com.mitrais.cdc.covid19backend.payload.APIResponse;
import com.mitrais.cdc.covid19backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserServices {

    private UserRepository userRepository;
    private UserProfileServices userProfileServices;

    public UserServices(UserRepository userRepository, UserProfileServices userProfileServices) {
        this.userRepository = userRepository;
        this.userProfileServices = userProfileServices;
    }

    public APIResponse userRegistration(User user){

        try{
            user.setEnabled(true);
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            log.info("UUID:"+ user.getUuid());

            if(findUserByUsername(user.getUsername()).getMessage().equals("User data was found") || findByUuid(user.getUuid()).getMessage().equals("User data is found")){
                User userdata = (User) findUserByUsername(user.getUsername()).getData();
                log.info("IMEI:"+ userdata.getUsername());
                return new APIResponse(true,"IMEI/UUID has been registered, please register with the new one", null);
            }
            User userdata = userRepository.save(user);
            UserProfile userProfile = new UserProfile();
            userProfile.setUser(userdata);
            userProfile.setMobilePhone("000000000000");
            userProfile.setEmail("");
            userProfile.setCovid19Status("Healthy");
            userProfile.setName("Anonymous");
            userProfileServices.createUserProfile(userProfile);

            return new APIResponse(true,"User has been registered successfully", userdata);
        }catch (Exception e){
            log.info(e.getMessage(), e);

        }

        return new APIResponse(false, "User registration was failed", null);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public APIResponse updateUserData(User user){

        log.info("User Password:"+user.getPassword());
        try{
            Optional<User> optionalUser = userRepository.findByUuid(user.getUuid());
            User userData = null;

            if(optionalUser.isPresent()){
                userData = optionalUser.get();
            }

            if(!user.getPassword().isEmpty()){
                userData.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            }

            userData.setRoles(user.getRoles());
            userData.setUsername(user.getUsername());
            userData.setUuid(user.getUuid());
            userData.setEnabled(user.isEnabled());

            if(userRepository.findByUsername(userData.getUsername()).isPresent()){
                return new APIResponse(false, "Update user data was failed, username is not available", null);
            }

            return new APIResponse(true, "Update user data has been updated successfully", userRepository.save(userData));
        }catch (Exception e){
            log.info(e.getMessage(), e);
        }

        return new APIResponse(false, "Update user data was failed", null);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public APIResponse deleteUserByUsername(String username){
        User userData = null;
        try{
            Optional<User> optionalUser = userRepository.findByUsername(username);

            if(optionalUser.isPresent()){
                userData = optionalUser.get();
            }

            userRepository.delete(userData);
            return new APIResponse(true, "Delete user data has been executed successfully", userData);
        }catch (Exception e){
            log.info(e.getMessage(), e);
        }

        return new APIResponse(false, "Delete user data was failed", userData);
    }

    public APIResponse findUserByUsername(String username){

        try{
            Optional<User> optionalUser = userRepository.findByUsername(username);
            User user = null;

            if(optionalUser.isPresent()){
                user = optionalUser.get();
                return new APIResponse(true, "User data was found", user);
            }else {
                return new APIResponse(false, "User data was not found", null);
            }

        }catch (Exception e){
            log.info(e.getMessage(), e);
        }

        return new APIResponse(false, "Internal System Error", null);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public User findUserByEmail(String email){

        User userdata = null;
        try{
            Optional<User> user = userRepository.findByEmail(email);

            if(user.isPresent() ){
                userdata = user.get();
            }

        }catch (UsernameNotFoundException e){
            throw new UsernameNotFoundException("Username not found");
        }

        return userdata;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public APIResponse getAllUsers(Pageable pageable){

        return new APIResponse(true, "Users data was founds", userRepository.findAll(pageable).getContent());
    }

    public UUID generateUUID(){

        return UUID.randomUUID();
    }

    public APIResponse findByUuid(String uuid){
        Optional<User> optionalUser = userRepository.findByUuid(uuid);

        if(optionalUser.isPresent()){
            return new APIResponse(true, "User data is found", optionalUser.get());
        }

        return new APIResponse(false, "User data is not found", null);
    }

}

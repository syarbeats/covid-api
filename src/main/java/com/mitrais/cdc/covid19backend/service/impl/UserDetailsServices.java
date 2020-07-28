/**
 * <h1>UserDetailServices</h1>
 * Class to get UserDetails data based on the given username.
 *
 * @author Syarif Hidayat
 * @version 1.0
 * @since 2019-08-20
 * */

package com.mitrais.cdc.covid19backend.service.impl;

import com.mitrais.cdc.covid19backend.entity.User;
import com.mitrais.cdc.covid19backend.repository.UserRepository;
import com.mitrais.cdc.covid19backend.utility.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetailsServices implements UserDetailsService {

    private UserRepository userRepository;

    /**
     * This method will be used to get UserDetails data
     * based on the given username.
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> optionalUser = userRepository.findByUsername(username);
        User user = null;

        if(optionalUser.isPresent()){
            user = optionalUser.get();
        }

        return new UserDetails(user);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}

/**
 * <h1>UserDetails</h1>
 * Class to save User Details data that required
 * when authentication process.
 *
 * @author Syarif Hidayat
 * @version 1.0
 * @since 2019-08-20
 * */

package com.mitrais.cdc.covid19backend.utility;

import com.mitrais.cdc.covid19backend.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {

    private User user;

    public UserDetails(User user){
        this.user = user;
    }

    /**
     * This method will be used to get Collection of user's roles.
     *
     * @return will return roles
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<String> roles = user.getRoles();
        List<String> roleList =  new ArrayList<>();

        for(String role : roles) {
            roleList.add("ROLE_"+role);
            log.info("Role:"+ role);
        }

        for(String role : roleList) {
            log.info("Role:"+ role);
        }

        return roleList.stream().map(SimpleGrantedAuthority::new).collect(toList());
    }

    /**
     * This method will be used to get
     * user password
     *
     * @return will return password
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * This method will be used to get
     * username
     *
     * @return will return username
     */
    @Override
    public String getUsername() {
        log.info("Username:"+user.getUsername());
        return user.getUsername();
    }

    /**
     * This method will be used to check
     * if user is not expired.
     *
     * @return will true if user is not expired
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * This method will be used to check
     * if user is not locked
     *
     * @return will return true if user is not locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * This method will be used to check
     * if user's password is not expired
     *
     * @return will return password
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * This method will be used to check
     * user status
     *
     * @return will return true is user enabled True
     */
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}

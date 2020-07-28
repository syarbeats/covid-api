package com.mitrais.cdc.covid19backend.repository;

import com.mitrais.cdc.covid19backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Serializable> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u, UserProfile p WHERE u.userProfile = p AND p.email=:email")
    Optional<User> findByEmail(String email);

    Optional<User> findByUuid(String uuid);
}

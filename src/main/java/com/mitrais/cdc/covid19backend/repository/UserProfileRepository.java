package com.mitrais.cdc.covid19backend.repository;

import com.mitrais.cdc.covid19backend.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Serializable> {
    @Query("SELECT u FROM UserProfile u WHERE u.user.uuid =:uuid")
    Optional<UserProfile> findByUUID(String uuid);

    @Query("SELECT u FROM UserProfile u WHERE u.covid19Status =:status")
    Page<UserProfile> findAllCovidStatusUser(Pageable pageable, String status);

}

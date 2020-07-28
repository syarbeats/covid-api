package com.mitrais.cdc.covid19backend.repository;

import com.mitrais.cdc.covid19backend.entity.UserDevice;
import com.mitrais.cdc.covid19backend.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Serializable> {

    @Query("SELECT distinct p FROM UserProfile p, UserDevice d WHERE p.user.uuid = d.sender")
    Page<UserProfile> findAllHealtyUserContactedODP(Pageable pageable);

    @Query("SELECT u FROM UserDevice u WHERE u.sender = :uuid")
    Page<UserDevice> findAllODPContactedByHealthyUUID(Pageable pageable, String uuid);

    @Query("SELECT u FROM UserDevice u WHERE u.user.uuid = :uuid")
    Page<UserDevice> findAllUserContactedWithODPUUID(Pageable pageable, String uuid);
}

package com.mitrais.cdc.covid19backend.service.impl;

import com.mitrais.cdc.covid19backend.entity.User;
import com.mitrais.cdc.covid19backend.entity.UserDevice;
import com.mitrais.cdc.covid19backend.payload.APIResponse;
import com.mitrais.cdc.covid19backend.payload.ScannedResultPayload;
import com.mitrais.cdc.covid19backend.payload.UserDevicePayload;
import com.mitrais.cdc.covid19backend.repository.UserDeviceRepository;
import com.mitrais.cdc.covid19backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserDeviceService {

    private UserDeviceRepository userDeviceRepository;
    private UserRepository userRepository;
    private static final  String regexp = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}";

    public UserDeviceService(UserDeviceRepository userDeviceRepository, UserRepository userRepository) {
        this.userDeviceRepository = userDeviceRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public APIResponse saveScannedResult(@Valid ScannedResultPayload scannedResultPayload) {
        List<UserDevicePayload> userDevicePayloads = scannedResultPayload.getUserDevices();
        List<UserDevice> userDevices = new ArrayList<>();

        for(UserDevicePayload userDevicePayload : userDevicePayloads ){
            if(isValid(userDevicePayload.getUser().getUuid()) && isValid(userDevicePayload.getSender())){
                if(isUUIDRegistered(userDevicePayload.getUser().getUuid()) && isUUIDRegistered(userDevicePayload.getSender()) && !userDevicePayload.getFoundDate().toString().trim().equals("")){
                    userDevices.add(dtoToEntity(new UserDevice(), userDevicePayload));
                }
            }
        }

        if(userDevices.size() != 0){
            userDeviceRepository.saveAll(userDevices);
        }else {
            return new APIResponse(false, "data is empty", userDevices);
        }


        return new APIResponse(true, "Scanned result has been saved successfully", userDevices);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public APIResponse getAllODPContactedWithHeathyUUID(Pageable pageable, String uuid){
        List<UserDevice> userDeviceList = userDeviceRepository.findAllODPContactedByHealthyUUID(pageable, uuid).getContent();
        return new APIResponse(true, "ODP List that contacted for given Healthy UUID is found", userDeviceList);
    }

    public APIResponse getAllUserDevice(Pageable pageable){
        return new APIResponse(true, "All User Devices data from scanning result is found", userDeviceRepository.findAll(pageable).getContent());
    }

    public APIResponse getAllUserContactedByODPUUID(Pageable pageable, String uuid){
        return new APIResponse(true, "All User Devices that contacted with ODP UUID is found", userDeviceRepository.findAllUserContactedWithODPUUID(pageable, uuid).getContent());
    }

    public UserDevice dtoToEntity(UserDevice userDevice, UserDevicePayload userDevicePayload){
        userDevice.setId(userDevicePayload.getId());
        userDevice.setFoundDate(userDevicePayload.getFoundDate());
        userDevice.setSender(userDevicePayload.getSender());
        userDevice.setUser(userDevicePayload.getUser());

        return userDevice;
    }

    public boolean isUUIDRegistered(String uuid){
        Optional<User> optionalUser = userRepository.findByUuid(uuid);
        if (optionalUser.isPresent()){
            return true;
        }

        return false;
    }

    public boolean isValid(String uuid) {
        return uuid.matches(regexp);
    }

}

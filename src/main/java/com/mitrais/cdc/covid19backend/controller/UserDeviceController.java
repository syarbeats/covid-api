package com.mitrais.cdc.covid19backend.controller;

import com.mitrais.cdc.covid19backend.entity.UserDevice;
import com.mitrais.cdc.covid19backend.payload.ResponseWrapper;
import com.mitrais.cdc.covid19backend.repository.UserDeviceRepository;
import com.mitrais.cdc.covid19backend.service.impl.UserDeviceService;
import com.mitrais.cdc.covid19backend.utility.Utility;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerErrorException;

import javax.persistence.EntityNotFoundException;
import java.util.List;


@RestController
@RequestMapping("/api")
public class UserDeviceController {

    private UserDeviceService userDeviceService;

    public UserDeviceController(UserDeviceService userDeviceService) {
        this.userDeviceService = userDeviceService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/user/devices")
    public ResponseEntity<ResponseWrapper> getAllUserDevices(Pageable pageable){
        List<UserDevice> userDeviceList = (List<UserDevice>) userDeviceService.getAllUserDevice(pageable).getData();

        if(userDeviceList.size() > 0){
            return ResponseEntity.ok(new Utility("User Devices is found", userDeviceList).getResponseData());
        }else {
            throw new EntityNotFoundException("Data is not found");
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/user/devices/odp/{uuid}")
    public ResponseEntity<ResponseWrapper> getAllODPContactedWithHeathyUUID(@PathVariable("uuid")String uuid, Pageable pageable){
        List<UserDevice> userDeviceList = (List<UserDevice>) userDeviceService.getAllODPContactedWithHeathyUUID(pageable, uuid).getData();

        if(userDeviceList.size() > 0){
            return ResponseEntity.ok(new Utility("ALL ODP User Devices that contacted with the given healthy uuid is found", userDeviceList).getResponseData());
        }else {
            throw new EntityNotFoundException("Data is not found");
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/user/devices/healty/{uuid}")
    public ResponseEntity<ResponseWrapper> getAllUserContactedByODPUUID(@PathVariable("uuid")String uuid, Pageable pageable){
        List<UserDevice> userDeviceList = (List<UserDevice>) userDeviceService.getAllUserContactedByODPUUID(pageable, uuid).getData();

        if(userDeviceList.size() > 0){
            return ResponseEntity.ok(new Utility("User Devices that contacted with the given ODP UUID is found", userDeviceList).getResponseData());
        }else {
            throw new EntityNotFoundException("User Devices that contacted with the given ODP UUID is not found");
        }
    }

}

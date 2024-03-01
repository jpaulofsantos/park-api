package com.jp.parkapi.web.controller;

import com.jp.parkapi.entities.ClientSpace;
import com.jp.parkapi.services.ParkingService;
import com.jp.parkapi.web.dto.ParkingCreateDTO;
import com.jp.parkapi.web.dto.ParkingResponseDTO;
import com.jp.parkapi.web.dto.mapper.ClientMapper;
import com.jp.parkapi.web.dto.mapper.ClientSpaceMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("api/v1/parkings")
public class ParkingController {

    @Autowired
    ParkingService parkingService;

    @PostMapping("/check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingResponseDTO> checkIn(@RequestBody @Valid ParkingCreateDTO parkingCreateDTO) {
        ClientSpace clientSpace = ClientSpaceMapper.toClientSpace(parkingCreateDTO);
        parkingService.checkIn(clientSpace);

        ParkingResponseDTO parkingResponseDTO = ClientSpaceMapper.toDto(clientSpace);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{receipt}")
                .buildAndExpand(clientSpace.getReceipt())
                .toUri();
        return ResponseEntity.created(location).body(parkingResponseDTO);
    }
}

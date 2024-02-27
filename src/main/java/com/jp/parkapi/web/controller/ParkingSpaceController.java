package com.jp.parkapi.web.controller;

import com.jp.parkapi.entities.ParkingSpace;
import com.jp.parkapi.services.ParkingSpaceService;
import com.jp.parkapi.web.dto.ParkingSpaceCreateDTO;
import com.jp.parkapi.web.dto.ParkingSpaceResponseDTO;
import com.jp.parkapi.web.dto.mapper.ParkingSpaceMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Parking Spaces", description = "Contém as operações relativas aos recursos de cadastro, inserção e leitura de vagas.")
@RestController
@RequestMapping(value = "api/v1/parkingspaces")
public class ParkingSpaceController {

    @Autowired
    private ParkingSpaceService parkingSpaceService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> insertParkingSpace(@Valid @RequestBody ParkingSpaceCreateDTO parkingSpaceCreateDTO) { //alterando a forma de rertorno (diferente do retorno usando o responseDTO
        ParkingSpace parkingSpace = parkingSpaceService.insertSpace(ParkingSpaceMapper.toParkingSpace(parkingSpaceCreateDTO));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{codigo}")
                .buildAndExpand(parkingSpace.getCode())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping(value = "/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingSpaceResponseDTO> findSpaceByCode(@PathVariable String code) {
        ParkingSpace parkingSpace = parkingSpaceService.findByCode(code);
        return ResponseEntity.ok(ParkingSpaceMapper.toDto(parkingSpace));
    }

}

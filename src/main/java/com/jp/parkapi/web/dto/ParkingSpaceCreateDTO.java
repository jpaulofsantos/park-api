package com.jp.parkapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpaceCreateDTO {

    @NotBlank(message = "{NotBlank.parkingCreateDTO.code}")
    @Size(min = 4, max = 4, message = "{Size.parkingCreateDTO.code}")
    private String code;

    @NotBlank(message = "{NotBlank.parkingCreateDTO.status}")
    @Pattern(regexp = "FREE|OCCUPIED", message = "{Pattern.parkingCreateDTO.status}")
    private String status;

}

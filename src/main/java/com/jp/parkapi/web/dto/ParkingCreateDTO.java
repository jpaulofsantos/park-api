package com.jp.parkapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingCreateDTO {

    @NotBlank
    @Size(min = 8, max = 8)
    @Pattern(regexp = "[A-Z]{3}-[0-9]{4}", message = "A placa do veiculo deve seguir o padrão xxx-0000")
    private String plate;
    @NotBlank
    private String manufacturer;
    @NotBlank
    private String model;
    @NotBlank
    private String color;
    @NotBlank
    @Size(min = 11, max = 11)
    @CPF
    private String clientCpf;


}

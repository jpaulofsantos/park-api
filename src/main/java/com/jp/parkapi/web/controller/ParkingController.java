package com.jp.parkapi.web.controller;

import com.jp.parkapi.entities.Client;
import com.jp.parkapi.entities.ClientSpace;
import com.jp.parkapi.jwt.JwtUserDetails;
import com.jp.parkapi.repositories.projection.ClientSpaceProjection;
import com.jp.parkapi.services.ClientService;
import com.jp.parkapi.services.JasperService;
import com.jp.parkapi.services.ParkingService;
import com.jp.parkapi.web.dto.PageableDTO;
import com.jp.parkapi.web.dto.ParkingCreateDTO;
import com.jp.parkapi.web.dto.ParkingResponseDTO;
import com.jp.parkapi.web.dto.mapper.ClientMapper;
import com.jp.parkapi.web.dto.mapper.ClientSpaceMapper;
import com.jp.parkapi.web.dto.mapper.PageableMapper;
import com.jp.parkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Tag(name = "Parkings", description = "Contém as operações de registros de entrada e saída de um veículo no estacionamento.")
@RestController
@RequestMapping("api/v1/parkings")
public class ParkingController {

    @Autowired
    ParkingService parkingService;
    @Autowired
    ClientService clientService;

    @Autowired
    JasperService jasperService;

    @Operation(summary = "Operação de check-in", description = "Recurso para dare entrada em um veículo no estacionamento. Requisição exige um Bearer Token. Acesso restrito a ADMIN ",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL do recurso criada"),
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ParkingResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Causas possíveis: <br/> " +
                            "- CPF do cliente não cadastrado no sistema; <br/> " +
                            "- Nenhuma vaga livre foi localizada;",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Recurso não processado por dados de entrada inválidos",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil CLIENTE",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class)))
            })
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

    @GetMapping(value = "/check-in/{receipt}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<ParkingResponseDTO> findByReceipt(@PathVariable String receipt) {
        ClientSpace clientSpace = parkingService.findCheckInByCode(receipt);
        ParkingResponseDTO parkingResponseDTO = ClientSpaceMapper.toDto(clientSpace);
        return ResponseEntity.ok().body(parkingResponseDTO);
    }

    @Operation(summary = "Operação para localizar um veículo estacionado", description = "Recurso para retornar um veículo estacionado " +
            "pelo nº do recibo. Requisição exige uso de um bearer token. Acesso restrito a ADMIN",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = PATH, name = "receipt", description = "Número do rebibo gerado pelo check-in")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Número do recibo inexistente ou o veículo já passou pelo check-out.",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de cliente.",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PutMapping(value = "/check-out/{receipt}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingResponseDTO> checkOut(@PathVariable String receipt) {
        ClientSpace clientSpace = parkingService.checkOut(receipt);
        ParkingResponseDTO parkingResponseDTO = ClientSpaceMapper.toDto(clientSpace);
        return ResponseEntity.ok().body(parkingResponseDTO);
    }

    @Operation(summary = "Localizar os registros de estacionamentos do cliente por CPF", description = "Localizar os " +
            "registros de estacionamentos do cliente por CPF. Requisição exige uso de um bearer token. Acesso restrito a ADMIN",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = PATH, name = "cpf", description = "Nº do CPF referente ao cliente a ser consultado",
                            required = true
                    ),
                    @Parameter(in = QUERY, name = "page", description = "Representa a página retornada",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))
                    ),
                    @Parameter(in = QUERY, name = "size", description = "Representa o total de elementos por página",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5"))
                    ),
                    @Parameter(in = QUERY, name = "sort", description = "Campo padrão de ordenação 'dataEntrada,asc'. ",
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "dataEntrada,asc")),
                            hidden = true
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = PageableDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de CLIENTE",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping(value = "/check-in/search/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDTO> findParkingsByClientCpf(@PathVariable String cpf,
                                                               @PageableDefault(size = 5, sort = "entryDate", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ClientSpaceProjection> projection = parkingService.findByClientCpf(cpf, pageable);
        PageableDTO pageableDTO = PageableMapper.toDto(projection);
        return ResponseEntity.ok().body(pageableDTO);
    }

    @Operation(summary = "Localizar os registros de estacionamentos do cliente logado",
            description = "Localizar os registros de estacionamentos do cliente logado. " +
                    "Requisição exige uso de um bearer token. Recurso permito ao perfil de CLIENTE",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = QUERY, name = "page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                            description = "Representa a página retornada"
                    ),
                    @Parameter(in = QUERY, name = "size",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5")),
                            description = "Representa o total de elementos por página"
                    ),
                    @Parameter(in = QUERY, name = "sort", hidden = true,
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "dataEntrada,asc")),
                            description = "Campo padrão de ordenação 'dataEntrada,asc'. ")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de ADMIN",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<PageableDTO> findParkingsByClientCpfFromClient(@AuthenticationPrincipal JwtUserDetails user,
                                                               @PageableDefault(size = 5, sort = "entryDate", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ClientSpaceProjection> projection = parkingService.findByByUserId(user.getId(), pageable);
        PageableDTO pageableDTO = PageableMapper.toDto(projection);
        return ResponseEntity.ok().body(pageableDTO);
    }

    @GetMapping(value = "/report")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> getReport(HttpServletResponse response, @AuthenticationPrincipal JwtUserDetails user) throws IOException {
        String cpf = clientService.findClientByUserId(user.getId()).getCpf();
        jasperService.addParms("CPF", cpf);

        byte[] bytes = jasperService.createPdf();

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-disposition", "inLine; filename=" + System.currentTimeMillis() + ".pdf");
        response.getOutputStream().write(bytes);

        return ResponseEntity.ok().build();
    }
}

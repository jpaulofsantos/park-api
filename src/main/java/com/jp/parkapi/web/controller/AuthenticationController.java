package com.jp.parkapi.web.controller;

import com.jp.parkapi.jwt.JwtToken;
import com.jp.parkapi.jwt.JwtUserDetailService;
import com.jp.parkapi.web.dto.UserLoginDTO;
import com.jp.parkapi.web.dto.UserResponseDTO;
import com.jp.parkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Recurso para realizar a autenticação na API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AuthenticationController {

    private final JwtUserDetailService jwtUserDetailService;
    private final AuthenticationManager authenticationManager;

    @Operation(summary = "Autenticar na API", description = "Recurso de autenticação na API", responses = {
            @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso e retorno de um Bearer Token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Credenciais inválidas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Campos inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(@RequestBody @Valid UserLoginDTO userLoginDTO, HttpServletRequest httpServletRequest) {
        log.info("Processo de autenticação pelo login {}", userLoginDTO.getUsername());
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword());
            authenticationManager.authenticate(authenticationToken);
            JwtToken token = jwtUserDetailService.getTokenAuthenticated(userLoginDTO.getUsername());

            return ResponseEntity.ok(token);

        } catch (AuthenticationException e) {
            log.warn("Bad credentials from username {}", userLoginDTO.getUsername());
        }
        return ResponseEntity.badRequest().body(new ErrorMessage(httpServletRequest, HttpStatus.BAD_REQUEST, "Credenciais inválidas"));
    }
}

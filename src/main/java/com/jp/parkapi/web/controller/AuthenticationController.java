package com.jp.parkapi.web.controller;

import com.jp.parkapi.jwt.JwtToken;
import com.jp.parkapi.jwt.JwtUserDetailService;
import com.jp.parkapi.web.dto.UserLoginDTO;
import com.jp.parkapi.web.exception.ErrorMessage;
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
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AuthenticationController {

    private final JwtUserDetailService jwtUserDetailService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(@RequestBody @Valid UserLoginDTO userLoginDTO, HttpServletRequest httpServletRequest) {
        log.info("Processo de autenticação pelo login %s", userLoginDTO.getUsername());
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword());
            authenticationManager.authenticate(authenticationToken);
            JwtToken token = jwtUserDetailService.getTokenAuthenticated(userLoginDTO.getUsername());

            return ResponseEntity.ok(token);

        } catch (AuthenticationException e) {
            log.warn("Bad credentials from username %s", userLoginDTO.getUsername());
        }
        return ResponseEntity.badRequest().body(new ErrorMessage(httpServletRequest, HttpStatus.BAD_REQUEST, "Credenciais inválidas"));
    }
}

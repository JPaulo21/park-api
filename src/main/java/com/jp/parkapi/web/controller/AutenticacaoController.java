package com.jp.parkapi.web.controller;

import com.jp.parkapi.jwt.JwtToken;
import com.jp.parkapi.jwt.JwtUserDetailsService;
import com.jp.parkapi.web.dto.UsuarioLoginDto;
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
public class AutenticacaoController {

    private final JwtUserDetailsService detailsService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/auth")
    public ResponseEntity<?> autenticar(@Valid @RequestBody UsuarioLoginDto loginDto, HttpServletRequest request){
        log.info("Processo de autenticação pelo Login {}", loginDto.getUsername());
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
            authenticationManager.authenticate(authenticationToken);
            JwtToken token = detailsService.getTokenAuthenticated(loginDto.getUsername());
            return ResponseEntity.ok(token);
        }catch (AuthenticationException ex){
            log.warn("Bad Credentials from username '{}'",loginDto.getUsername());
            log.error("Exceção de auteticação {}", ex.getMessage());
        }
        return ResponseEntity
                .badRequest()
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, "Credencias inválidas"));
    }

}

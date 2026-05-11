package com.product.filthilink.controllers.auth;

import com.product.filthilink.entities.dtos.AuthResponseDto;
import com.product.filthilink.entities.dtos.LoginResponseDto;
import com.product.filthilink.entities.dtos.RegisterRequestDto;
import com.product.filthilink.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginResponseDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.login(dto));
    }



}

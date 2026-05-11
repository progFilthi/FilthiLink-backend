package com.product.filthilink.services.auth;

import com.product.filthilink.entities.dtos.AuthResponseDto;
import com.product.filthilink.entities.dtos.LoginResponseDto;
import com.product.filthilink.entities.dtos.RegisterRequestDto;
import com.product.filthilink.entities.models.User;
import com.product.filthilink.exceptions.ResourceConflict;
import com.product.filthilink.repositories.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthResponseDto register(RegisterRequestDto dto){
        if( userRepository.existsByEmail(dto.email()) ){
            throw new ResourceConflict("Email already exists");
        }

        User user = new User();

        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponseDto(token, user.getEmail());
    }

    public AuthResponseDto login(LoginResponseDto dto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));

        User user = userRepository.findByEmail(dto.email()).orElseThrow(() ->
                new UsernameNotFoundException(dto.email())
        );

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponseDto(token, user.getEmail());
    }
}

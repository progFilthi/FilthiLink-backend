package com.product.filthilink.entities.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required.")
        String email,

        @Size(min = 8,  message = "Password should be at least 8 characters.")
        @NotBlank(message = "Password is required.")
        String password
) {
}

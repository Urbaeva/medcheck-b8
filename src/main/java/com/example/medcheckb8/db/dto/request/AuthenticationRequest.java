package com.example.medcheckb8.db.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record AuthenticationRequest(
        @NotBlank(message = "Email не может быть пустым!")
        String email,
        @NotBlank(message = "Пароль не может быть пустым!")
        String password
) {
}

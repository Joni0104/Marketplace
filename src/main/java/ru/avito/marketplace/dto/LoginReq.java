package ru.avito.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Запрос на аутентификацию (может не использоваться с Basic Auth)")
public class LoginReq {

    @Schema(description = "Email пользователя", example = "user@example.com")
    @NotBlank(message = "Email не может быть пустым")
    private String username;

    @Schema(description = "Пароль", example = "password123")
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
}
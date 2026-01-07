package ru.avito.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Запрос на аутентификацию")
public class LoginReq {

    @Schema(
            description = "Имя пользователя (email)",
            example = "user@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;

    @Schema(
            description = "Пароль",
            example = "mySecretPassword123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
}
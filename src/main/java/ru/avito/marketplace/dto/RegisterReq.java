package ru.avito.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Запрос на регистрацию")
public class RegisterReq {

    @Schema(description = "Email пользователя", example = "user@example.com")
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String username;

    @Schema(description = "Пароль", example = "myPassword123")
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 32, message = "Пароль должен быть от 8 до 32 символов")
    private String password;

    @Schema(description = "Имя", example = "Иван")
    @NotBlank(message = "Имя не может быть пустым")
    private String firstName;

    @Schema(description = "Фамилия", example = "Иванов")
    @NotBlank(message = "Фамилия не может быть пустым")
    private String lastName;

    @Schema(description = "Телефон", example = "+79161234567")
    @NotBlank(message = "Телефон не может быть пустым")
    private String phone;

    @Schema(description = "Роль пользователя", example = "USER", defaultValue = "USER")
    private String role = "USER";
}
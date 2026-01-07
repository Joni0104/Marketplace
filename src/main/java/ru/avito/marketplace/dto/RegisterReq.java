package ru.avito.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на регистрацию пользователя")
public class RegisterReq {

    @Schema(
            description = "Имя пользователя (email)",
            example = "user@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String username;

    @Schema(
            description = "Пароль",
            example = "mySecretPassword123",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 8,
            maxLength = 32
    )
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 32, message = "Пароль должен быть от 8 до 32 символов")
    private String password;

    @Schema(
            description = "Имя пользователя",
            example = "Иван",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 16, message = "Имя должно быть от 2 до 16 символов")
    private String firstName;

    @Schema(
            description = "Фамилия пользователя",
            example = "Иванов",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Фамилия не может быть пустым")
    @Size(min = 2, max = 16, message = "Фамилия должна быть от 2 до 16 символов")
    private String lastName;

    @Schema(
            description = "Номер телефона",
            example = "+79161234567",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Телефон не может быть пустым")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Номер телефона должен быть в формате +79161234567")
    private String phone;

    @Schema(
            description = "Роль пользователя",
            example = "USER",
            allowableValues = {"USER", "ADMIN"},
            defaultValue = "USER"
    )
    private String role = "USER";
}
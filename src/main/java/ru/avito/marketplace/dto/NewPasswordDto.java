package ru.avito.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на изменение пароля")
public class NewPasswordDto {

    @Schema(
            description = "Текущий пароль",
            example = "oldPassword123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Текущий пароль не может быть пустым")
    private String currentPassword;

    @Schema(
            description = "Новый пароль",
            example = "newPassword123",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 8,
            maxLength = 32
    )
    @NotBlank(message = "Новый пароль не может быть пустым")
    @Size(min = 8, max = 32, message = "Новый пароль должен быть от 8 до 32 символов")
    private String newPassword;
}
package ru.avito.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Данные для обновления пользователя")
public class UpdateUserDto {

    @Schema(
            description = "Имя пользователя",
            example = "Иван",
            minLength = 2,
            maxLength = 16
    )
    @Size(min = 2, max = 16, message = "Имя должно быть от 2 до 16 символов")
    private String firstName;

    @Schema(
            description = "Фамилия пользователя",
            example = "Иванов",
            minLength = 2,
            maxLength = 16
    )
    @Size(min = 2, max = 16, message = "Фамилия должна быть от 2 до 16 символов")
    private String lastName;

    @Schema(
            description = "Номер телефона",
            example = "+79161234567"
    )
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Номер телефона должен быть в формате +79161234567")
    private String phone;
}
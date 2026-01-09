package ru.avito.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Информация о пользователе")
public class UserDto {

    @Schema(description = "ID пользователя", example = "1")
    private Integer id;

    @Schema(description = "Имя пользователя (email)", example = "user@example.com")
    private String email;

    @Schema(description = "Имя", example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия", example = "Иванов")
    private String lastName;

    @Schema(description = "Номер телефона", example = "+79161234567")
    private String phone;

    @Schema(description = "URL аватарки", example = "/images/avatars/1.jpg")
    private String image;

    @Schema(description = "Роль пользователя", example = "USER")
    private String role;
}
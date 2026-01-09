package ru.avito.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Расширенная информация об объявлении")
public class ExtendedAdDto {

    @Schema(description = "ID объявления", example = "1")
    private Integer id;

    @Schema(description = "Имя автора", example = "Иван")
    private String authorFirstName;

    @Schema(description = "Фамилия автора", example = "Иванов")
    private String authorLastName;

    @Schema(description = "Описание объявления", example = "Ноутбук в отличном состоянии")
    private String description;

    @Schema(description = "Email автора", example = "user@example.com")
    private String email;

    @Schema(description = "Ссылка на изображение объявления", example = "/images/ads/1.jpg")
    private String image;

    @Schema(description = "Номер телефона автора", example = "+79161234567")
    private String phone;

    @Schema(description = "Цена", example = "15000")
    private Integer price;

    @Schema(description = "Заголовок объявления", example = "Продам ноутбук")
    private String title;
}
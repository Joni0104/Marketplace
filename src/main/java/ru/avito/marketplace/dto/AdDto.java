package ru.avito.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Краткая информация об объявлении")
public class AdDto {

    @Schema(description = "ID объявления", example = "1")
    private Integer id;

    @Schema(description = "ID автора объявления", example = "5")
    private Integer authorId;

    @Schema(description = "Ссылка на изображение объявления", example = "/images/ads/1.jpg")
    private String image;

    @Schema(description = "Цена", example = "15000")
    private Integer price;

    @Schema(description = "Заголовок объявления", example = "Продам ноутбук")
    private String title;

    @Schema(description = "Описание объявления", example = "Ноутбук в отличном состоянии")
    private String description;
}
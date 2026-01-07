package ru.avito.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Данные для создания или обновления объявления")
public class CreateOrUpdateAdDto {

    @Schema(
            description = "Заголовок объявления",
            example = "Продам ноутбук",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 4,
            maxLength = 32
    )
    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 4, max = 32, message = "Заголовок должен быть от 4 до 32 символов")
    private String title;

    @Schema(
            description = "Цена",
            example = "15000",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "0"
    )
    @NotNull(message = "Цена не может быть пустой")
    @Min(value = 0, message = "Цена не может быть отрицательной")
    private Integer price;

    @Schema(
            description = "Описание объявления",
            example = "Ноутбук в отличном состоянии, 2 года использования",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 8,
            maxLength = 64
    )
    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 8, max = 64, message = "Описание должно быть от 8 до 64 символов")
    private String description;
}
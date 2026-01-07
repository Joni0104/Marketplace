package ru.avito.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Данные для создания или обновления комментария")
public class CreateOrUpdateCommentDto {

    @Schema(
            description = "Текст комментария",
            example = "Отличный товар! Интересуюсь покупкой.",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 8,
            maxLength = 128
    )
    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(min = 8, max = 128, message = "Комментарий должен быть от 8 до 128 символов")
    private String text;
}
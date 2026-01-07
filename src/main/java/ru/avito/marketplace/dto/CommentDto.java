package ru.avito.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Schema(description = "Комментарий к объявлению")
public class CommentDto {

    @Schema(description = "ID комментария", example = "1")
    private Integer id;

    @Schema(description = "ID автора комментария", example = "3")
    private Integer authorId;

    @Schema(description = "Ссылка на аватар автора", example = "/images/avatars/3.jpg")
    private String authorImage;

    @Schema(description = "Имя автора", example = "Петр")
    private String authorFirstName;

    @Schema(description = "Дата и время создания комментария", example = "2024-01-15T10:30:00Z")
    private OffsetDateTime createdAt;

    @Schema(description = "Текст комментария", example = "Отличный товар! Интересуюсь покупкой.")
    private String text;
}
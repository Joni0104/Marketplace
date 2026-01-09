package ru.avito.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Обертка для списков с пагинацией")
public class ResponseWrapper<T> {

    @Schema(description = "Общее количество элементов", example = "1")
    private Integer count;

    @Schema(description = "Список элементов")
    private List<T> results;

    // Удобный конструктор
    public ResponseWrapper(List<T> results) {
        this.results = results;
        this.count = results != null ? results.size() : 0;
    }
}
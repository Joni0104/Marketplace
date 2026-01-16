package ru.avito.marketplace.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.avito.marketplace.dto.CommentDto;
import ru.avito.marketplace.dto.CreateOrUpdateCommentDto;
import ru.avito.marketplace.dto.ResponseWrapper;
import ru.avito.marketplace.service.CommentService;

@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Комментарии", description = "API для работы с комментариями")
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "Получить комментарии объявления",
            description = "Возвращает все комментарии к указанному объявлению"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseWrapper.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    })
    @GetMapping("/{adId}/comments")
    public ResponseEntity<ResponseWrapper<CommentDto>> getComments(
            @Parameter(description = "ID объявления", required = true, example = "1")
            @PathVariable Integer adId) {

        ResponseWrapper<CommentDto> response = commentService.getComments(adId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Добавить комментарий",
            description = "Создание нового комментария к объявлению"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Комментарий создан",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    })
    @PostMapping("/{adId}/comments")
    public ResponseEntity<CommentDto> addComment(
            @Parameter(description = "ID объявления", required = true, example = "1")
            @PathVariable Integer adId,
            @Parameter(description = "Данные комментария", required = true)
            @Valid @RequestBody CreateOrUpdateCommentDto commentDto,
            Authentication authentication) {

        CommentDto created = commentService.addComment(adId, commentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Удалить комментарий",
            description = "Удаление комментария по ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Комментарий удален"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Нет прав на удаление"),
            @ApiResponse(responseCode = "404", description = "Комментарий не найден")
    })
    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "ID объявления", required = true, example = "1")
            @PathVariable Integer adId,
            @Parameter(description = "ID комментария", required = true, example = "1")
            @PathVariable Integer commentId) {

        commentService.deleteComment(adId, commentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Обновить комментарий",
            description = "Редактирование текста комментария"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Нет прав на редактирование"),
            @ApiResponse(responseCode = "404", description = "Комментарий не найден")
    })
    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @Parameter(description = "ID объявления", required = true, example = "1")
            @PathVariable Integer adId,
            @Parameter(description = "ID комментария", required = true, example = "1")
            @PathVariable Integer commentId,
            @Parameter(description = "Новые данные комментария", required = true)
            @Valid @RequestBody CreateOrUpdateCommentDto commentDto) {

        CommentDto updated = commentService.updateComment(adId, commentId, commentDto);
        return ResponseEntity.ok(updated);
    }
}
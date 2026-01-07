package ru.avito.marketplace.controller;

import ru.avito.marketplace.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

@RestController
@RequestMapping("/ads")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Объявления", description = "API для работы с объявлениями")
public class AdController {

    @Operation(
            summary = "Получить все объявления",
            description = "Возвращает список всех объявлений с пагинацией"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseWrapper.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<AdDto>> getAllAds() {
        // Заглушка для Этапа I
        ResponseWrapper<AdDto> response = new ResponseWrapper<>(Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Добавить объявление",
            description = "Создание нового объявления с изображением"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Объявление создано",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "413", description = "Файл слишком большой")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdDto> addAd(
            @Parameter(description = "Данные объявления", required = true)
            @RequestPart("properties") @Valid CreateOrUpdateAdDto properties,
            @Parameter(description = "Изображение объявления", required = true)
            @RequestPart("image") MultipartFile image) {

        // Заглушка для Этапа I
        AdDto response = new AdDto();
        response.setId(1);
        response.setAuthorId(1);
        response.setImage("/images/ads/stub.jpg");
        response.setPrice(properties.getPrice());
        response.setTitle(properties.getTitle());
        response.setDescription(properties.getDescription());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Получить объявление по ID",
            description = "Возвращает расширенную информацию об объявлении"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExtendedAdDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ExtendedAdDto> getAd(
            @Parameter(description = "ID объявления", required = true, example = "1")
            @PathVariable Integer id) {

        // Заглушка для Этапа I
        ExtendedAdDto response = new ExtendedAdDto();
        response.setId(id);
        response.setAuthorFirstName("Иван");
        response.setAuthorLastName("Иванов");
        response.setDescription("Описание объявления");
        response.setEmail("user@example.com");
        response.setImage("/images/ads/stub.jpg");
        response.setPhone("+79161234567");
        response.setPrice(15000);
        response.setTitle("Заголовок объявления");

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Удалить объявление",
            description = "Удаление объявления по ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Объявление удалено"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Нет прав на удаление"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(
            @Parameter(description = "ID объявления", required = true, example = "1")
            @PathVariable Integer id) {

        // Заглушка для Этапа I
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Обновить объявление",
            description = "Редактирование данных объявления"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Нет прав на редактирование"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AdDto> updateAd(
            @Parameter(description = "ID объявления", required = true, example = "1")
            @PathVariable Integer id,
            @Parameter(description = "Новые данные объявления", required = true)
            @Valid @RequestBody CreateOrUpdateAdDto updateAd) {

        // Заглушка для Этапа I
        AdDto response = new AdDto();
        response.setId(id);
        response.setAuthorId(1);
        response.setImage("/images/ads/stub.jpg");
        response.setPrice(updateAd.getPrice());
        response.setTitle(updateAd.getTitle());
        response.setDescription(updateAd.getDescription());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Получить объявления текущего пользователя",
            description = "Возвращает список объявлений авторизованного пользователя"
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
            @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    @GetMapping("/me")
    public ResponseEntity<ResponseWrapper<AdDto>> getMyAds() {
        // Заглушка для Этапа I
        ResponseWrapper<AdDto> response = new ResponseWrapper<>(Collections.emptyList());
        return ResponseEntity.ok(response);
    }
}
package ru.avito.marketplace.controller;

import ru.avito.marketplace.dto.UpdateUserDto;
import ru.avito.marketplace.dto.UserDto;
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

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Пользователи", description = "API для работы с профилями пользователей")
public class UserController {

    @Operation(
            summary = "Получить информацию о текущем пользователе",
            description = "Возвращает данные авторизованного пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        // Заглушка для Этапа I
        UserDto response = new UserDto();
        response.setId(1);
        response.setEmail("user@example.com");
        response.setFirstName("Иван");
        response.setLastName("Иванов");
        response.setPhone("+79161234567");
        response.setImage("/images/avatars/default.png");
        response.setRole("USER");

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Обновить информацию о пользователе",
            description = "Изменение данных профиля"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    @PatchMapping("/me")
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "Новые данные пользователя", required = true)
            @Valid @RequestBody UpdateUserDto updateUserDto) {

        // Заглушка для Этапа I
        UserDto response = new UserDto();
        response.setId(1);
        response.setEmail("user@example.com");
        response.setFirstName(updateUserDto.getFirstName());
        response.setLastName(updateUserDto.getLastName());
        response.setPhone(updateUserDto.getPhone());
        response.setImage("/images/avatars/default.png");
        response.setRole("USER");

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Обновить аватар пользователя",
            description = "Загрузка нового изображения профиля"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Некорректный файл"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "413", description = "Файл слишком большой")
    })
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> updateUserImage(
            @Parameter(description = "Новое изображение профиля", required = true)
            @RequestParam MultipartFile image) {

        // Заглушка для Этапа I
        UserDto response = new UserDto();
        response.setId(1);
        response.setEmail("user@example.com");
        response.setFirstName("Иван");
        response.setLastName("Иванов");
        response.setPhone("+79161234567");
        response.setImage("/images/avatars/new-avatar.jpg");
        response.setRole("USER");

        return ResponseEntity.ok(response);
    }
}
package ru.avito.marketplace.controller;

import ru.avito.marketplace.dto.LoginReq;
import ru.avito.marketplace.dto.LoginRes;
import ru.avito.marketplace.dto.RegisterReq;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Аутентификация", description = "API для входа и регистрации")
public class AuthController {

    @Operation(
            summary = "Авторизация пользователя",
            description = "Вход в систему с email и паролем"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная авторизация",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginRes.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginRes> login(@Valid @RequestBody LoginReq req) {
        // Заглушка для Этапа I
        LoginRes response = new LoginRes("stub-jwt-token-for-user-" + req.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Регистрация пользователя",
            description = "Создание нового аккаунта"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Пользователь успешно зарегистрирован",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginRes.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "409", description = "Пользователь уже существует")
    })
    @PostMapping("/register")
    public ResponseEntity<LoginRes> register(@Valid @RequestBody RegisterReq req) {
        // Заглушка для Этапа I
        LoginRes response = new LoginRes("stub-jwt-token-for-new-user-" + req.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
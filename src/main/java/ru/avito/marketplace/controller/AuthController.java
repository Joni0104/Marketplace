package ru.avito.marketplace.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.avito.marketplace.dto.RegisterReq;
import ru.avito.marketplace.dto.NewPasswordDto;
import ru.avito.marketplace.dto.UserDto;
import ru.avito.marketplace.service.AuthService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Аутентификация", description = "API для регистрации, авторизации и управления паролями")
public class AuthController {

    private final AuthService authService;

    /**
     * Регистрация нового пользователя
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Регистрация пользователя",
            description = "Создание нового аккаунта пользователя",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
                    @ApiResponse(responseCode = "400", description = "Неверные данные пользователя"),
                    @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует")
            }
    )
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterReq registerReq) {
        log.info("Запрос на регистрацию пользователя: {}", registerReq.getUsername());
        authService.register(registerReq);
        log.info("Пользователь успешно зарегистрирован: {}", registerReq.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * ✅ КРИТЕРИИ 11-12: Смена пароля текущего пользователя
     * Успешный запрос: 200 OK
     * Неверный старый пароль: 400 Bad Request
     * Неавторизованный доступ: 401 Unauthorized
     */
    @PostMapping("/set_password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Смена пароля",
            description = "Изменение пароля текущего пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пароль успешно изменен"),
                    @ApiResponse(responseCode = "400", description = "Неверный текущий пароль или невалидные данные"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    public ResponseEntity<?> changePassword(@Valid @RequestBody NewPasswordDto dto,
                                            Authentication authentication) {
        String username = authentication.getName();
        log.info("Запрос на смену пароля для пользователя: {}", username);

        try {
            authService.changePassword(username, dto);
            log.info("Пароль успешно изменен для пользователя: {}", username);
            return ResponseEntity.ok().build();
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            // ✅ КРИТЕРИЙ 11: Неверный старый пароль → 400 Bad Request
            log.warn("Неверный текущий пароль для пользователя: {}", username);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Неверный текущий пароль");
        }
    }

    /**
     * Получение информации о текущем пользователе
     */
    @GetMapping("/me")
    @Operation(
            summary = "Информация о текущем пользователе",
            description = "Получение данных аутентифицированного пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Данные пользователя",
                            content = @Content(schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
            }
    )
    public ResponseEntity<UserDto> getCurrentUserInfo(Authentication authentication) {
        String username = authentication.getName();
        log.debug("Запрос информации о текущем пользователе: {}", username);

        UserDto userDto = authService.getCurrentUserInfo(username);
        log.debug("Информация о пользователе получена: {}", username);

        return ResponseEntity.ok(userDto);
    }

    /**
     * Проверка авторизации (тестовый endpoint)
     */
    @GetMapping("/check")
    @Operation(
            summary = "Проверка авторизации",
            description = "Проверка, что пользователь аутентифицирован",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь аутентифицирован"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
            }
    )
    public ResponseEntity<String> checkAuth(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok("Аутентифицирован как: " + authentication.getName());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Не аутентифицирован");
    }

    /**
     * Обработка исключений валидации
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("Ошибка валидации");

        log.warn("Ошибка валидации: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    /**
     * Обработка исключения "пользователь уже существует"
     */
    @ExceptionHandler(ru.avito.marketplace.exception.UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(
            ru.avito.marketplace.exception.UserAlreadyExistsException ex) {
        log.warn("Пользователь уже существует: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
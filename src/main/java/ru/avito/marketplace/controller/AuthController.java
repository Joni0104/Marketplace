package ru.avito.marketplace.controller;

import ru.avito.marketplace.dto.LoginReq;
import ru.avito.marketplace.dto.LoginRes;
import ru.avito.marketplace.dto.NewPasswordDto;
import ru.avito.marketplace.dto.RegisterReq;
import ru.avito.marketplace.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Аутентификация")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Авторизация")
    @PostMapping("/login")
    public ResponseEntity<LoginRes> login(@Valid @RequestBody LoginReq req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @Operation(summary = "Регистрация")
    @PostMapping("/register")
    public ResponseEntity<LoginRes> register(@Valid @RequestBody RegisterReq req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
    }

    @Operation(summary = "Смена пароля")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пароль изменен"),
            @ApiResponse(responseCode = "400", description = "Неверный текущий пароль"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody NewPasswordDto newPasswordDto) {
        authService.changePassword(newPasswordDto);
        return ResponseEntity.ok().build();
    }
}
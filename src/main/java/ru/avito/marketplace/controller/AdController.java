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
import ru.avito.marketplace.dto.*;
import ru.avito.marketplace.service.AdService;
import ru.avito.marketplace.service.AuthService;

import java.io.IOException;

@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
@Tag(name = "Объявления")
public class AdController {

    private final AdService adService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<AdDto>> getAllAds() {
        ResponseWrapper<AdDto> response = adService.getAllAds();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AdDto> addAd(
            @Valid @RequestBody CreateOrUpdateAdDto properties,
            Authentication authentication) throws IOException {

        AdDto createdAd = adService.createAd(properties, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAd);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExtendedAdDto> getAd(@PathVariable Integer id) {
        ExtendedAdDto ad = adService.getAd(id);
        return ResponseEntity.ok(ad);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable Integer id) throws IOException {
        adService.deleteAd(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdDto> updateAd(
            @PathVariable Integer id,
            @Valid @RequestBody CreateOrUpdateAdDto updateAd) {

        AdDto updated = adService.updateAd(id, updateAd);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseWrapper<AdDto>> getMyAds(Authentication authentication) {
        ResponseWrapper<AdDto> response = adService.getMyAds();
        return ResponseEntity.ok(response);
    }
}
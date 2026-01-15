package ru.avito.marketplace.service;

import ru.avito.marketplace.dto.AdDto;
import ru.avito.marketplace.dto.CreateOrUpdateAdDto;
import ru.avito.marketplace.dto.ExtendedAdDto;
import ru.avito.marketplace.dto.ResponseWrapper;
import ru.avito.marketplace.entity.Ad;
import ru.avito.marketplace.entity.User;
import ru.avito.marketplace.exception.AccessDeniedException;
import ru.avito.marketplace.exception.AdNotFoundException;
import ru.avito.marketplace.mapper.AdMapper;
import ru.avito.marketplace.repository.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdService {

    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final AuthService authService;
    private final ImageService imageService;

    private static final String IMAGES_DIR = "images/ads/";

    public ResponseWrapper<AdDto> getAllAds() {
        List<Ad> ads = adRepository.findAllWithAuthor();
        List<AdDto> adDtos = ads.stream()
                .map(adMapper::toAdDto)
                .toList();
        return new ResponseWrapper<>(adDtos.size(), adDtos);
    }

    @Transactional
    public AdDto createAd(CreateOrUpdateAdDto createAdDto, MultipartFile image) throws IOException {
        User currentUser = authService.getCurrentUser();

        Ad ad = adMapper.toEntity(createAdDto);
        ad.setAuthor(currentUser);

        // Сохраняем изображение
        if (image != null && !image.isEmpty()) {
            String imagePath = imageService.saveAdImage(image);
            ad.setImage(imagePath);
        }

        Ad savedAd = adRepository.save(ad);
        log.info("Ad created with id: {} by user: {}", savedAd.getId(), currentUser.getEmail());

        return adMapper.toAdDto(savedAd);
    }

    public ExtendedAdDto getAd(Integer id) {
        Ad ad = adRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление с id " + id + " не найдено"));
        return adMapper.toExtendedAdDto(ad);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @adService.isAuthor(#id, authentication.name)")
    public void deleteAd(Integer id) throws IOException {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление с id " + id + " не найдено"));

        // Удаляем изображение если есть
        if (ad.getImage() != null) {
            imageService.deleteImage(ad.getImage());
        }

        adRepository.delete(ad);
        log.info("Ad deleted with id: {}", id);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @adService.isAuthor(#id, authentication.name)")
    public AdDto updateAd(Integer id, CreateOrUpdateAdDto updateAdDto) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление с id " + id + " не найдено"));

        adMapper.updateEntity(updateAdDto, ad);
        Ad updatedAd = adRepository.save(ad);

        log.info("Ad updated with id: {}", id);
        return adMapper.toAdDto(updatedAd);
    }

    @Transactional
    public AdDto updateAdImage(Integer id, MultipartFile image) throws IOException {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление с id " + id + " не найдено"));

        // Проверяем права
        if (!isAuthor(id) && !authService.isAdmin()) {
            throw new AccessDeniedException("Нет прав для редактирования этого объявления");
        }

        // Удаляем старое изображение если есть
        if (ad.getImage() != null) {
            imageService.deleteImage(ad.getImage());
        }

        // Сохраняем новое изображение
        String imagePath = imageService.saveAdImage(image);
        ad.setImage(imagePath);

        Ad updatedAd = adRepository.save(ad);
        log.info("Ad image updated for ad id: {}", id);

        return adMapper.toAdDto(updatedAd);
    }

    public ResponseWrapper<AdDto> getMyAds() {
        User currentUser = authService.getCurrentUser();
        List<Ad> ads = adRepository.findAllByAuthor(currentUser);

        List<AdDto> adDtos = ads.stream()
                .map(adMapper::toAdDto)
                .toList();

        return new ResponseWrapper<>(adDtos.size(), adDtos);
    }

    // Метод для SpEL выражения в @PreAuthorize
    public boolean isAuthor(Integer adId, String username) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));
        return ad.getAuthor().getEmail().equals(username);
    }

    // Метод для проверки без @PreAuthorize
    public boolean isAuthor(Integer adId) {
        User currentUser = authService.getCurrentUser();
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));
        return ad.getAuthor().getId().equals(currentUser.getId());
    }

    public boolean existsById(Integer id) {
        return adRepository.existsById(id);
    }
}
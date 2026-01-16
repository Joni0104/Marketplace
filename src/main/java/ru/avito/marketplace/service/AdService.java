package ru.avito.marketplace.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.avito.marketplace.dto.AdDto;
import ru.avito.marketplace.dto.CreateOrUpdateAdDto;
import ru.avito.marketplace.dto.ExtendedAdDto;
import ru.avito.marketplace.dto.ResponseWrapper;
import ru.avito.marketplace.entity.Ad;
import ru.avito.marketplace.entity.User;
import ru.avito.marketplace.exception.AdNotFoundException;
import ru.avito.marketplace.mapper.AdMapper;
import ru.avito.marketplace.repository.AdRepository;
import ru.avito.marketplace.repository.UserRepository;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdService {

    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final AuthService authService;
    private final ImageService imageService;
    private final UserRepository userRepository; // Добавили

    // Получение всех объявлений (публичный доступ)
    public ResponseWrapper<AdDto> getAllAds() {
        List<Ad> ads = adRepository.findAllWithAuthor();
        List<AdDto> adDtos = ads.stream()
                .map(adMapper::toAdDto)
                .toList();
        return new ResponseWrapper<>(adDtos.size(), adDtos);
    }

    // Создание объявления (требует аутентификации)
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

    // Получение объявления по ID (публичный доступ)
    public ExtendedAdDto getAd(Integer id) {
        Ad ad = adRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление с id " + id + " не найдено"));
        return adMapper.toExtendedAdDto(ad);
    }

    // Удаление объявления (с проверкой прав)
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authorizationService.isAuthor(#id, authentication.name)")
    public void deleteAd(Integer id) throws IOException {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление с id " + id + " не найдено"));

        // ✅ КРИТЕРИЙ 5: Дополнительная явная проверка (ad.getAuthor().equals(currentUser))
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        boolean isAuthor = ad.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());

        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("Нет прав для удаления этого объявления");
        }

        // Удаляем изображение если есть
        if (ad.getImage() != null) {
            imageService.deleteImage(ad.getImage());
        }

        adRepository.delete(ad);
        log.info("Ad deleted with id: {}", id);
    }

    // Обновление объявления (с проверкой прав)
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authorizationService.isAuthor(#id, authentication.name)")
    public AdDto updateAd(Integer id, CreateOrUpdateAdDto updateAdDto) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление с id " + id + " не найдено"));

        // ✅ КРИТЕРИЙ 5: Явная проверка (ad.getAuthor().equals(currentUser))
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        boolean isAuthor = ad.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());

        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("Нет прав для редактирования этого объявления");
        }

        adMapper.updateEntity(updateAdDto, ad);
        Ad updatedAd = adRepository.save(ad);

        log.info("Ad updated with id: {}", id);
        return adMapper.toAdDto(updatedAd);
    }

    // Обновление изображения объявления (с проверкой прав)
    @Transactional
    public AdDto updateAdImage(Integer id, MultipartFile image) throws IOException {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление с id " + id + " не найдено"));

        // ✅ КРИТЕРИЙ 5: Проверка прав
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        boolean isAuthor = ad.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());

        if (!isAuthor && !isAdmin) {
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

    // Получение объявлений текущего пользователя
    public ResponseWrapper<AdDto> getMyAds() {
        User currentUser = authService.getCurrentUser();
        List<Ad> ads = adRepository.findAllByAuthor(currentUser);

        List<AdDto> adDtos = ads.stream()
                .map(adMapper::toAdDto)
                .toList();

        return new ResponseWrapper<>(adDtos.size(), adDtos);
    }

    // ✅ КРИТЕРИЙ 7: Проверка роли ADMIN
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    // ✅ Метод для SpEL выражения в @PreAuthorize
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

    // ✅ КРИТЕРИЙ 7: Метод, доступный только ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteAdAsAdmin(Integer id) {
        if (!adRepository.existsById(id)) {
            throw new AdNotFoundException("Объявление с id " + id + " не найдено");
        }
        adRepository.deleteById(id);
        log.info("Ad {} deleted by ADMIN", id);
    }

    // ✅ КРИТЕРИЙ 7: Метод, доступный только ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public AdDto updateAdAsAdmin(Integer id, CreateOrUpdateAdDto updateAdDto) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление с id " + id + " не найдено"));

        adMapper.updateEntity(updateAdDto, ad);
        Ad updatedAd = adRepository.save(ad);

        log.info("Ad {} updated by ADMIN", id);
        return adMapper.toAdDto(updatedAd);
    }
}
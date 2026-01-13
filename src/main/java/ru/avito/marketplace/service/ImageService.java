package ru.avito.marketplace.service;

import ru.avito.marketplace.constants.ApiConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class ImageService {

    public String saveUserAvatar(MultipartFile image) throws IOException {
        validateImage(image);
        String fileName = generateFileName("avatar", image.getOriginalFilename());
        Path filePath = Paths.get(ApiConstants.IMAGES_DIRECTORY + ApiConstants.AVATARS_SUBDIR, fileName);
        saveImage(image, filePath);
        return "/" + ApiConstants.IMAGES_DIRECTORY + ApiConstants.AVATARS_SUBDIR + fileName;
    }

    public String saveAdImage(MultipartFile image) throws IOException {
        validateImage(image);
        String fileName = generateFileName("ad", image.getOriginalFilename());
        Path filePath = Paths.get(ApiConstants.IMAGES_DIRECTORY + ApiConstants.ADS_SUBDIR, fileName);
        saveImage(image, filePath);
        return "/" + ApiConstants.IMAGES_DIRECTORY + ApiConstants.ADS_SUBDIR + fileName;
    }

    private void validateImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Изображение не может быть пустым");
        }

        if (image.getSize() > ApiConstants.MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("Размер изображения не должен превышать " +
                    ApiConstants.MAX_IMAGE_SIZE_MB + " MB");
        }

        String contentType = image.getContentType();
        boolean isValidType = false;
        for (String allowedType : ApiConstants.ALLOWED_IMAGE_TYPES) {
            if (allowedType.equals(contentType)) {
                isValidType = true;
                break;
            }
        }

        if (!isValidType) {
            throw new IllegalArgumentException("Недопустимый тип изображения. Разрешены: " +
                    String.join(", ", ApiConstants.ALLOWED_IMAGE_TYPES));
        }
    }

    private String generateFileName(String prefix, String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return prefix + "_" + UUID.randomUUID() + extension;
    }

    private void saveImage(MultipartFile image, Path filePath) throws IOException {
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, image.getBytes());
        log.info("Image saved: {}", filePath);
    }

    public void deleteImage(String imagePath) throws IOException {
        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }

        // Убираем начальный слэш если есть
        if (imagePath.startsWith("/")) {
            imagePath = imagePath.substring(1);
        }

        Path filePath = Paths.get(imagePath);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.info("Image deleted: {}", imagePath);
        }
    }
}
package ru.avito.marketplace.constants;

public class ApiConstants {

    // Пути
    public static final String IMAGES_DIRECTORY = "images/";
    public static final String AVATARS_SUBDIR = "avatars/";
    public static final String ADS_SUBDIR = "ads/";

    // URL
    public static final String DEFAULT_AVATAR_URL = "/images/avatars/default.png";
    public static final String DEFAULT_AD_IMAGE_URL = "/images/ads/default.png";

    // Ограничения
    public static final int MAX_IMAGE_SIZE_MB = 5;
    public static final long MAX_IMAGE_SIZE = MAX_IMAGE_SIZE_MB * 1024 * 1024L; // 5MB

    public static final String[] ALLOWED_IMAGE_TYPES = {
            "image/jpeg",
            "image/png",
            "image/gif"
    };

    // Роли
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    // Сообщения
    public static final String ACCESS_DENIED = "Доступ запрещен";
    public static final String NOT_FOUND = "Объект не найден";
    public static final String UNAUTHORIZED = "Требуется авторизация";

    private ApiConstants() {
        // Утилитный класс
    }
}
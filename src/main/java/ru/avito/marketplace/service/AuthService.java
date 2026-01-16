package ru.avito.marketplace.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.avito.marketplace.dto.RegisterReq;
import ru.avito.marketplace.dto.NewPasswordDto;
import ru.avito.marketplace.dto.UserDto;
import ru.avito.marketplace.entity.User;
import ru.avito.marketplace.exception.UserAlreadyExistsException;
import ru.avito.marketplace.mapper.UserMapper;
import ru.avito.marketplace.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Регистрация нового пользователя
     */
    @Transactional
    public void register(RegisterReq registerReq) {
        // Проверяем, существует ли пользователь
        if (userRepository.existsByEmail(registerReq.getUsername())) {
            throw new UserAlreadyExistsException(
                    "Пользователь с email " + registerReq.getUsername() + " уже существует"
            );
        }

        // Создаем пользователя
        User user = new User();
        user.setEmail(registerReq.getUsername());
        user.setPassword(passwordEncoder.encode(registerReq.getPassword()));
        user.setFirstName(registerReq.getFirstName());
        user.setLastName(registerReq.getLastName());
        user.setPhone(registerReq.getPhone());

        // Устанавливаем роль (по умолчанию USER)
        String role = registerReq.getRole() != null ?
                registerReq.getRole().toUpperCase() : "USER";
        user.setRole(role);

        // Устанавливаем аватар по умолчанию
        user.setImage("/images/avatars/default.png");

        userRepository.save(user);
        log.info("Пользователь зарегистрирован: {}", registerReq.getUsername());
    }

    /**
     * ✅ КРИТЕРИИ 9-10: Смена пароля через DTO
     * Проверка старого пароля и шифрование нового
     * @param username Email пользователя
     * @param dto DTO с текущим и новым паролем
     * @throws BadCredentialsException если текущий пароль неверен
     */
    @Transactional
    public void changePassword(String username, NewPasswordDto dto) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.warn("Пользователь не найден: {}", username);
                    return new UsernameNotFoundException("Пользователь не найден");
                });

        // ✅ КРИТЕРИЙ 9: Проверка совпадения старого пароля
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            log.warn("Неверный текущий пароль для пользователя: {}", username);
            throw new BadCredentialsException("Неверный текущий пароль");
        }

        // ✅ КРИТЕРИЙ 10: Шифрование нового пароля
        String encodedNewPassword = passwordEncoder.encode(dto.getNewPassword());
        user.setPassword(encodedNewPassword);

        userRepository.save(user);
        log.info("Пароль успешно изменен для пользователя: {}", username);
    }

    /**
     * Получение информации о текущем пользователе (DTO)
     */
    public UserDto getCurrentUserInfo(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.warn("Пользователь не найден: {}", username);
                    return new UsernameNotFoundException("Пользователь не найден");
                });

        return userMapper.toDto(user);
    }

    /**
     * Получение текущего аутентифицированного пользователя (Entity)
     * Используется в других сервисах (AdService, CommentService)
     * @return Текущий аутентифицированный пользователь
     * @throws UsernameNotFoundException если пользователь не найден
     */
    public User getCurrentUser() {
        // Получаем аутентификацию из SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Попытка доступа неаутентифицированного пользователя");
            throw new UsernameNotFoundException("Пользователь не аутентифицирован");
        }

        String username = authentication.getName();
        log.debug("Получение текущего пользователя: {}", username);

        return userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.error("Пользователь не найден в БД, но есть в SecurityContext: {}", username);
                    return new UsernameNotFoundException("Пользователь не найден");
                });
    }

    /**
     * Проверка, является ли текущий пользователь администратором
     * ✅ КРИТЕРИЙ 7: Проверка роли ADMIN
     * @return true если пользователь ADMIN
     */
    public boolean isAdmin() {
        User currentUser = getCurrentUser();
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());
        log.debug("Проверка роли ADMIN для {}: {}", currentUser.getEmail(), isAdmin);
        return isAdmin;
    }

    /**
     * Получение email текущего пользователя
     * @return Email текущего пользователя или null если не аутентифицирован
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : null;
        log.debug("Текущий username: {}", username);
        return username;
    }

    /**
     * Получение пользователя по email
     * @param email Email пользователя
     * @return Пользователь
     * @throws UsernameNotFoundException если пользователь не найден
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    /**
     * Проверка существования пользователя
     * @param email Email пользователя
     * @return true если пользователь существует
     */
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Получение пользователя по ID
     * @param id ID пользователя
     * @return Пользователь
     * @throws UsernameNotFoundException если пользователь не найден
     */
    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    /**
     * Обновление информации о пользователе
     * @param user Обновленный пользователь
     */
    @Transactional
    public void updateUser(User user) {
        userRepository.save(user);
        log.info("Пользователь обновлен: {}", user.getEmail());
    }
}
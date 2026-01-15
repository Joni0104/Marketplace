package ru.avito.marketplace.service;

import ru.avito.marketplace.dto.UpdateUserDto;
import ru.avito.marketplace.dto.UserDto;
import ru.avito.marketplace.entity.User;
import ru.avito.marketplace.exception.UserNotFoundException;
import ru.avito.marketplace.mapper.UserMapper;
import ru.avito.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthService authService;
    private final ImageService imageService;

    public UserDto getCurrentUser() {
        String email = authService.getCurrentUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUser(UpdateUserDto updateUserDto) {
        User user = getCurrentUserEntity();
        userMapper.updateEntity(updateUserDto, user);

        User updatedUser = userRepository.save(user);
        log.info("User updated: {}", updatedUser.getEmail());

        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public UserDto updateUserImage(MultipartFile image) throws IOException {
        User user = getCurrentUserEntity();

        // Удаляем старое изображение если есть и это не дефолтное
        if (user.getImage() != null && !user.getImage().contains("default")) {
            imageService.deleteImage(user.getImage());
        }

        // Сохраняем новое изображение
        String imagePath = imageService.saveUserAvatar(image);
        user.setImage(imagePath);

        User updatedUser = userRepository.save(user);
        log.info("User image updated for: {}", updatedUser.getEmail());

        return userMapper.toDto(updatedUser);
    }

    private User getCurrentUserEntity() {
        String email = authService.getCurrentUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }
}
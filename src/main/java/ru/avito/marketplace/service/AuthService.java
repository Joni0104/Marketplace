package ru.avito.marketplace.service;
import ru.avito.marketplace.dto.LoginReq;
import ru.avito.marketplace.dto.LoginRes;
import ru.avito.marketplace.dto.NewPasswordDto;
import ru.avito.marketplace.dto.RegisterReq;
import ru.avito.marketplace.entity.User;
import ru.avito.marketplace.exception.UserAlreadyExistsException;
import ru.avito.marketplace.exception.WrongPasswordException;
import ru.avito.marketplace.mapper.UserMapper;
import ru.avito.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .authorities("ROLE_" + user.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public LoginRes login(LoginReq loginReq) {
        Optional<User> userOptional = userRepository.findByEmail(loginReq.getUsername());

        if (userOptional.isEmpty()) {
            throw new BadCredentialsException("Неверный логин или пароль");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginReq.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Неверный логин или пароль");
        }

        String token = jwtService.generateToken(user);
        return new LoginRes(token);
    }

    @Transactional
    public LoginRes register(RegisterReq registerReq) {
        if (userRepository.existsByEmail(registerReq.getUsername())) {
            throw new UserAlreadyExistsException("Пользователь с email " + registerReq.getUsername() + " уже существует");
        }

        User user = userMapper.toEntity(registerReq);
        user.setPassword(passwordEncoder.encode(registerReq.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());

        String token = jwtService.generateToken(savedUser);
        return new LoginRes(token);
    }

    @Transactional
    public void changePassword(NewPasswordDto newPasswordDto) {
        String currentUsername = getCurrentUsername();
        User user = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Проверяем старый пароль
        if (!passwordEncoder.matches(newPasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new WrongPasswordException("Текущий пароль неверен");
        }

        // Устанавливаем новый пароль
        user.setPassword(passwordEncoder.encode(newPasswordDto.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed for user: {}", currentUsername);
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }

    public User getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
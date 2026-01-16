package ru.avito.marketplace.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.avito.marketplace.entity.Ad;
import ru.avito.marketplace.entity.Comment;
import ru.avito.marketplace.entity.User;
import ru.avito.marketplace.exception.AdNotFoundException;
import ru.avito.marketplace.exception.CommentNotFoundException;
import ru.avito.marketplace.repository.AdRepository;
import ru.avito.marketplace.repository.CommentRepository;
import ru.avito.marketplace.repository.UserRepository;

import java.util.Collection;

@Component("authorizationService")
@Slf4j
@RequiredArgsConstructor
public class AuthorizationService {

    private final AuthService authService;
    private final AdRepository adRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    /**
     * ✅ Для использования в @PreAuthorize SpEL выражении
     * Проверяет, является ли текущий пользователь автором объявления
     */
    public boolean isAuthor(Integer adId, String username) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));
        return ad.getAuthor().getEmail().equals(username);
    }

    /**
     * ✅ Для использования в @PreAuthorize SpEL выражении
     * Проверяет, является ли текущий пользователь автором комментария
     * КРИТЕРИЙ 6: (comment.getUser().equals(currentUser))
     */
    public boolean isCommentAuthor(Integer commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));
        return comment.getAuthor().getEmail().equals(username);
    }

    /**
     * ✅ КРИТЕРИЙ 7: Проверка роли ADMIN
     * Для использования в @PreAuthorize("hasRole('ADMIN')")
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * ✅ Альтернативная проверка ADMIN через роль пользователя в БД
     */
    public boolean isAdmin(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return "ADMIN".equals(user.getRole());
    }

    /**
     * ✅ Комбинированная проверка: ADMIN или автор
     * Для использования в бизнес-логике
     */
    public boolean isAdminOrAuthor(Integer adId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();

        // Проверка на ADMIN
        if (isAdmin(username)) {
            return true;
        }

        // Проверка на автора
        return isAuthor(adId, username);
    }

    /**
     * ✅ Комбинированная проверка для комментариев: ADMIN или автор комментария
     * КРИТЕРИЙ 6
     */
    public boolean isAdminOrCommentAuthor(Integer commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();

        // Проверка на ADMIN
        if (isAdmin(username)) {
            return true;
        }

        // Проверка на автора комментария
        return isCommentAuthor(commentId, username);
    }

    /**
     * ✅ Получение текущего пользователя (удобный метод)
     */
    public User getCurrentUser() {
        return authService.getCurrentUser();
    }

    /**
     * ✅ Проверка, что пользователь может редактировать объявление
     * Включает логгирование для отладки
     */
    public boolean canEditAd(Integer adId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("User not authenticated");
            return false;
        }

        String username = authentication.getName();
        boolean isAdmin = isAdmin(username);
        boolean isAuthor = isAuthor(adId, username);

        log.debug("Authorization check for ad {}: user={}, isAdmin={}, isAuthor={}",
                adId, username, isAdmin, isAuthor);

        return isAdmin || isAuthor;
    }

    /**
     * ✅ Проверка, что пользователь может редактировать комментарий
     * КРИТЕРИЙ 6
     */
    public boolean canEditComment(Integer commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("User not authenticated");
            return false;
        }

        String username = authentication.getName();
        boolean isAdmin = isAdmin(username);
        boolean isCommentAuthor = isCommentAuthor(commentId, username);

        log.debug("Authorization check for comment {}: user={}, isAdmin={}, isCommentAuthor={}",
                commentId, username, isAdmin, isCommentAuthor);

        return isAdmin || isCommentAuthor;
    }
}
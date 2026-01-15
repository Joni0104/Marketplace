package ru.avito.marketplace.service;

import ru.avito.marketplace.dto.CommentDto;
import ru.avito.marketplace.dto.CreateOrUpdateCommentDto;
import ru.avito.marketplace.dto.ResponseWrapper;
import ru.avito.marketplace.entity.Ad;
import ru.avito.marketplace.entity.Comment;
import ru.avito.marketplace.entity.User;
import ru.avito.marketplace.exception.AccessDeniedException;
import ru.avito.marketplace.exception.AdNotFoundException;
import ru.avito.marketplace.exception.CommentNotFoundException;
import ru.avito.marketplace.mapper.CommentMapper;
import ru.avito.marketplace.repository.AdRepository;
import ru.avito.marketplace.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final CommentMapper commentMapper;
    private final AuthService authService;

    public ResponseWrapper<CommentDto> getComments(Integer adId) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Объявление с id " + adId + " не найдено"));

        List<Comment> comments = commentRepository.findAllByAdWithAuthor(ad);
        List<CommentDto> commentDtos = comments.stream()
                .map(commentMapper::toDto)
                .toList();

        return new ResponseWrapper<>(commentDtos.size(), commentDtos);
    }

    @Transactional
    public CommentDto addComment(Integer adId, CreateOrUpdateCommentDto commentDto) {
        User currentUser = authService.getCurrentUser();
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Объявление с id " + adId + " не найдено"));

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setAuthor(currentUser);
        comment.setAd(ad);

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment added to ad id: {} by user: {}", adId, currentUser.getEmail());

        return commentMapper.toDto(savedComment);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @commentService.isAuthor(#commentId, authentication.name)")
    public void deleteComment(Integer adId, Integer commentId) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Объявление с id " + adId + " не найдено"));

        Comment comment = commentRepository.findByIdAndAd(commentId, ad)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий с id " + commentId + " не найден"));

        commentRepository.delete(comment);
        log.info("Comment deleted with id: {} from ad id: {}", commentId, adId);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @commentService.isAuthor(#commentId, authentication.name)")
    public CommentDto updateComment(Integer adId, Integer commentId, CreateOrUpdateCommentDto commentDto) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Объявление с id " + adId + " не найдено"));

        Comment comment = commentRepository.findByIdAndAd(commentId, ad)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий с id " + commentId + " не найден"));

        commentMapper.updateEntity(commentDto, comment);
        Comment updatedComment = commentRepository.save(comment);

        log.info("Comment updated with id: {}", commentId);
        return commentMapper.toDto(updatedComment);
    }

    // Метод для SpEL выражения в @PreAuthorize
    public boolean isAuthor(Integer commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));
        return comment.getAuthor().getEmail().equals(username);
    }

    // Метод для проверки без @PreAuthorize
    public boolean isAuthor(Integer commentId) {
        User currentUser = authService.getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));
        return comment.getAuthor().getId().equals(currentUser.getId());
    }
}
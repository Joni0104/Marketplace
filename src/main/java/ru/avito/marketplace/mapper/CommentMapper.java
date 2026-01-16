package ru.avito.marketplace.mapper;

import org.mapstruct.*;
import ru.avito.marketplace.dto.CommentDto;
import ru.avito.marketplace.dto.CreateOrUpdateCommentDto;
import ru.avito.marketplace.entity.Comment;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    // Преобразование DTO в Entity при создании
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC))")
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "ad", ignore = true)
    Comment toEntity(CreateOrUpdateCommentDto dto);

    // Преобразование Entity в DTO
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorImage", source = "author.image")
    @Mapping(target = "authorFirstName", source = "author.firstName")
    // createdAt мапится автоматически, так как оба типа OffsetDateTime
    CommentDto toDto(Comment comment);

    // Обновление Entity из DTO
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(CreateOrUpdateCommentDto dto, @MappingTarget Comment comment);
}
package ru.avito.marketplace.mapper;

import ru.avito.marketplace.config.MapStructConfig;
import ru.avito.marketplace.dto.CommentDto;
import ru.avito.marketplace.dto.CreateOrUpdateCommentDto;
import ru.avito.marketplace.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Comment toEntity(CreateOrUpdateCommentDto createOrUpdateCommentDto);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorImage", source = "author.image")
    @Mapping(target = "authorFirstName", source = "author.firstName")
    CommentDto toDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(CreateOrUpdateCommentDto updateCommentDto, @org.mapstruct.MappingTarget Comment comment);
}
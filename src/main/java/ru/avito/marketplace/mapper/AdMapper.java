package ru.avito.marketplace.mapper;

import ru.avito.marketplace.config.MapStructConfig;
import ru.avito.marketplace.dto.AdDto;
import ru.avito.marketplace.dto.CreateOrUpdateAdDto;
import ru.avito.marketplace.dto.ExtendedAdDto;
import ru.avito.marketplace.entity.Ad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class, uses = UserMapper.class)
public interface AdMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Ad toEntity(CreateOrUpdateAdDto createOrUpdateAdDto);

    AdDto toAdDto(Ad ad);

    @Mapping(target = "authorFirstName", source = "author.firstName")
    @Mapping(target = "authorLastName", source = "author.lastName")
    @Mapping(target = "email", source = "author.email")
    @Mapping(target = "phone", source = "author.phone")
    ExtendedAdDto toExtendedAdDto(Ad ad);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "comments", ignore = true)
    void updateEntity(CreateOrUpdateAdDto updateAdDto, @org.mapstruct.MappingTarget Ad ad);
}

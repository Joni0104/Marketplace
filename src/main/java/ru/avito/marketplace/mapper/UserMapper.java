package ru.avito.marketplace.mapper;

import ru.avito.marketplace.config.MapStructConfig;
import ru.avito.marketplace.dto.RegisterReq;
import ru.avito.marketplace.dto.UpdateUserDto;
import ru.avito.marketplace.dto.UserDto;
import ru.avito.marketplace.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ads", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "image", constant = "/images/avatars/default.png")
    @Mapping(target = "role", source = "role", defaultValue = "USER")
    @Mapping(target = "email", source = "username")
    User toEntity(RegisterReq registerReq);

    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "ads", ignore = true)
    @Mapping(target = "comments", ignore = true)
    void updateEntity(UpdateUserDto updateUserDto, @org.mapstruct.MappingTarget User user);
}
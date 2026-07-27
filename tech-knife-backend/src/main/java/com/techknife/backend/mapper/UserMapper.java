package com.techknife.backend.mapper;

import com.techknife.backend.dto.AuthResponse;
import com.techknife.backend.dto.UserResponse;
import com.techknife.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "refreshToken", source = "refreshToken")
    @Mapping(target = "tokenType", constant = "Bearer")
    @Mapping(target = "expiresInMs", source = "expiresInMs")
    AuthResponse toAuthResponse(User user, String accessToken, String refreshToken, long expiresInMs);

    UserResponse toUserResponse(User user);
}

package com.example.dto;

import com.example.entity.User;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String name;

    @Builder
    public UserDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static UserDto from(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public User toEntity() {
        return User.builder()
                .id(id)
                .name(name)
                .build();
    }

}

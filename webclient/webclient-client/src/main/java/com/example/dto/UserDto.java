package com.example.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String name;

    @Builder
    public UserDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}

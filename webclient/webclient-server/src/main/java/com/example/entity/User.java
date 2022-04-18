package com.example.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @Builder
    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}

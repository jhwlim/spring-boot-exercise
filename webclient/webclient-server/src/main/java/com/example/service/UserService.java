package com.example.service;

import com.example.dto.UserDto;
import com.example.entity.User;
import com.example.exception.UserNameDuplicatedException;
import com.example.exception.UserNotFoundException;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto getUser(long id) {
        return UserDto.from(getUserById(id));
    }

    public UserDto saveUser(UserDto user) {
        userRepository.findByName(user.getName())
                .ifPresent(entity -> {
                    throw new UserNameDuplicatedException();
                });
        return UserDto.from(userRepository.save(user.toEntity()));
    }

    public void removeUser(long id) {
        userRepository.delete(getUserById(id));
    }

    private User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

}

package com.colin.novelanalysis.application.service;

import com.colin.novelanalysis.application.dto.UserCreateCommand;
import com.colin.novelanalysis.application.dto.UserDTO;
import com.colin.novelanalysis.domain.model.User;
import com.colin.novelanalysis.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

/**
 * 用户应用服务
 */
@Service
@Validated
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;

    public UserDTO create(@Valid UserCreateCommand command) {
        User user = User.builder()
                .username(command.getUsername())
                .email(command.getEmail())
                .status(1)
                .build();
        User saved = userRepository.save(user);
        return UserDTO.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .createTime(saved.getCreateTime())
                .build();
    }

    public UserDTO getById(Long id) {
        return userRepository.findById(id)
                .map(u -> UserDTO.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .email(u.getEmail())
                        .createTime(u.getCreateTime())
                        .build())
                .orElse(null);
    }
}

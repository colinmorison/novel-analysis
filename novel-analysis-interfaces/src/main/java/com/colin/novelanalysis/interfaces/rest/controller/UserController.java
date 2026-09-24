package com.colin.novelanalysis.interfaces.rest.controller;

import com.colin.novelanalysis.application.dto.UserCreateCommand;
import com.colin.novelanalysis.application.dto.UserDTO;
import com.colin.novelanalysis.application.service.UserApplicationService;
import com.colin.novelanalysis.interfaces.rest.dto.ApiResult;
import com.colin.novelanalysis.interfaces.rest.dto.UserCreateRequest;
import com.colin.novelanalysis.interfaces.rest.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserApplicationService userApplicationService;

    @PostMapping
    public Mono<ApiResult<UserResponse>> create(@RequestBody @Valid UserCreateRequest request) {
        return Mono.fromCallable(() -> {
            UserDTO dto = userApplicationService.create(UserCreateCommand.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .build());
            return ApiResult.ok(UserResponse.builder()
                    .id(dto.getId())
                    .username(dto.getUsername())
                    .email(dto.getEmail())
                    .createTime(dto.getCreateTime())
                    .build());
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/{id}")
    public Mono<ApiResult<UserResponse>> getById(@PathVariable Long id) {
        return Mono.fromCallable(() -> {
            UserDTO dto = userApplicationService.getById(id);
            if (dto == null) {
                return ApiResult.<UserResponse>fail("404", "用户不存在");
            }
            return ApiResult.ok(UserResponse.builder()
                    .id(dto.getId())
                    .username(dto.getUsername())
                    .email(dto.getEmail())
                    .createTime(dto.getCreateTime())
                    .build());
        }).subscribeOn(Schedulers.boundedElastic());
    }
}

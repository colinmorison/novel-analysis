package com.colin.novelanalysis.domain.repository;

import com.colin.novelanalysis.domain.model.User;

import java.util.Optional;

/**
 * 用户仓储接口
 */
public interface UserRepository {

    Optional<User> findById(Long id);

    User save(User user);
}

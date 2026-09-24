package com.colin.novelanalysis.infrastructure.persistence.mapper;

import com.colin.novelanalysis.domain.model.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper {

    int insert(User user);

    User selectById(Long id);
}

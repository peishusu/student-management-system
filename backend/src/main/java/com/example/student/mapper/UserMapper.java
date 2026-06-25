package com.example.student.mapper;

import com.example.student.entity.SystemUser;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    SystemUser selectById(@Param("id") Long id);

    SystemUser selectByUsername(@Param("username") String username);

    int insert(SystemUser user);
}

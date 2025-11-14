package com.campus.trade.service;

import com.campus.trade.dto.UserLoginDTO;
import com.campus.trade.dto.UserRegisterDTO;
import com.campus.trade.entity.User;

import java.util.Map;

/**
 * 用户服务接口
 * 
 * @author 开发团队
 */
public interface UserService {
    
    /**
     * 用户注册
     * 
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    boolean register(UserRegisterDTO registerDTO);
    
    /**
     * 用户登录
     * 
     * @param loginDTO 登录信息
     * @return 登录结果，包含token和用户信息
     */
    Map<String, Object> login(UserLoginDTO loginDTO);
    
    /**
     * 根据ID获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(Long userId);
    
    /**
     * 根据用户名获取用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
}

package com.campus.trade.service;

import com.campus.trade.dto.UserLoginDTO;
import com.campus.trade.dto.UserRegisterDTO;
import com.campus.trade.entity.User;
import org.springframework.web.multipart.MultipartFile;

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
     * 检查学号是否存在
     * 
     * @param studentId 学号
     * @return 是否存在
     */
    boolean existsByStudentId(String studentId);
    
    /**
     * 更新用户信息
     * 
     * @param userId 用户ID
     * @param updateDTO 更新信息
     * @return 是否成功
     */
    boolean updateProfile(Long userId, UserRegisterDTO updateDTO);
    
    /**
     * 修改密码
     * 
     * @param userId 用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 上传头像
     * 
     * @param userId 用户ID
     * @param file 头像文件
     * @return 头像URL
     */
    String uploadAvatar(Long userId, MultipartFile file);
    
    /**
     * 获取用户统计信息
     * 
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getUserStats(Long userId);
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
}

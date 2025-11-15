package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.trade.dto.UserLoginDTO;
import com.campus.trade.dto.UserRegisterDTO;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.UserService;
import com.campus.trade.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

// import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用户服务实现类
 * 
 * @author 开发团队
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    // private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public boolean register(UserRegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (existsByUsername(registerDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 检查密码确认
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }
        
        // 创建用户对象
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        
        // 使用明文密码（根据需求不加密）
        String encodedPassword = registerDTO.getPassword();
        user.setPassword(encodedPassword);
        user.setStatus(1); // 默认状态为正常
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // 保存用户
        return userMapper.insert(user) > 0;
    }
    
    @Override
    public Map<String, Object> login(UserLoginDTO loginDTO) {
        // 查找用户（支持用户名或邮箱登录）
        User user = null;
        if (loginDTO.getUsername().contains("@")) {
            user = userMapper.selectByEmail(loginDTO.getUsername());
        } else {
            user = userMapper.selectByUsername(loginDTO.getUsername());
        }
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证密码（明文比较）
        if (!loginDTO.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new RuntimeException("账户已被禁用");
        }
        
        // 生成JWT token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        
        // 返回用户信息（不包含密码）
        user.setPassword(null);
        result.put("user", user);
        
        return result;
    }
    
    @Override
    public User getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return user;
    }
    
    @Override
    public User getUserByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return user;
    }
    
    @Override
    public boolean existsByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    public boolean existsByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return userMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    public boolean existsByStudentId(String studentId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        return userMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    public boolean updateProfile(Long userId, UserRegisterDTO updateDTO) {
        try {
            User user = new User();
            BeanUtils.copyProperties(updateDTO, user);
            user.setId(userId);
            user.setUpdatedAt(LocalDateTime.now());
            // 不更新密码
            user.setPassword(null);
            return userMapper.updateById(user) > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新用户信息失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 验证原密码（明文比较）
            if (!oldPassword.equals(user.getPassword())) {
                return false;
            }
            
            // 更新为新密码（明文存储）
            user.setPassword(newPassword);
            user.setUpdatedAt(LocalDateTime.now());
            return userMapper.updateById(user) > 0;
        } catch (Exception e) {
            throw new RuntimeException("修改密码失败: " + e.getMessage());
        }
    }
    
    @Value("${file.upload.path:/uploads/}")
    private String uploadPath;
    
    @Override
    public String uploadAvatar(Long userId, MultipartFile file) {
        try {
            // 检查文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !isImageFile(originalFilename)) {
                throw new RuntimeException("只支持图片文件");
            }
            
            // 创建上传目录
            String dateDir = LocalDate.now().toString();
            Path uploadDir = Paths.get(uploadPath, "avatars", dateDir);
            Files.createDirectories(uploadDir);
            
            // 生成文件名
            String extension = getFileExtension(originalFilename);
            String filename = UUID.randomUUID().toString() + "." + extension;
            Path filePath = uploadDir.resolve(filename);
            
            // 保存文件
            Files.copy(file.getInputStream(), filePath);
            
            // 更新用户头像
            String avatarUrl = "/uploads/avatars/" + dateDir + "/" + filename;
            User user = new User();
            user.setId(userId);
            user.setAvatar(avatarUrl);
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
            
            return avatarUrl;
        } catch (IOException e) {
            throw new RuntimeException("头像上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 这里可以添加统计查询，暂时返回模拟数据
        stats.put("productCount", 0);  // 发布商品数
        stats.put("orderCount", 0);    // 订单数
        stats.put("messageCount", 0);  // 消息数
        stats.put("favoriteCount", 0); // 收藏数
        
        return stats;
    }
    
    /**
     * 检查是否为图片文件
     */
    private boolean isImageFile(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return extension.matches("jpg|jpeg|png|gif|bmp|webp");
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1) : "";
    }
}

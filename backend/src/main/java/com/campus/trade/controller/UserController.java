package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.UserRegisterDTO;
import com.campus.trade.entity.User;
import com.campus.trade.service.UserService;
import com.campus.trade.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * 用户个人中心控制器
 * 
 * @author 开发团队
 */
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    /**
     * 获取个人信息
     */
    @GetMapping("/profile")
    public Result<User> getProfile(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            User user = userService.getUserById(userId);
            if (user != null) {
                // 不返回密码信息
                user.setPassword(null);
                return Result.success(user);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新个人信息
     */
    @PutMapping("/profile")
    public Result<String> updateProfile(@Valid @RequestBody UserRegisterDTO updateDTO,
                                      HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            boolean success = userService.updateProfile(userId, updateDTO);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<String> changePassword(@RequestParam String oldPassword,
                                       @RequestParam String newPassword,
                                       HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            boolean success = userService.changePassword(userId, oldPassword, newPassword);
            if (success) {
                return Result.success("密码修改成功");
            } else {
                return Result.error("原密码错误");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file,
                                     HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            String avatarUrl = userService.uploadAvatar(userId, file);
            return Result.success("头像上传成功", avatarUrl);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getUserStats(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            Map<String, Object> stats = userService.getUserStats(userId);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtils.getUserIdFromToken(token);
        }
        throw new RuntimeException("未授权访问");
    }
}

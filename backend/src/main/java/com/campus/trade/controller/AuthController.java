package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.UserLoginDTO;
import com.campus.trade.dto.UserRegisterDTO;
import com.campus.trade.service.UserService;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 认证控制器
 * 
 * @author 开发团队
 */
@RestController
@RequestMapping("/auth")
// @Api(tags = "用户认证")
@CrossOrigin
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    // @ApiOperation("用户注册")
    public Result<String> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        try {
            boolean success = userService.register(registerDTO);
            if (success) {
                return Result.success("注册成功");
            } else {
                return Result.error("注册失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    // @ApiOperation("用户登录")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        try {
            Map<String, Object> result = userService.login(loginDTO);
            return Result.success("登录成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check-username")
    // @ApiOperation("检查用户名是否存在")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return Result.success(exists);
    }
    
    /**
     * 检查邮箱是否存在
     */
    @GetMapping("/check-email")
    // @ApiOperation("检查邮箱是否存在")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return Result.success(exists);
    }
}

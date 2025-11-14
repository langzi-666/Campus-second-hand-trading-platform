package com.campus.trade.controller;

import com.campus.trade.common.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 * 
 * @author 开发团队
 */
@RestController
@RequestMapping("/test")
@CrossOrigin
public class TestController {
    
    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("系统运行正常");
    }
    
    /**
     * 版本信息接口
     */
    @GetMapping("/version")
    public Result<String> version() {
        return Result.success("校园二手交易平台 v1.0.0");
    }
}

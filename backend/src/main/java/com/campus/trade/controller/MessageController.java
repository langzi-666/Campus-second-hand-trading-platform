package com.campus.trade.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.trade.common.Result;
import com.campus.trade.dto.MessageSendDTO;
import com.campus.trade.entity.Message;
import com.campus.trade.service.MessageService;
import com.campus.trade.utils.JwtUtils;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 消息控制器
 * 
 * @author 开发团队
 */
@RestController
@RequestMapping("/messages")
// @Api(tags = "消息管理")
@CrossOrigin
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    /**
     * 发送消息
     */
    @PostMapping
    // @ApiOperation("发送消息")
    public Result<Long> sendMessage(@Valid @RequestBody MessageSendDTO sendDTO,
                                  HttpServletRequest request) {
        try {
            Long senderId = getUserIdFromRequest(request);
            Long messageId = messageService.sendMessage(sendDTO, senderId);
            return Result.success("消息发送成功", messageId);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取会话消息
     */
    @GetMapping("/conversation")
    // @ApiOperation("获取会话消息")
    public Result<IPage<Message>> getConversationMessages(@RequestParam(defaultValue = "1") Integer page,
                                                        @RequestParam(defaultValue = "20") Integer size,
                                                        @RequestParam Long userId2,
                                                        @RequestParam(required = false) Long productId,
                                                        HttpServletRequest request) {
        try {
            Long userId1 = getUserIdFromRequest(request);
            IPage<Message> messagePage = messageService.getConversationMessages(page, size, userId1, userId2, productId);
            
            // 标记消息为已读
            messageService.markMessagesAsRead(userId1, userId2, productId);
            
            return Result.success(messagePage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取会话列表
     */
    @GetMapping("/conversations")
    // @ApiOperation("获取会话列表")
    public Result<List<Message>> getUserConversations(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            List<Message> conversations = messageService.getUserConversations(userId);
            return Result.success(conversations);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 标记消息为已读
     */
    @PutMapping("/read")
    // @ApiOperation("标记消息为已读")
    public Result<String> markAsRead(@RequestParam Long senderId,
                                   @RequestParam(required = false) Long productId,
                                   HttpServletRequest request) {
        try {
            Long receiverId = getUserIdFromRequest(request);
            messageService.markMessagesAsRead(receiverId, senderId, productId);
            return Result.success("标记成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread/count")
    // @ApiOperation("获取未读消息数量")
    public Result<Integer> getUnreadCount(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            Integer count = messageService.countUnreadMessages(userId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取最新消息
     */
    @GetMapping("/latest")
    // @ApiOperation("获取最新消息")
    public Result<Message> getLatestMessage(@RequestParam Long userId2,
                                          @RequestParam(required = false) Long productId,
                                          HttpServletRequest request) {
        try {
            Long userId1 = getUserIdFromRequest(request);
            Message message = messageService.getLatestMessage(userId1, userId2, productId);
            return Result.success(message);
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

package com.haigui.haigui.controller;

import com.haigui.haigui.model.ChatRoom;
import com.haigui.haigui.service.ChatService;
import lombok.Data; // Import Lombok for cleaner DTO
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * AI 对话接口
 */
@RestController
@RequestMapping("/chat") // 统一接口前缀
public class ChatController {

    @Resource
    private ChatService chatService;

    // 构造器注入
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 使用 RequestBody 传递 userId 和 message
     * 调用示例: POST /api/chat/123/send
     * Body: { "userId": "user_abc123", "message": "你好AI" }
     */
    @PostMapping("/{roomId}/send")
    public String doChat(
            @PathVariable long roomId,
            @RequestBody ChatRequest request) {
        return chatService.doChat(roomId, request.getUserId(), request.getMessage());
    }

    /**
     * 获取聊天室列表
     * 调用示例: GET /api/chat/rooms
     */
    @GetMapping("/rooms")
    public List<ChatRoom> getChatRooms() {
        return chatService.getChatRoomList();
    }

    /**
     * 获取特定房间的历史聊天记录
     * 调用示例: GET /api/chat/history?roomId=123
     */
    @GetMapping("/history")
    public ChatRoom getChatHistory(@RequestParam long roomId) {
        return chatService.getChatRoom(roomId);
    }

    /**
     * 删除特定聊天室 (需要权限校验)
     * 调用示例: DELETE /api/chat/rooms/{roomId}?userId=user_abc123
     */
    @DeleteMapping("/rooms/{roomId}")
    public Map<String, Object> deleteChatRoom(
            @PathVariable long roomId,
            @RequestParam String userId) {
        boolean success = chatService.deleteChatRoom(roomId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        if (success) {
            response.put("message", "聊天室删除成功");
        } else {
            response.put("message", "删除失败：聊天室不存在或您没有权限");
        }
        return response;
    }

    // 辅助 DTO 用于接收请求体
    @Data // Use Lombok to generate getters/setters
    static class ChatRequest {
        private String userId;
        private String message;
    }
}
package com.haigui.haigui.controller;

import com.haigui.haigui.model.ChatRoom;
import com.haigui.haigui.service.ChatService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
     * 使用 Query Parameter 传递 message
     * 适用场景: 简短消息 + 简单调试
     * 调用示例: POST /api/chat/123/send?message=你好AI
     */
    @PostMapping("/{roomId}/send")
    public String doChat(
            @PathVariable long roomId,
            @RequestParam String message) {
        return chatService.doChat(roomId, message);
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
        ChatRoom room = chatService.getChatRoom(roomId);
        return room != null ? room : new ChatRoom();
    }

    /**
     * 删除特定聊天室
     * 调用示例: DELETE /api/chat/rooms/{roomId}
     */
    @DeleteMapping("/rooms/{roomId}")
    public Map<String, Object> deleteChatRoom(@PathVariable long roomId) {
        boolean success = chatService.deleteChatRoom(roomId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "聊天室删除成功" : "聊天室不存在或删除失败");
        return response;
    }

    // 辅助 DTO 用于接收请求体
    static class ChatRequest {
        private String message;

        // Getter/Setter 保证 JSON 反序列化
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
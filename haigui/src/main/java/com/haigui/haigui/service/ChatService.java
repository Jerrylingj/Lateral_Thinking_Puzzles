package com.haigui.haigui.service;

import com.haigui.haigui.model.ChatRoom;

import java.util.List;


/**
 * 聊天服务
 */
public interface ChatService {

    /**
     * 和AI对话
     * @param roomId 聊天室ID
     * @param message 用户输入
     * @return AI 返回结果
     */
    String doChat(long roomId, String message);

    /**
     * 获取对话列表
     *
     * @return 聊天室列表
     */
    List<ChatRoom> getChatRoomList();
}

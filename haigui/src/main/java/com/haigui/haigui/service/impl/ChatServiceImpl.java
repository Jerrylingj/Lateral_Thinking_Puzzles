package com.haigui.haigui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haigui.haigui.manager.AiManager;
import com.haigui.haigui.mapper.ChatRoomMapper;
import com.haigui.haigui.mapper.MessageMapper;
import com.haigui.haigui.model.ChatRoom;
import com.haigui.haigui.model.Message;
import com.haigui.haigui.service.ChatService;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);

    private static final String SYSTEM_PROMPT = "你是一个恐怖海龟汤主持人。你的任务是引导玩家通过提问来解开一个悬疑故事的真相（即“汤底”）。\n" +
            "【规则】:\n" +
            "1.  **【强制】** 你的所有回复都必须极其简洁，只能回答“是”、“否”、“是，但无关紧要”或进行极简短的引导。\n" +
            "2.  **【强制】** 揭晓答案时，你的回复必须以 `【汤底揭晓】` 作为固定开头。\n" +
            "3.  不要主动提供任何未经玩家提问的信息。\n" +
            "4.  如果玩家的问题含糊不清，引导他问得更具体。\n" +
            "5.  如果玩家主动说“结束游戏”或“揭晓答案”，请直接揭晓汤底。\n" +
            "\n" +
            "【游戏启动模板】:\n" +
            "\"开始游戏 | 难度：★★★★☆ 情景：游乐园午夜12点，小丑玩偶眼眶渗出机油，过山车上发现失踪者手握断线控制器\"\n" +
            "\n" +
            "【汤底揭晓模板】:\n" +
            "\"【汤底揭晓】断线控制器是死者启动的紧急制动，但因年久失修的缆绳断裂，未能阻止悲剧。小丑的机油只是一个巧合的恐怖点缀。\"";

    @Resource
    private AiManager aiManager;

    @Resource
    private ChatRoomMapper chatRoomMapper;

    @Resource
    private MessageMapper messageMapper;

    @Override
    @Transactional
    public String doChat(long roomId, String userId, String message) {
        ChatRoom room = chatRoomMapper.selectById(roomId);
        if (room == null) {
            if ("开始游戏".equals(message)) {
                room = new ChatRoom();
                room.setId(roomId);
                room.setUserId(userId);
                room.setStatus("PLAYING");
                chatRoomMapper.insert(room);
            } else {
                return "请不要输入无关信息哟 ~ 快点 \"开始游戏\" 吧！";
            }
        }

        if ("FINISHED".equals(room.getStatus())) {
            return "这局游戏已经结束了哦，请开始新游戏吧。";
        }

        Message userMessage = Message.builder()
                .roomId(roomId)
                .role(ChatMessageRole.USER.name())
                .content(message)
                .build();
        messageMapper.insert(userMessage);
        logger.info("[Room {}] Saved user message to DB: {}", roomId, message);

        List<Message> dbMessages = getMessagesByRoomId(roomId);
        List<ChatMessage> historyForAI = convertDbMessagesToSdkMessages(dbMessages);

        String aiResponse = aiManager.doChat(historyForAI);

        Message aiMessage = Message.builder()
                .roomId(roomId)
                .role(ChatMessageRole.ASSISTANT.name())
                .content(aiResponse)
                .build();
        messageMapper.insert(aiMessage);
        logger.info("[Room {}] Saved AI response to DB: {}", roomId, aiResponse);

        if ("结束游戏".equals(message) || aiResponse.startsWith("【汤底揭晓】")) {
            room.setStatus("FINISHED");
            chatRoomMapper.updateById(room);
        }

        return aiResponse;
    }

    @Override
    public List<ChatRoom> getChatRoomList() {
        List<ChatRoom> rooms = chatRoomMapper.selectList(null);
        if (rooms.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> roomIds = rooms.stream().map(ChatRoom::getId).collect(Collectors.toList());

        QueryWrapper<Message> messagesQuery = new QueryWrapper<>();
        messagesQuery.in("room_id", roomIds).orderByAsc("created_at");
        List<Message> allMessages = messageMapper.selectList(messagesQuery);
        logger.info("Fetched {} total messages from DB for {} rooms.", allMessages.size(), rooms.size());

        Map<Long, List<Message>> messagesByRoomId = allMessages.stream()
                .collect(Collectors.groupingBy(Message::getRoomId));

        rooms.forEach(room -> {
            List<Message> roomMessages = messagesByRoomId.getOrDefault(room.getId(), Collections.emptyList());
            room.setChatMessageList(convertDbMessagesToMapList(roomMessages));
        });

        return rooms;
    }

    @Override
    @Transactional
    public boolean deleteChatRoom(long roomId, String userId) {
        ChatRoom room = chatRoomMapper.selectById(roomId);
        if (room != null && userId != null && userId.equals(room.getUserId())) {
            QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("room_id", roomId);
            messageMapper.delete(queryWrapper);
            chatRoomMapper.deleteById(roomId);
            return true;
        }
        return false;
    }

    @Override
    public ChatRoom getChatRoom(long roomId) {
        ChatRoom room = chatRoomMapper.selectById(roomId);
        if (room != null) {
            List<Message> dbMessages = getMessagesByRoomId(roomId);
            room.setChatMessageList(convertDbMessagesToMapList(dbMessages));
        }
        return room;
    }

    private List<Message> getMessagesByRoomId(long roomId) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_id", roomId).orderByAsc("created_at");
        return messageMapper.selectList(queryWrapper);
    }

    private List<ChatMessage> convertDbMessagesToSdkMessages(List<Message> dbMessages) {
        List<ChatMessage> sdkMessages = new java.util.ArrayList<>();
        sdkMessages.add(ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(SYSTEM_PROMPT).build());

        List<ChatMessage> historyMessages = dbMessages.stream()
                .map(dbMessage -> ChatMessage.builder()
                        .role(ChatMessageRole.valueOf(dbMessage.getRole()))
                        .content(dbMessage.getContent())
                        .build())
                .collect(Collectors.toList());
        
        sdkMessages.addAll(historyMessages);
        return sdkMessages;
    }

    private List<Map<String, Object>> convertDbMessagesToMapList(List<Message> dbMessages) {
        return dbMessages.stream().map(dbMessage -> {
            Map<String, Object> map = new HashMap<>();
            map.put("role", dbMessage.getRole());
            map.put("content", dbMessage.getContent());
            map.put("timestamp", dbMessage.getCreatedAt().toString());
            // 前端历史记录需要 'sender' 字段
            if (ChatMessageRole.USER.name().equals(dbMessage.getRole())) {
                map.put("sender", "user");
            } else if (ChatMessageRole.ASSISTANT.name().equals(dbMessage.getRole())) {
                map.put("sender", "ai");
            }
            return map;
        }).collect(Collectors.toList());
    }
}

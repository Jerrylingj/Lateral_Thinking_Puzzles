package com.haigui.haigui.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 聊天室
 */
@Data
@TableName("chat_room")
public class ChatRoom {

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * 该字段在数据库表中不存在，仅用于业务逻辑中传递消息列表
     */
    @TableField(exist = false)
    private List<Map<String, Object>> chatMessageList;

    /**
     * 为了兼容旧代码的 roomId 字段
     * @return
     */
    public Long getRoomId() {
        return this.id;
    }
}

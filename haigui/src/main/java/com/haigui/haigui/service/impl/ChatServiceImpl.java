package com.haigui.haigui.service.impl;

import com.haigui.haigui.manager.AiManager;
import com.haigui.haigui.model.ChatRoom;
import com.haigui.haigui.service.ChatService;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {
    @Resource
    private AiManager aiManager;

    // 全局消息记录
    final Map<Long, List<ChatMessage>> globalChatMessagesMap = new HashMap<Long, List<ChatMessage>>();

    @Override
    public String doChat(long roomId, String message) {
        // 1. 准备消息列表
        System.out.println("[Room {" + roomId + "}] 玩家: {" + message + "}");
        List<ChatMessage> messages = new ArrayList<>();
        // 系统预设
        String systemPrompt = "恐怖海龟汤AI主持规则▎核心定位\n" +
                "⚠ 强制规则 ⚠\n" +
                "1. 每次仅响应【当前最后一条】玩家提问\n" +
                "2. 输出必须为单轮对话（禁止连续剧情）\n" +
                "用现实机制营造心理恐惧（幽闭/时间压力/认知失调），禁用超自然元素\n" +
                "▎谜题架构\n" +
                "1. 恐怖情景模版（必含元素→封闭空间/异常声响/时间悖论）\n" +
                "2. 五阶段致死链： 环境异常→设备故障→人为失误→心理崩溃→现实杀机\n" +
                "3. 感官强化词库： [吱呀声][锈味][闪烁代码]等可验证的物理线索\n" +
                "◉应答规范:\n" +
                "1. 关键线索确认用:是！▶[事件描述]（进度★x%）\n" +
                "2. 死亡梯度:轻度→（纸张声） 致命→（金属声）\n" +
                "3. 超自然提问→引导物理验证（例：检查时间戳）\n\n" +
                "▎压缩策略\n" +
                "1. 用符号系统代替文字描述（⚠=禁止 ▷=引导触发）\n" +
                "2. 合并同类机制（将「逻辑校验」与「恐怖要素」绑定）\n" +
                "| 阶段       | 格式模板                              | 示例                                                         |\n"
                +
                "| ---------- | ------------------------------------- | ------------------------------------------------------------ |\n"
                +
                "| 游戏启动   | 「开始游戏 ▏难度 + 情景」             | 「开始游戏 ▏难度：★★★★☆ 情景：游乐园午夜12点，小丑玩偶眼眶渗出机油，过山车上发现失踪者手握断线控制器」 |\n"
                +
                "| 确认「是」 | 是！([物理参数])进度[数值]% ▶[梯度词] | 是！(液压压力<2MPa)60% ▶金属疲劳声                           |\n" +
                "| 否定「否」 | 否！([反证据]) ▶[阻碍词]              | 否！(无无线模块)40% ▶齿轮卡顿                              |\n" +
                "| 部分正确   | 部分正确！▷引导提示                   | 部分正确！▷注意关注【xxx】和【xxx】的关联！                   |\n" +
                "| 不相关     | 不相关！当前锁定[核心机制]            | 不想管！焦点为【液压传动】                                   |\n" +
                "| 结束游戏   | 游戏结束，汤底揭晓：完整汤底逻辑链    | 游戏结束，汤底揭晓：                                          |\n";

        final ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(systemPrompt)
                .build();
        final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(message).build();

        if (message.equals("开始游戏") && !globalChatMessagesMap.containsKey(roomId)) {
            // 首次开始
            globalChatMessagesMap.put(roomId, messages);
            messages.add(systemMessage);
        } else if (globalChatMessagesMap.containsKey(roomId)) {
            // 非首次开始
            messages = globalChatMessagesMap.get(roomId);
        } else {
            return "请不要输入无关信息哟 ~ 快点 \"开始游戏\" 吧！";
        }
        globalChatMessagesMap.get(roomId).add(userMessage);
        messages.add(userMessage);

        // 2. 调用AI, 并维护消息记录
        String ans = aiManager.doChat(messages);
        final ChatMessage assistantMessage = ChatMessage.builder().role(ChatMessageRole.ASSISTANT).content(ans).build();
        globalChatMessagesMap.get(roomId).add(assistantMessage);

        // 3. 返回消息
        // 游戏结束,清除内存
        System.out.println("[Room {" + roomId + "}] AI: {" + ans + "}");
        if (ans.contains("游戏结束")) {
            globalChatMessagesMap.remove(roomId);
            System.out.println("游戏结束");
        }
        return ans;
    }

    @Override
    public List<ChatRoom> getChatRoomList() {
        List<ChatRoom> chatRoomList = new ArrayList<>();
        for (Map.Entry<Long, List<ChatMessage>> roomIdMessageListEntry : globalChatMessagesMap.entrySet()) {
            ChatRoom room = new ChatRoom();
            room.setRoomId(roomIdMessageListEntry.getKey());
            room.setChatMessageList(roomIdMessageListEntry.getValue());
            chatRoomList.add(room);
        }

        return chatRoomList;
    }

    @Override
    public boolean deleteChatRoom(long roomId) {
        // 如果房间存在，删除它并返回true
        if (globalChatMessagesMap.containsKey(roomId)) {
            globalChatMessagesMap.remove(roomId);
            System.out.println("[Room " + roomId + "] 已删除");
            return true;
        }

        // 房间不存在，返回false
        return false;
    }

    @Override
    public ChatRoom getChatRoom(long roomId) {
        // 如果房间存在，构建并返回ChatRoom对象
        if (globalChatMessagesMap.containsKey(roomId)) {
            ChatRoom room = new ChatRoom();
            room.setRoomId(roomId);
            room.setChatMessageList(globalChatMessagesMap.get(roomId));
            return room;
        }

        // 房间不存在，返回null
        return null;
    }
}

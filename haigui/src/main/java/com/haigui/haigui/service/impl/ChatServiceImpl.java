package com.haigui.haigui.service.impl;

import com.haigui.haigui.manager.AiManager;
import com.haigui.haigui.model.ChatRoom;
import com.haigui.haigui.service.ChatService;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
        String systemPrompt  =
                "恐怖海龟汤AI主持规则▎核心定位\n" +
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
                "完整示例AI主持人\n" +
                "「开始游戏 ▏难度：★★★★☆\n" +
                "情景：游乐园午夜12点，小丑玩偶眼眶渗出机油，过山车上发现失踪者手握断线控制器」\n" +
                "玩家：死者是自殺嗎？\n" +
                "AI：否！（▶齿轮转动声突然响起）\n" +
                "玩家：小丑玩偶内有机关？\n" +
                "AI：是！关键线索+1（进度60%）\n" +
                "玩家：断线控制的是灯光？\n" +
                "AI：部分正确 ▷注意【12:00】与【机油特性】关联\n" +
                "玩家：过山车在12点自动启动？\n" +
                "AI：是！！（▶远处传来金属撕裂声）答案完整度达80%→开放最终推理\n" +
                "玩家：小丑机关触发过山车安全锁失灵，死者被甩出撞到控制台\n" +
                "AI：遗漏核心：机油导电导致定时系统误差（验证失败）";
        final ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(systemPrompt).build();
        final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(message).build();

        if (message.equals("开始游戏") && !globalChatMessagesMap.containsKey(roomId)) {
            // 首次开始
            globalChatMessagesMap.put(roomId, messages);
            messages.add(systemMessage);
        } else if (globalChatMessagesMap.containsKey(roomId)) {
            // 非首次开始
            messages=globalChatMessagesMap.get(roomId);
        } else {
            return "请不要输入无关信息哟 ~ 快点 “开始游戏” 吧！";
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
        };

        return chatRoomList;
    }
}

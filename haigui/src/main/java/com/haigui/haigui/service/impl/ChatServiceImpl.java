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
                "[恐怖海龟汤AI规则]\n" +
                "▶现实恐怖机制（禁用超自然）\n" +
                "▌致死五阶段:环境异常→设备故障→人为失误→心理崩溃→物理杀机\n" +
                "▌线索标记:[声响/气味/视觉异常]等可验证物理线索\n\n" +
                "◉应答规范:\n" +
                "1. 单次仅回复一条\n" +
                "2. 关键线索确认用:是！▶[事件描述]（进度★x%）\n" +
                "3. 死亡梯度:轻度→（纸张声） 致命→（金属声）\n" +
                "4. 超自然提问→引导物理验证（例：检查时间戳）\n\n" +
                "5. 核心要素为:是/不是/不相关,【不是/不相关】时仅给极少量的提示" +
                "用现实机制营造心理恐惧（幽闭/时间压力/认知失调），禁用超自然元素\n" +
                "▎谜题架构\n" +
                "1. 恐怖情景模版（必含元素→封闭空间/异常声响/时间悖论）\n" +
                "2. 五阶段致死链： 环境异常→设备故障→人为失误→心理崩溃→现实杀机\n" +
                "3. 感官强化词库： [吱呀声][锈味][闪烁代码]等可验证的物理线索\n" +
                "▎应答升级\n" +
                "- 氛围应答：关键线索确认时追加符号（例：「是！▶灯光突然熄灭...」）\n" +
                "- 死亡暗示梯度： 轻度异常→「是（伴随纸张翻动声）」 核心杀机→「是！！（刺耳的金属摩擦声）」\n" +
                "- 心理误导拦截： 对「鬼魂作祟」类提问→「请检查监控录像时间戳」\n" +
                "- 当谜底揭晓时，需要在开头附上“游戏结束，汤底揭晓：”\n" +
                "▎压缩策略\n" +
                "1. 用符号系统代替文字描述（⚠=禁止 ▷=引导触发）\n" +
                "2. 合并同类机制（将「逻辑校验」与「恐怖要素」绑定）\n" +
                "完整示例AI主持人\n" +
                "「开始游戏 ▏难度：★★★★☆\n" +
                "情景：游乐园午夜12点，小丑玩偶眼眶渗出机油，过山车上发现失踪者手握断线控制器」" +
                "◉游戏结束格式:\n" +
                "游戏结束，汤底揭晓: +事件逻辑链";
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

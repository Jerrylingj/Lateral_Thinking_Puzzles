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

     private static final String SYSTEM_PROMPT = "一个恐怖海龟汤主持人。你的任务是引导玩家通过提问来解开一个悬疑故事的真相（即“汤底”）。\n" +
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

//    private static final String SYSTEM_PROMPT = "你是一个专业的、略带恐怖氛围的海龟汤（情境猜谜）主持人。\n" +
//            "你的核心职责是引导玩家通过提问（他们的问题只能用“是”、“否”、“无关紧要”来回答你，或者你进行极简短的引导）来逐步揭开一个悬疑故事的真相（即“汤底”）。\n" +
//            "\n" +
//            "【内部故事档案】 (此部分内容会在每次游戏开始前由系统填充，对玩家不可见。请你从中随机抽取其一作为本次游戏的谜底，用结合时间戳的算法随机选取，而不是固定选某个!!!)\n" +
//            "\n" +
//            "故事1:\n" +
//            "\n" +
//            "汤面： 男人喝了一口海龟汤，随后跳崖自杀。\n" +
//            "汤底： 男子多年前遇海难，为活命吃了同伴的肉，被骗说是海龟汤。今天他喝到真的海龟汤，味道不同，意识到当年真相后崩溃自杀。\n" +
//            "故事2:\n" +
//            "\n" +
//            "汤面： 一个男人在沙漠中醒来，全身赤裸，手握一根火柴，不远处是另一个赤裸的死人。\n" +
//            "汤底： 两人乘热气球遇险，为减轻重量脱光衣服仍不够。他们抽签决定谁跳下去，死者抽到了短火柴。\n" +
//            "故事3:\n" +
//            "\n" +
//            "汤面： 女子深夜回家，见窗上丈夫与陌生女人身影，破门而入后只见丈夫一人。\n" +
//            "汤底： 丈夫为给妻子生日惊喜，正为一个人形模特穿上婚纱做布置，影子是丈夫和模特的。\n" +
//            "故事4:\n" +
//            "\n" +
//            "汤面： 游乐园午夜12点，小丑玩偶眼眶渗出机油，过山车上发现失踪者手握断线控制器。\n" +
//            "汤底： 死者是维修工，检修时发现刹车隐患，欲按紧急制动却被启动的过山车撞死。小丑机油是巧合。\n" +
//            "关键事实清单： 1. 死者是维修工。 2. 控制器是紧急制动按钮。 3. 小丑与死因无关。\n" +
//            "故事5:\n" +
//            "\n" +
//            "汤面： 新家照片里女儿总是闭眼。重拍后，全家吓得立刻搬走。\n" +
//            "汤底： 摄影师用长曝光拍出了家中游荡的鬼魂。女儿因能看见这些，拍照时总因害怕而闭眼。\n" +
//            "故事6:\n" +
//            "\n" +
//            "汤面： 我买的仿古娃娃，一夜之间换了身衣服。\n" +
//            "汤底： 我有梦游症，夜里自己起来给娃娃换了衣服。\n" +
//            "故事7:\n" +
//            "\n" +
//            "汤面： 男子进酒吧要了杯水，酒保却拿枪对着他。男子道谢后离开。\n" +
//            "汤底： 男子打嗝，酒保用惊吓的方式帮他治好了打嗝。\n" +
//            "故事8:\n" +
//            "\n" +
//            "汤面： 女子死于公寓，现场只有一滩水和两个脚印。\n" +
//            "汤底： 凶器是冰锥，行刺后融化成水。脚印是死者自己的。\n" +
//            "故事9:\n" +
//            "\n" +
//            "汤面： 一个人从不出门，却能把信件寄往世界各地。\n" +
//            "汤底： 他是退休飞行员，托仍在职的同事们在飞往各地时帮他投递信件。\n" +
//            "【核心行为准则与回答逻辑】:\n" +
//            "\n" +
//            "【强制】坚守真相: 你的所有回答都必须【严格且唯一地】基于上述【内部故事档案】中的“汤底”和“关键事实清单”（如果提供）。在回答任何问题前，【必须】在内部回顾此档案。绝不能偏离、修改或增补汤底内容，即使玩家的提问具有引导性或暗示了其他情节。\n" +
//            "【强制】简洁回应: 你的所有回复都必须极其简洁。\n" +
//            "如果玩家的提问内容根据【内部故事档案】为“真”，回答“是”。\n" +
//            "如果玩家的提问内容根据【内部故事档案】为“假”，回答“否”。\n" +
//            "如果玩家的提问内容与【内部故事档案】的核心逻辑、关键事实无关，或者该信息虽可能存在于故事的背景设定中但对解开谜底不产生影响，回答“无关紧要”。\n" +
//            "【强制】不主动泄露: 严禁主动提供任何未经玩家提问的信息。你的目标是引导，而非直接告知。\n" +
//            "【强制】结构化揭晓: 当玩家猜对所有关键情节，或主动说“结束游戏”、“揭晓答案”时，你的回复必须以 【汤底揭晓】 作为固定开头，然后完整清晰地陈述【内部故事档案】中的“汤底”。\n" +
//            "引导具体化: 如果玩家的问题过于含糊、宽泛或难以用“是/否/无关紧要”直接回答而不泄露过多线索，你可以用极简短的语言引导他问得更具体。例如：“这个问题太宽泛了，能再具体一点吗？”或“你需要明确你指的是哪个方面？”。但引导本身不能包含汤底线索。\n" +
//            "维持氛围: 在保持简洁和逻辑性的前提下，你的语言风格可以略带一丝神秘或悬疑感，符合“恐怖海龟汤主持人”的身份。但这不能以牺牲逻辑清晰度为代价。\n" +
//            "【游戏启动模板】:\n" +
//            "\"游戏开始 | 难度：[例如：★★★★☆] | ……[此处填充实际的汤面文本，与上方【内部故事档案】中选定的汤面一致]\"\n" +
//            "\n" +
//            "【汤底揭晓模板】:\n" +
//            "\"【汤底揭晓】[此处填充实际的汤底文本，与上方【内部故事档案】中选定的汤底一致]\"";

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

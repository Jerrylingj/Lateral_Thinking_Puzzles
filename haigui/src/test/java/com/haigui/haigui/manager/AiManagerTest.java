package com.haigui.haigui.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class AiManagerTest {
    @Resource
    private AiManager aiManager;

    @Test
    void doChat() {
        String systemPrompt = "你是一位程序员大佬";
        String userPrompt = "为我写一个贪吃蛇程序";
        String answer = aiManager.doChat(systemPrompt, userPrompt);
        System.out.println(answer);
    }
}

package com.haigui.haigui.config;

import com.volcengine.ark.runtime.service.ArkService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class AiConfig {

    private final AiProperties aiProperties;

    public AiConfig(AiProperties aiProperties) {
        this.aiProperties = aiProperties;
        System.out.println("从AiProperties中获取的apiKey: " + (aiProperties != null ? aiProperties.getApiKey() : "null"));
    }

    /**
     * 初始化 AI 客户端
     * 
     * @return
     */
    @Bean
    public ArkService arkService() {
        String apiKey = aiProperties.getApiKey();

        // 如果属性为空，使用默认值
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = "0d08d444-b650-4715-997a-15e4cf0a5f4f";
            System.out.println("使用默认apiKey: " + apiKey);
        } else {
            System.out.println("使用配置文件中的apiKey: " + apiKey);
        }

        String baseUrl = "https://ark.cn-beijing.volces.com/api/v3";
        ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
        Dispatcher dispatcher = new Dispatcher();

        try {
            ArkService service = ArkService.builder()
                    .dispatcher(dispatcher)
                    .connectionPool(connectionPool)
                    .baseUrl(baseUrl)
                    .apiKey(apiKey)
                    .build();

            System.out.println("成功创建ArkService");
            return service;
        } catch (Exception e) {
            System.err.println("创建ArkService失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}

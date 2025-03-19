package com.haigui.haigui;

import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionRequest;
import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionResult;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class AiTest {
    static String apiKey = System.getenv("ARK_API_KEY");
    static ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
    static Dispatcher dispatcher = new Dispatcher();
    static ArkService service = ArkService.builder().dispatcher(dispatcher).connectionPool(connectionPool).baseUrl("https://ark.cn-beijing.volces.com/api/v3/").apiKey(apiKey).build();

    public void doTest() {
        System.out.println("\n----- standard request -----");
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build();
        final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content("常见的十字花科植物有哪些？").build();
        messages.add(systemMessage);
        messages.add(userMessage);

        BotChatCompletionRequest chatCompletionRequest = BotChatCompletionRequest.builder()
                .botId("bot-20250319131909-svjsk")
                .messages(messages)
                .build();

        BotChatCompletionResult chatCompletionResult =  service.createBotChatCompletion(chatCompletionRequest);
        chatCompletionResult.getChoices().forEach(choice -> System.out.println(choice.getMessage().getContent()));
        // the references example
        if (chatCompletionResult.getReferences() != null) {
            chatCompletionResult.getReferences().forEach(ref -> System.out.println(ref.getUrl()));
        }

        System.out.println("\n----- streaming request -----");
        final List<ChatMessage> streamMessages = new ArrayList<>();
        final ChatMessage streamSystemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build();
        final ChatMessage streamUserMessage = ChatMessage.builder().role(ChatMessageRole.USER).content("常见的十字花科植物有哪些？").build();
        streamMessages.add(streamSystemMessage);
        streamMessages.add(streamUserMessage);

        BotChatCompletionRequest streamChatCompletionRequest = BotChatCompletionRequest.builder()
                .botId("bot-20250319131909-svjsk")
                .messages(streamMessages)
                .build();

        service.streamBotChatCompletion(streamChatCompletionRequest)
                .doOnError(Throwable::printStackTrace)
                .blockingForEach(
                        choice -> {
                            if (choice.getReferences() != null && !choice.getReferences().isEmpty()) {
                                choice.getReferences().forEach(ref -> System.out.println(ref.getUrl()));
                            }
                            if (!choice.getChoices().isEmpty()) {
                                System.out.print(choice.getChoices().get(0).getMessage().getContent());
                            }
                        }
                );

        // shutdown service after all requests is finished
        service.shutdownExecutor();
    }
}

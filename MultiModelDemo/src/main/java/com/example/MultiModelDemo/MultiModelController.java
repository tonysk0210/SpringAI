package com.example.MultiModelDemo;

import com.example.MultiModelDemo.config.ChatClientConfig;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MultiModelController {

    // 宣告兩個 ChatClient 欄位：
    // 一個連線到 OpenAI 模型（雲端模型）
    // 一個連線到 Ollama 模型（本地模型）
    private final ChatClient openAiChatClient;
    private final ChatClient ollamaChatClient;

    //因為有兩個相同型別 (ChatClient) 的 Bean，所以我們必須用 @Qualifier 來明確指定要注入哪一個。
    //Spring 預設會使用 方法名稱 作為 Bean 名稱
    @Autowired
    public MultiModelController(
            @Qualifier("openAiChatClient") ChatClient openAiChatClient,
            @Qualifier("ollamaChatClient") ChatClient ollamaChatClient
    ) {
        this.openAiChatClient = openAiChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    @GetMapping("/openai/chat")
    public String openaiChat(@RequestParam(value = "message") String message) {
        return openAiChatClient.prompt(message).call().content();
    }

    @RequestMapping("/ollama/chat")
    public String ollamaChat(@RequestParam(value = "message") String message) {
        return ollamaChatClient.prompt(message).call().content();
    }
}

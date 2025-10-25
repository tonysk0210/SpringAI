package com.example.MultiModelDemo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.create(openAiChatModel); // 方法 1：使用 ChatClient.create() 靜態工廠方法
    }

    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel).build(); // 方法 2：使用 ChatClient.builder() 建造者模式
    }
}

/*
## 🔄 @Configuration 的生命週期
```
應用啟動
    ↓
Spring 掃描所有 @Configuration 類別
    ↓
執行類別中所有 @Bean 方法
    ↓
建立並管理這些 Bean
    ↓
其他地方可以透過 @Autowired 注入使用
    ↓
應用關閉時，Spring 負責清理
*/

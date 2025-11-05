package com.example.RagWithPDF.chatClientConfig;

import com.example.RagWithPDF.advisor.TokenUsageAuditAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryChatClientConfig {

    // arguments are implicitly autowired by Spring Boot.
    // ChatMemory implemented by MessageWindowChatMemory. In which it has ChatMemoryRepository implemented by InMemoryChatMemoryRepository.
    @Bean("chatMemoryChatClient")
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build(); // Advisor for chat memory
        Advisor loggerAdvisor = new SimpleLoggerAdvisor();                            // Advisor for logging
        Advisor tokenUsage = new TokenUsageAuditAdvisor();
        return chatClientBuilder
                .defaultAdvisors(memoryAdvisor, loggerAdvisor, tokenUsage) // set Advisors for chat memory and logging
                .build();
    }
    /**
     * MessageChatMemoryAdvisor 會：
     *
     * 在呼叫前：
     * 自動從 ChatMemory（例如 MessageWindowChatMemory）中取出過去對話，
     * 加入這次 prompt 的上下文。
     *
     * 在呼叫後：
     * 把這次的使用者訊息與模型回覆，一起存回 ChatMemory。
     */
}

/**
 * User → ChatClient → MessageChatMemoryAdvisor
 * │
 * ▼
 * MessageWindowChatMemory
 * │
 * ▼
 * (ChatMemoryRepository → RAM / Redis / DB)
 */
package com.example.ToolCallingWithDB.chatClientConfig;

import com.example.ToolCallingWithDB.advisor.TokenUsageAuditAdvisor;
import com.example.ToolCallingWithDB.tools.TimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeChatClientConfig {

    // ChatMemory implemented by MessageWindowChatMemory. In which it has ChatMemoryRepository implemented by InMemoryChatMemoryRepository.
    @Bean("timeChatClient")
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory, TimeTools timeTools) {
        Advisor loggerAdvisor = new SimpleLoggerAdvisor(); // Advisor for logging
        Advisor tokenUsage = new TokenUsageAuditAdvisor();
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build(); // MessageChatMemoryAdvisor 會在每次 prompt().call() 執行時，自動儲存該次對話內容到 chatMemory（不論是 RAM 還是 DB）。
        return chatClientBuilder
                .defaultTools(timeTools) // set TimeTools as the tool for the ChatClient
                .defaultAdvisors(loggerAdvisor, memoryAdvisor, tokenUsage) // set Advisors for chat memory and logging
                .build();
    }
    /**
     * 只要你設定了：
     * spring.datasource.url=jdbc:h2:file:~/chat
     * spring.datasource.driver-class-name=org.h2.Driver
     * spring.ai.chat.memory.jdbc.initialize-schema=true
     * <p>
     * Spring AI 會自動：
     * 啟用 JDBC ChatMemory
     * 建立 chat_memory 表
     * 持久化所有對話記錄
     * 👉 你不需要額外寫 Bean 或組態，Spring Boot 會自動完成。
     */
}

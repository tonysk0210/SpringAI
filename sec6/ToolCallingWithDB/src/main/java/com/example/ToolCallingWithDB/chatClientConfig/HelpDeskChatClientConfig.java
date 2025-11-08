package com.example.ToolCallingWithDB.chatClientConfig;

import com.example.ToolCallingWithDB.advisor.TokenUsageAuditAdvisor;
import com.example.ToolCallingWithDB.tools.TimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class HelpDeskChatClientConfig {

    @Value("classpath:/promptTemplate/helpDeskSystemPromptTemplate.st")
    Resource helpDeskSystemTemplate;

    /*@Bean
    ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository).build();
    }*/

    // ChatMemory implemented by MessageWindowChatMemory. In which it has ChatMemoryRepository implemented by InMemoryChatMemoryRepository.
    @Bean("helpDeskChatClient")
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory, TimeTools timeTools) {
        Advisor loggerAdvisor = new SimpleLoggerAdvisor(); // Advisor for logging
        Advisor tokenUsage = new TokenUsageAuditAdvisor();
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build(); // MessageChatMemoryAdvisor 會在每次 prompt().call() 執行時，自動儲存該次對話內容到 chatMemory（不論是 RAM 還是 DB）。
        return chatClientBuilder
                .defaultSystem(helpDeskSystemTemplate)
                .defaultTools(timeTools) // set TimeTools as the tool for the ChatClient
                .defaultAdvisors(loggerAdvisor, memoryAdvisor, tokenUsage) // set Advisors for chat memory and logging
                .build();
    }
}

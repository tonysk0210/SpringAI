package com.example.ToolCallingWithDBReturnDirect.chatClientConfig;

import com.example.ToolCallingWithDBReturnDirect.advisor.TokenUsageAuditAdvisor;
import com.example.ToolCallingWithDBReturnDirect.tools.TimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.ai.tool.execution.ToolExecutionException;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
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

    // ToolExecutionExceptionProcessor 不是自動註冊的 Bean，它只是被某些組件（例如 ToolCallingService）在內部使用時 自行 new 出來
    // 並不是「覆蓋原有的 Bean」→ 而是「你第一次在 context 中提供了這個 Bean」。
    @Bean
    ToolExecutionExceptionProcessor toolExecutionExceptionProcessor() {
        return new DefaultToolExecutionExceptionProcessor(true);
    }
}

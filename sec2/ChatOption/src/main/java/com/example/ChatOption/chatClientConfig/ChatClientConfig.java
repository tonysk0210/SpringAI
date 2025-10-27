package com.example.ChatOption.chatClientConfig;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// 提供預先設定好的 ChatClient Bean，方便控制器直接注入使用。
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        ChatOptions chatOptions = ChatOptions.builder().model("gpt-4.1-mini").maxTokens(50).temperature(0.8).build(); //create ChatOptions
        //.maxTokens() only controls the completion tokens, not the prompt tokens.

        // 透過 Builder 設定 ChatClient 的預設系統指令，確保每次交談都套用相同背景角色。
        return chatClientBuilder
                .defaultOptions(chatOptions) //set default chat options
                .defaultAdvisors(new SimpleLoggerAdvisor()) // 加入簡單的日誌記錄顧問，方便追蹤對話歷程。
                // 設定系統提示，限制助理僅回答與人資政策HR相關的問題。
                .defaultSystem("""
                        You are an internal HR assistant. Your role is to help\s
                        employees with questions related to HR policies, such as\s
                        leave policies, working hours, benefits, and code of conduct.
                        If a user asks for help with anything outside of these topics,\s
                        kindly inform them that you can only assist with queries related to\s
                        HR policies.
                        """).build();
    }
}

/**
 * User: "I used 5 leaves this year..."
 * │
 * ▼
 * [SimpleLoggerAdvisor.before()]
 * → log "request" (System + User prompt)
 * │
 * ▼
 * LLM (gpt-4o-mini)
 * │
 * ▼
 * [SimpleLoggerAdvisor.after()]
 * → log "response" (AI 回答 + metadata)
 * │
 * ▼
 * Controller 回傳 AI 回覆給使用者
 */
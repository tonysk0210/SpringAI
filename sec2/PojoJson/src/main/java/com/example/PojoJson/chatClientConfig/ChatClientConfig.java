package com.example.PojoJson.chatClientConfig;

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
        ChatOptions chatOptions = ChatOptions.builder().model("gpt-4.1-mini").temperature(0.8).build();

        return chatClientBuilder // ← 原始的 Builder（乾淨的）
                .defaultOptions(chatOptions) // ← 返回新 Builder A
                .defaultAdvisors(new SimpleLoggerAdvisor()) // ← 返回新 Builder B
                .defaultSystem("""
                        You are an internal HR assistant. Your role is to help\s
                        employees with questions related to HR policies, such as\s
                        leave policies, working hours, benefits, and code of conduct.
                        If a user asks for help with anything outside of these topics,\s
                        kindly inform them that you can only assist with queries related to\s
                        HR policies.
                        """)    // ← 返回新 Builder C
                .build(); // ← 用 Builder C 來 build
        // 關鍵：原本注入的 chatClientBuilder 參數「沒有被修改」！
    }
}
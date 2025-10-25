package com.example.PromptTemplateDemo.chatClientConfig;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// 提供預先設定好的 ChatClient Bean，方便控制器直接注入使用。
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        // 透過 Builder 設定 ChatClient 的預設系統指令，確保每次交談都套用相同背景角色。
        return chatClientBuilder
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

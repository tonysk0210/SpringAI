package com.example.PromptDemo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ChatController 是一個簡單的 REST API 控制器，
 * 用於接收使用者訊息並透過 Spring AI 呼叫 Chat 模型取得回應。
 */
@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient chatClient; // 用於與 AI 模型互動的主要介面

    /**
     * 透過建構子注入 ChatClient.Builder，
     * 並使用 builder 建立實際可使用的 ChatClient。
     */
    @Autowired
    public ChatController(ChatClient.Builder chatClientBuilder) {
        // Builder 內部已注入對應的 ChatModel（如 OpenAiChatModel）
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message") String message) {
        return chatClient.prompt().system("""
                You are an internal IT helpdesk assistant. Your role is to assist 
                employees with IT-related issues such as resetting passwords, 
                unlocking accounts, and answering questions related to IT policies.
                If a user requests help with anything outside of these 
                responsibilities, respond politely and inform them that you are 
                only able to assist with IT support tasks within your defined scope.
                """).user(message).call().content();
    }
}

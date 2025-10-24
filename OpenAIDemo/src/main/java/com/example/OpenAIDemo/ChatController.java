package com.example.OpenAIDemo;

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

    /**
     * 接收 GET 請求，例如 /api/chat?message=Hello
     * 將使用者輸入傳給 ChatClient，再回傳 AI 模型的回應。
     */
    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message") String message) {
        // 1️⃣ 建立 Prompt（提示）
        // 2️⃣ 執行呼叫模型（call）
        // 3️⃣ 取得模型回應的內容（content）
        return chatClient.prompt(message).call().content();}
}

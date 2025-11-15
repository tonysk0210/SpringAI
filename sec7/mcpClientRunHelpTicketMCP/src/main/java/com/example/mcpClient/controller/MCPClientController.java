package com.example.mcpClient.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MCPClientController {

    private final ChatClient chatClient;

    @Autowired
    public MCPClientController(ChatClient.Builder chatClientBuilder, ToolCallbackProvider toolCallbackProvider) { // ToolCallbackProvider (bean) 負責告訴 Spring AI，哪些工具可被呼叫，以及怎麼呼叫它們
        // SimpleLoggerAdvisor 幫助你在模型請求與回應過程中，自動打印出請求、回覆、以及工具呼叫（tool call）的詳細資訊。
        /**
         * 其中 SimpleLoggerAdvisor 會：
         * 1. 在送出前打印 prompt（請求內容）
         * 2. 在接收後打印 response（模型回覆）
         * 3. 若使用 tool calling，會打印出呼叫的工具名稱與參數
         */
        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(toolCallbackProvider) // 定義了「模型能呼叫哪些工具」
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam("message") String message, @RequestHeader("username") String username) {
        return chatClient.prompt().user(message + " My username is " + username).call().content();
    }
}

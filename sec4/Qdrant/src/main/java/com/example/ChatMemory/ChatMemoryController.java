package com.example.ChatMemory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatMemoryController {

    private final ChatClient chatClient;

    @Autowired
    public ChatMemoryController(@Qualifier("chatMemoryChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chatMemory")
    public ResponseEntity<String> chatMemory(@RequestParam(value = "message") String message) {
        return ResponseEntity.ok(
                chatClient.prompt()
                        .user(message)
                        .call()
                        .content()
        );
    }

    @GetMapping("/chatMemoryUserName")
    public ResponseEntity<String> chatMemoryUserName(@RequestParam(value = "message") String message,
                                                     @RequestHeader("user-name") String userName) { // 從 HTTP Header 取得 user-name（用戶名稱），作為對話 ID 使用
        return ResponseEntity.ok(
                chatClient.prompt()             // 1️⃣ 開始建立一個新的 Prompt（提示語）請求
                        .user(message)           // 2️⃣ 將使用者輸入的文字設定為「user」角色的訊息
                        // Sets the conversation ID to the userName
                        // This ensures each user has their own isolated conversation history
                        // All messages from the same userName will share the same memory context
                        .advisors(advisorSpec -> // 3️⃣ 進一步設定「advisors（顧問/攔截器）」的參數
                                advisorSpec.param(
                                        ChatMemory.CONVERSATION_ID, // 4️⃣ 設定 advisor 的參數鍵為 ChatMemory.CONVERSATION_ID
                                        userName                       // 5️⃣ 將 HTTP Header 的 userName 當作該對話的唯一 ID
                                )
                        )
                        .call()                                        // 6️⃣ 呼叫 LLM（例如 OpenAI 或 Ollama）取得回應
                        .content()
        );
    }


}

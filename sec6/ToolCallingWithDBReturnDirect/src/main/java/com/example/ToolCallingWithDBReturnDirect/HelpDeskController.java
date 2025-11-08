package com.example.ToolCallingWithDBReturnDirect;

import com.example.ToolCallingWithDBReturnDirect.tools.HelpDeskTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tools")
public class HelpDeskController {

    private final ChatClient chatClient;
    private final HelpDeskTools helpDeskTools;

    @Autowired
    public HelpDeskController(@Qualifier("helpDeskChatClient") ChatClient chatClient, HelpDeskTools helpDeskTools) {
        this.chatClient = chatClient;
        this.helpDeskTools = helpDeskTools;
    }

    @GetMapping("/helpdesk")
    public ResponseEntity<String> helpDesk(@RequestHeader("username") String username, @RequestParam("message") String message) {
        String answer = chatClient.prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, username))
                .user(message)
                .tools(helpDeskTools)
                .toolContext(Map.of("username", username)) // 「當 Spring AI 將這個 prompt 傳給 LLM 時，若 LLM 要呼叫某個 @Tool 方法， 請把這個背景資料（username）傳給後端方法。」
                .call().content();
        return ResponseEntity.ok(answer);
    }
}

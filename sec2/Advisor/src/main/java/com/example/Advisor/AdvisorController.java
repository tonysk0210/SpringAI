package com.example.Advisor;

import com.example.Advisor.advisors.TokenUsageAuditAdvisor;
import org.springframework.core.io.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class AdvisorController {

    private final ChatClient chatClient;
    // 注入經過預設設定的 ChatClient，用於呼叫大語言模型。

    @Value("classpath:/promptTemplate/systemPromptTemplate.st")
    private Resource systemPromptTemplate;

    @Autowired
    public AdvisorController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/prompt-stuffing")
    public String promptStuffing(@RequestParam(value = "message") String message) {
        return chatClient.prompt()
                .advisors(new TokenUsageAuditAdvisor()) //override default advisors, add TokenUsageAuditAdvisor
                .system(systemPromptTemplate) //override default system prompt, use template from resource for prompt stuffing
                .user(message).call().content();
    }
}

    /*
    .system() = 設定 AI 的人格與行為準則
    .user() = 傳達使用者的問題或任務內容
    */

/**
 * (SimpleLoggerAdvisor) before()   ← order = 0
 * ↓
 * (TokenUsageAuditAdvisor) before()  ← order = 1
 * ↓
 * [LLM 模型呼叫]
 * ↑
 * (TokenUsageAuditAdvisor) after()
 * ↑
 * (SimpleLoggerAdvisor) after()
 */
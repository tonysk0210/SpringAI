package com.example.PromptTemplateDemo;

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
public class PromptTemplateController {

    private final ChatClient chatClient;
    // 注入經過預設設定的 ChatClient，用於呼叫大語言模型。

    @Autowired
    public PromptTemplateController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Value("classpath:/promptTemplate/userTemplate.st")
    // 從 classpath（資源目錄） 中載入一個名為 userTemplate.st 的檔案，並注入成一個 Resource 物件。
    // classpath: 前綴代表「從 src/main/resources 或 jar 內部資源」載入。
    private Resource userTemplate;

    @GetMapping("/email")
    public String emailResponse(
            @RequestParam(value = "customerName") String customerName,
            @RequestParam(value = "customerMessage") String customerMessage) {

        // 建立一個包含系統角色與使用者樣板的提示，並要求模型組出 Email 草稿。
        return chatClient.prompt().system("""
                        You are a professional customer service assistant which helps drafting email
                        responses to improve the productivity of the customer support team
                        """)
                .user(spec -> spec.text(userTemplate)
                        // 依序替換樣板中的變數，注入使用者輸入內容。
                        .param("customerName", customerName)
                        .param("customerMessage", customerMessage)).call().content();
    }

    /*
    .system() = 設定 AI 的人格與行為準則
    .user() = 傳達使用者的問題或任務內容
    */
}

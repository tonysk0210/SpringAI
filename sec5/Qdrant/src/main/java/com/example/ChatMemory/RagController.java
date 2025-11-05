package com.example.ChatMemory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    // 請 Spring 將 classpath 中的這個檔案載入為一個 Resource 物件，並注入到這個變數中。
    @Value("classpath:/promptTemplate/systemPromptRandomDataTemplate.st")
    Resource systemTemplate; //org.springframework.core.io.Resource

    //@Qualifier("chatMemoryChatClient") is to specify which bean to inject when there are multiple beans of the same type
    @Autowired
    public RagController(@Qualifier("chatMemoryChatClient") ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/random/chat")
    public ResponseEntity<String> randomChat(@RequestHeader(value = "username") String userName,
                                             @RequestParam(value = "message") String message) {
        // SearchRequest 是用來 查詢向量資料庫（Vector DB，例如 Qdrant） 的請求物件。
        // 只有當文件與查詢向量的相似度 ≥ 0.5 時，該文件才會被視為「相關」並返回。
        SearchRequest searchRequest = SearchRequest.builder().query(message).topK(3).similarityThreshold(0.5).build();
        /**
         * 範圍在 -1 ~ 1
         * 越接近 1 表示越相似（方向幾乎一樣）
         * 越接近 0 表示無關
         * 越接近 -1 表示完全相反（語意對立）
         */
        List<Document> similarDocs = vectorStore.similaritySearch(searchRequest);
        /**
         * Document 內部大致長這樣（簡化版）：
         * public class Document {
         *     private String id;                 // 唯一識別
         *     private String text;               // 原始文件內容
         *     private Map<String, Object> metadata; // 其他附帶資訊（標籤、類別等）
         * }
         */
        String similarContext = similarDocs.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));
        String answer = chatClient.prompt()
                .system(systemSpec -> systemSpec.text(systemTemplate)
                        .param("documents", similarContext))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userName))
                .user(message)
                .call().content();

        return ResponseEntity.ok(answer);
    }

    /**
     * 使用者 (Client)
     *    │
     *    │  ① 發送 HTTP GET 請求
     *    │     /api/rag/random/chat?message=...
     *    │     Header: username
     *    ▼
     * Spring Boot Controller (RagController)
     *    │
     *    ├─ ② 建立 SearchRequest (查詢請求)
     *    │     query = 使用者訊息
     *    │     topK = 3
     *    │     similarityThreshold = 0.5
     *    │
     *    ├─ ③ 向 Qdrant 向量資料庫查詢
     *    │     vectorStore.similaritySearch(searchRequest)
     *    │
     *    ├─ ④ Qdrant 回傳最相似的文件列表 List<Document>
     *    │
     *    ├─ ⑤ 將每個 Document.getText() 內容串接成一段文字 similarContext
     *    │
     *    ├─ ⑥ （預留）將 similarContext + systemTemplate + 使用者訊息
     *    │       一起交給 ChatClient 生成回答
     *    │
     *    └─ ⑦ 回傳 HTTP 回應給前端
     */
}

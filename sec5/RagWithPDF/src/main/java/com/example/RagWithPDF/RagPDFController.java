package com.example.RagWithPDF;

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
public class RagPDFController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    // 請 Spring 將 classpath 中的這個檔案載入為一個 Resource 物件，並注入到這個變數中。
    @Value("classpath:/promptTemplate/systemPromptTemplate.st")
    Resource hrSystemTemplate; //org.springframework.core.io.Resource

    //@Qualifier("chatMemoryChatClient") is to specify which bean to inject when there are multiple beans of the same type
    @Autowired
    public RagPDFController(@Qualifier("chatMemoryChatClient") ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/document/chat")
    public ResponseEntity<String> documentChat(@RequestHeader(value = "username") String userName,
                                               @RequestParam(value = "message") String message) {
        // SearchRequest 是用來 查詢向量資料庫（Vector DB，例如 Qdrant） 的請求物件。
        // 只有當文件與查詢向量的相似度 ≥ 0.5 時，該文件才會被視為「相關」並返回。`
        SearchRequest searchRequest = SearchRequest.builder().query(message).topK(3).similarityThreshold(0.5).build();

        /**
         * Document 內部大致長這樣（簡化版）：
         * public class Document {
         *     private String id;                 // 唯一識別
         *     private String text;               // 原始文件內容
         *     private Map<String, Object> metadata; // 其他附帶資訊（標籤、類別等）
         * }
         */
        List<Document> similarDocs = vectorStore.similaritySearch(searchRequest);

        String similarContext = similarDocs.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));
        String answer = chatClient.prompt()
                .system(systemSpec -> systemSpec.text(hrSystemTemplate)
                        .param("documents", similarContext))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userName))
                .user(message)
                .call().content();

        return ResponseEntity.ok(answer);
    }
}

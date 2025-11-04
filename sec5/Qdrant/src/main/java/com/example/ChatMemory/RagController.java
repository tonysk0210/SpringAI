package com.example.ChatMemory;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath:/promptTemplates/system.st")
    Resource promptTemplate;

    //@Qualifier("chatMemoryChatClient") is to specify which bean to inject when there are multiple beans of the same type
    @Autowired
    public RagController(@Qualifier("chatMemoryChatClient") ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/rag")
    public ResponseEntity<String> randomChat(@RequestHeader(value = "username") String userName,
                                             @RequestParam(value = "message") String message) {
        return ResponseEntity.ok("Random chat response");
    }
}

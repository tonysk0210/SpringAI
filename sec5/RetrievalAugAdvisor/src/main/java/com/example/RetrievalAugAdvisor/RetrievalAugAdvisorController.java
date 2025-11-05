package com.example.RetrievalAugAdvisor;

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
public class RetrievalAugAdvisorController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    // 請 Spring 將 classpath 中的這個檔案載入為一個 Resource 物件，並注入到這個變數中。
    @Value("classpath:/promptTemplate/systemPromptTemplate.st")
    Resource hrSystemTemplate; //org.springframework.core.io.Resource

    //@Qualifier("chatMemoryChatClient") is to specify which bean to inject when there are multiple beans of the same type
    @Autowired
    public RetrievalAugAdvisorController(@Qualifier("chatMemoryChatClient") ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/document/chat")
    public ResponseEntity<String> documentChat(@RequestHeader(value = "username") String userName,
                                               @RequestParam(value = "message") String message) {
        /**
         * 手動搜尋
         * 用 vectorStore.similaritySearch() 根據 message 查出相似的文件。
         *
         * 手動拼接上下文
         * 把查回來的文件內容串起來成 similarContext。
         *
         * 手動注入 Prompt 模板（systemPromptTemplate）
         * 用 .system() 把文件內容 {documents} 放進 system prompt。
         */

        /*SearchRequest searchRequest = SearchRequest.builder().query(message).topK(3).similarityThreshold(0.5).build();

        List<Document> similarDocs = vectorStore.similaritySearch(searchRequest);

        String similarContext = similarDocs.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));*/
        String answer = chatClient.prompt()
                /*.system(systemSpec -> systemSpec.text(hrSystemTemplate)
                        .param("documents", similarContext))*/
                .system(system -> system.text("""
                            You are a helpful assistant, answering questions about EazyBytes company policies.
                            Only use the context retrieved from the documents.
                            If no relevant document is found, reply "I don't know".
                        """))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userName))
                .user(message)
                .call().content();

        return ResponseEntity.ok(answer);


    }
}

package com.example.RetrievalAugAdvisor.chatClientConfig;

import com.example.RetrievalAugAdvisor.advisor.TokenUsageAuditAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryChatClientConfig {

    // ChatMemory implemented by MessageWindowChatMemory. In which it has ChatMemoryRepository implemented by InMemoryChatMemoryRepository.
    @Bean("chatMemoryChatClient")
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder,
                                 ChatMemory chatMemory,
                                 RetrievalAugmentationAdvisor retrievalAugmentationAdvisor) {
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build(); // Advisor for chat memory
        Advisor loggerAdvisor = new SimpleLoggerAdvisor();                            // Advisor for logging
        Advisor tokenUsage = new TokenUsageAuditAdvisor();
        return chatClientBuilder
                .defaultAdvisors(memoryAdvisor, loggerAdvisor, tokenUsage, retrievalAugmentationAdvisor) // set Advisors for chat memory and logging
                .build();
    }

    /**
     * 在生成回答前，先從向量資料庫 (VectorStore) 檢索出相關文件，
     * 並把這些文件的內容附加到模型的 prompt 裡。
     * <p>
     * 這個 Bean 建立了一個「RAG 顧問 (RetrievalAugmentationAdvisor)」，
     * 讓你的 ChatClient 在回答前會自動從向量資料庫查找最相關的 3 份文件（相似度 ≥ 0.5），
     * 再用這些內容輔助生成更精準的答案。
     * <p>
     * RetrievalAugmentationAdvisor 的重點是「加上外部文件的內容」，
     * 而不是負責模型角色的個性、語氣、或任務設定。
     */
    @Bean
    RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(VectorStore vectorStore) {
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever( // 「用哪種方式從資料庫取文件。」
                        VectorStoreDocumentRetriever.builder() // 設定「如何從向量資料庫查詢文件」的建構器
                                .vectorStore(vectorStore) // 注入的是你的向量資料庫實例
                                .topK(3)
                                .similarityThreshold(0.5).build()
                ).build();
    }

}

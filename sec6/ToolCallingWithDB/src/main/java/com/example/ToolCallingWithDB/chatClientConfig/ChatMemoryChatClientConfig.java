package com.example.ToolCallingWithDB.chatClientConfig;

import com.example.ToolCallingWithDB.advisor.TokenUsageAuditAdvisor;
import com.example.ToolCallingWithDB.postRetrieval.PIIMaskingDocumentPostProcessor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryChatClientConfig {

    /**
     * 只要你設定了：
     * spring.datasource.url=jdbc:h2:file:~/chat
     * spring.datasource.driver-class-name=org.h2.Driver
     * spring.ai.chat.memory.jdbc.initialize-schema=true
     * <p>
     * Spring AI 會自動：
     * 啟用 JDBC ChatMemory
     * 建立 chat_memory 表
     * 持久化所有對話記錄
     * 👉 你不需要額外寫 Bean 或組態，Spring Boot 會自動完成。
     */
    // ChatMemory implemented by MessageWindowChatMemory. In which it has ChatMemoryRepository implemented by InMemoryChatMemoryRepository.
    @Bean("chatMemoryChatClient")
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder,
                                 ChatMemory chatMemory,
                                 RetrievalAugmentationAdvisor retrievalAugmentationAdvisor) { // RetrievalAugmentationAdvisor 「在 ChatClient 執行對話請求時，自動插入檢索增強的邏輯。」
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build(); // MessageChatMemoryAdvisor 會在每次 prompt().call() 執行時，自動儲存該次對話內容到 chatMemory（不論是 RAM 還是 DB）。
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
    RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) { //is a bean that QdrantVectorStore implements it based on the declaration of pom.xml
        return RetrievalAugmentationAdvisor.builder()
                /**
                 * pre-retrieval
                 */
                .queryTransformers(TranslationQueryTransformer.builder() // 建立一個 TranslationQueryTransformer.Builder 實例
                        .chatClientBuilder(chatClientBuilder.clone()) // 指定用哪個 LLM 來執行「翻譯」這件事。.clone() -> 各自獨立 builder, 包含目前的設定值
                        .targetLanguage("Engish").build())
                .documentRetriever( // 「用哪種方式從資料庫取文件。」
                        VectorStoreDocumentRetriever.builder() // 設定「如何從向量資料庫查詢文件」的建構器
                                .vectorStore(vectorStore) // 注入的是你的向量資料庫實例
                                .topK(3)
                                .similarityThreshold(0.5).build())
                /**
                 * post-retrieval
                 * 目的就是在 Retriever 從向量資料庫撈完文件之後、餵給 LLM 之前，
                 * 先對文件內容進行 PII（Personally Identifiable Information，個資）遮罩。
                 */
                .documentPostProcessors(PIIMaskingDocumentPostProcessor.builder()) // 👇 這裡只是「註冊」你的處理器
                .build();

        /**
         * User Query
         *    ↓
         * Pre-retrieval: TranslationQueryTransformer (翻譯成英文)
         *    ↓
         * Retriever: VectorStoreDocumentRetriever (取出文件)
         *    ↓
         * Post-retrieval: PIIMaskingDocumentPostProcessor (遮罩 PII)
         *    ↓
         * Augmentation: 組 Prompt + Context
         *    ↓
         * ChatClient: LLM 生成回答
         */

        /**
         *User query
         *    ↓
         * OpenAiEmbeddingModel → 產生 query embedding
         *    ↓
         * VectorStore (例如 Qdrant) → 比對相似度、取出最相似文件
         *    ↓
         * RetrievalAugmentationAdvisor → 把這些文件餵給 LLM
         *    ↓
         * OpenAiChatModel (或其他 LLM) → 根據 context 生成回答
         */
    }

}

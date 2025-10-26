package com.example.Advisor.advisors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;

public class TokenUsageAuditAdvisor implements CallAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(TokenUsageAuditAdvisor.class);

    /**
     * 這個方法是 CallAdvisor 介面裡的核心方法。
     * 每個 Advisor 都可以攔截一次 ChatClient 與模型之間的呼叫，
     * 在呼叫前、呼叫後或出錯時，插入自訂邏輯。
     * adviseCall() 就是這個攔截點的實作。
     *
     * @param chatClientRequest
     * @param callAdvisorChain
     * @return
     */
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

        // ChatClient → CallAdvisorChain → LLM Model → 回傳 ChatClientResponse
        // 呼叫下一個 advisor（或最終的模型），取得回應。
        // 如果還有其他 advisor，就會往下傳遞；如果沒有，會直接呼叫 LLM 模型。
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest); // Call next advisor in chain
        /**
         * 在 Spring AI 的 CallAdvisor 介面中，沒有分開的 before() / after() 方法；而是用一個 adviseCall(...)，
         * 你在 nextCall(...) 之前寫的程式碼＝before，在 nextCall(...) 之後寫的程式碼＝after。
         */

        // 從 ChatClientResponse 中取出更高階的回覆物件 ChatResponse。
        // ChatClientResponse 是 Spring AI 封裝的結果物件，內含模型輸出的 ChatResponse。
        ChatResponse chatResponse = chatClientResponse.chatResponse();

        // 確認這次模型回覆中是否帶有 metadata（附加資訊）。
        // metadata 可能包含：model 名稱、使用 token 數量、rate limit、finish reason 等。
        if (chatResponse.getMetadata() != null) {

            // 從 metadata 中取出 usage 統計資訊。
            // Usage 會包含：
            //   - promptTokens: 傳給模型的 token 數量
            //   - completionTokens: 模型產生的 token 數量
            //   - totalTokens: 總 token 數
            Usage usage = chatResponse.getMetadata().getUsage();
            if (usage != null)
                logger.info("Token usage details : {}", usage.toString()); //Token usage details : DefaultUsage{promptTokens=197, completionTokens=63, totalTokens=260}
        }
        return chatClientResponse;
    }

    @Override
    public String getName() {
        return "TokenUsageAuditAdvisor";
    }

    @Override
    public int getOrder() {
        return 1;
    }
}

/***
 * ┌───────────────────────────────┐
 * │   ChatClient (使用者呼叫)       │
 * └───────────────┬───────────────┘
 *                 │
 *         [CallAdvisorChain]
 *                 │
 *      ┌──────────┴──────────┐
 *      ▼                     ▼
 * Advisor #1           （呼叫下一個）
 *     │   before()
 *     │
 *     ├─> callAdvisorChain.nextCall(request)
 *     │       ↓
 *     │     Advisor #2
 *     │         ├─> callAdvisorChain.nextCall(request)
 *     │         │       ↓
 *     │         │     LLM (真正呼叫)
 *     │         └─ after()
 *     │
 *     └─ after()
 */
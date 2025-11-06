package com.example.PreRetrieval.rag;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 讓你的應用程式能呼叫外部的 Tavily Web Search API（https://api.tavily.com/search）** 並將搜尋結果轉成 Document 物件，用於 AI RAG（檢索增強生成）流程。
 */
// to create customized DocumentRetriever for web search
public class WebSearchDocumentRetriever implements DocumentRetriever {

    private static final Logger logger = LoggerFactory.getLogger(WebSearchDocumentRetriever.class);

    private static final String TAVILY_API_KEY = "TAVILY_API_KEY";
    private static final String TAVILY_BASE_URL = "https://api.tavily.com/search";
    private static final int DEFAULT_RESULT_LIMIT = 5; // 只要這個變數是 static，那麼同一個外部類別內的 靜態內部類（static nested class）都能直接存取它（不需要建立外部類實例）。
    private final int resultLimit;
    private final RestClient restClient; //在你的 Spring Boot 專案中，如果你想讓後端去呼叫別的 API

    //RestClient.Builder is a bean
    public WebSearchDocumentRetriever(RestClient.Builder clientBuilder, int resultLimit) {
        Assert.notNull(clientBuilder, "clientBuilder cannot be null"); // 如果有人傳進來的 clientBuilder 是 null，就直接丟 IllegalArgumentException
        String apiKey = System.getenv(TAVILY_API_KEY); // 讀系統環境變數中的 API Key
        Assert.hasText(apiKey, "Environment variable " + TAVILY_API_KEY + " must be set"); // 若是 null 或空字串，就丟出錯誤
        this.restClient = clientBuilder
                .baseUrl(TAVILY_BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build(); // RestClient.Builder 是一個 Spring Bean 工廠，它能幫你建立設定好的 RestClient 實例
        if (resultLimit <= 0) {
            throw new IllegalArgumentException("resultLimit must be greater than 0");
        }
        this.resultLimit = resultLimit;
    }

    /**
     * 負責完成「🔍 查詢 → 向 Tavily API 發請求 → 解析結果 → 回傳成 AI 可用的 Document」流程
     * <p>
     * ┌────────────────────────────────────────┐
     * │ retrieve(Query query)                  │
     * │  ↓                                     │
     * │  1️⃣ 驗證 query.text()                  │
     * │  2️⃣ 建立 TavilyRequestPayload          │
     * │  3️⃣ POST 請求 Tavily API               │
     * │  4️⃣ 回傳 JSON → TavilyResponsePayload  │
     * │  5️⃣ 轉換成 List<Document>               │
     * │  6️⃣ 回傳給 Spring AI RAG                │
     * └────────────────────────────────────────┘
     */
    @Override
    public List<Document> retrieve(Query query) {
        logger.info("Processing query: {}", query.text());
        Assert.notNull(query, "query cannot be null");

        String q = query.text(); // 從 Spring AI 的 Query 物件中，取出使用者的查詢文字
        Assert.hasText(q, "query.text() cannot be empty");

        TavilyResponsePayload response = restClient.post() //建立一個 HTTP POST 請求
                .body(new TavilyRequestPayload(q, "advanced", resultLimit)) //設定 request body
                .retrieve() // 送出請求、取得回應
                .body(TavilyResponsePayload.class); // 解析回應 JSON

        // 檢查回傳結果是否有效
        if (response == null || CollectionUtils.isEmpty(response.results())) {
            return List.of();
        }

        /**
         * This is what the response looks like:
         * {
         *   "results": [
         *     {
         *       "title": "Spring AI integration with Tavily",
         *       "url": "https://example.com/spring-ai",
         *       "content": "Spring AI allows you to integrate LLMs with web search APIs...",
         *       "score": 0.97
         *     },
         *     {
         *       "title": "Using RestClient in Spring Boot",
         *       "url": "https://example.com/restclient",
         *       "content": "RestClient is the new HTTP client in Spring 6.1...",
         *       "score": 0.89
         *     }
         *   ]
         * }
         */
        // 轉換 Tavily 結果 → Spring AI Document
        List<Document> docs = new ArrayList<>(response.results().size());
        for (TavilyResponsePayload.Hit hit : response.results()) {
            // Map each Tavily hit into a Spring AI Document with metadata and score.
            Document doc = Document.builder()
                    .text(hit.content()) // 文件主要內容
                    .metadata("title", hit.title()) // 文件標題
                    .metadata("url", hit.url()) // 文件原始網址
                    .score(hit.score()) // Tavily 給的信心分數
                    .build();
            docs.add(doc);
        }
        return docs;
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // Java 類別的欄位名稱如何對應到 JSON 的屬性名稱
    record TavilyRequestPayload(String query, String searchDepth, int maxResults) {
    }

    /**
     * 把 Tavily API 回傳的 JSON 資料，自動轉換（反序列化）成 Java 物件。
     * <p>
     * 每一筆 Hit 代表 Tavily 找到的一個網頁
     */
    record TavilyResponsePayload(List<Hit> results) {
        record Hit(String title, String url, String content, Double score) {
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 靜態代表它不依賴外部實例（可以直接呼叫 WebSearchDocumentRetriever.Builder）
     */
    public static class Builder {
        private RestClient.Builder clientBuilder;
        private int resultLimit = DEFAULT_RESULT_LIMIT;

        // 限制只能透過外部類別呼叫靜態方法（例如 WebSearchDocumentRetriever.builder()）建立。
        private Builder() {
        }


        public Builder restClientBuilder(RestClient.Builder clientBuilder) { // ← RestClient.Builder 需手動傳進去
            this.clientBuilder = clientBuilder;
            return this;
        }

        public Builder maxResults(int maxResults) {
            if (maxResults <= 0) {
                throw new IllegalArgumentException("maxResults must be greater than 0");
            }
            this.resultLimit = maxResults;
            return this;
        }

        /**
         * 呼叫外部類的建構子，
         * 把使用者剛剛設定的值傳入，產生出完整、可用的 WebSearchDocumentRetriever 實例。
         */
        public WebSearchDocumentRetriever build() {
            return new WebSearchDocumentRetriever(clientBuilder, resultLimit); // 呼叫建構子 = 創造一個新物件（不需要已存在的物件）
        }
    }


}

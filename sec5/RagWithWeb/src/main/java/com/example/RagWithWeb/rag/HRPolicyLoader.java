package com.example.RagWithWeb.rag;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HRPolicyLoader {

    private final VectorStore vectorStore;

    @Value("classpath:Eazybytes_HR_Policies.pdf")
    Resource policyFile;

    @Autowired
    public HRPolicyLoader(VectorStore vectorStore) {
        // spring.ai.vectorstore.qdrant.* 在 properties 檔中，用來決定這裡會注入哪一個 VectorStore 的實作版本。
        this.vectorStore = vectorStore;
    }

    //這個特定 Bean 初始化完成後、正式可用之前 執行一些初始化邏輯
    @PostConstruct
    public void LoadPDF() {
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(policyFile); // 用 Apache Tika 打開這個檔案，準備從中抽取文字
        List<Document> docs = tikaDocumentReader.get(); // 把 PDF 文件解析成多個文字段落，每段包裝成 Spring AI 的 Document 物件

        TextSplitter tokenTextSplitter = TokenTextSplitter.builder().withChunkSize(100).withMaxNumChunks(400).build(); // 「每段 100 token、最多切 400 段」的 TokenTextSplitter，用來把長文字內容拆成多個較小的 Document，以便送入向量資料庫或 LLM 查詢

        vectorStore.add(tokenTextSplitter.split(docs)); // 把「已經分段的文件內容」送進 向量資料庫
    }
}

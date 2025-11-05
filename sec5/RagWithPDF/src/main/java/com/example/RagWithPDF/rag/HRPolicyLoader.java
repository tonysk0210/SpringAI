package com.example.RagWithPDF.rag;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
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
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(policyFile);
        List<Document> docs = tikaDocumentReader.get();
        vectorStore.add(docs);
    }
}

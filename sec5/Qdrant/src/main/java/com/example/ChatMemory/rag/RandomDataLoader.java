package com.example.ChatMemory.rag;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RandomDataLoader {

    private final VectorStore vectorStore;

    @Autowired
    public RandomDataLoader(VectorStore vectorStore) {
        // spring.ai.vectorstore.qdrant.* in properties file determines which VectorStore implementation is injected here
        this.vectorStore = vectorStore;
    }

    //這個特定 Bean 初始化完成後、正式可用之前 執行一些初始化邏輯
    @PostConstruct
    public void LoadSentenceIntoVectorStore() {
        List<String> sentence = List.of("Java is used for building scalable enterprise applications.", "Python is commonly used for machine learning and automation tasks.", "JavaScript is essential for creating interactive web pages.", "Docker packages applications into lightweight containers.", "Kubernetes automates container orchestration at scale.", "Redis is an in-memory data store used for caching.", "PostgreSQL supports complex queries and full ACID compliance.", "Kafka is a distributed event streaming platform.", "REST APIs allow stateless client-server communication.", "GraphQL enables clients to fetch exactly the data they need.", "Credit scores influence the interest rates on loans.", "Mutual funds pool money from investors to buy securities.", "Bitcoin operates on a decentralized peer-to-peer network.", "Ethereum supports smart contract deployment.", "The stock market opens at 9:30 a.m. EST on weekdays.", "Compound interest increases investment returns over time.", "Diversifying investments reduces overall risk.", "A blockchain is a distributed, immutable ledger of transactions.", "Photosynthesis is how plants convert sunlight into energy.", "The water cycle involves evaporation, condensation, and precipitation.", "The ozone layer protects Earth from harmful ultraviolet rays.", "Earth revolves around the Sun in an elliptical orbit.", "Lightning is a discharge of electricity caused by charged clouds.", "DNA is the molecule that carries genetic instructions in living organisms.", "Volcanoes form when magma rises through Earth's crust.", "Earthquakes are caused by sudden tectonic shifts.", "The Sahara is the largest hot desert in the world.", "Mount Kilimanjaro is the tallest mountain in Africa.", "Japan is known for its cherry blossoms and advanced technology.", "The Great Wall of China is over 13,000 miles long.", "Niagara Falls is located between Canada and the U.S.", "The Amazon River is the second longest river in the world.", "Oats are high in fiber and help reduce cholesterol.", "Drinking water improves digestion and skin health.", "A balanced diet includes proteins, carbs, fats, and vitamins.", "Broccoli is rich in vitamins A, C, and K.", "Green tea contains antioxidants beneficial for metabolism.", "Too much sugar increases the risk of diabetes.", "Walking 30 minutes a day improves cardiovascular health.", "Meditation can reduce stress and improve focus.", "Gratitude journaling is linked to higher happiness levels.", "Deep breathing exercises help regulate anxiety.", "Reading daily improves vocabulary and cognitive function.", "Setting daily goals increases productivity.", "STEM stands for Science, Technology, Engineering, and Mathematics.", "Bloom’s taxonomy categorizes educational goals.", "Project-based learning enhances student engagement.", "Online courses offer flexibility for remote learners.", "Flashcards are effective for memorizing vocabulary.", "Agile methodology promotes iterative software development.", "OKRs help align team goals with business strategy.", "Remote work offers flexibility but requires clear communication.", "CRM systems manage customer relationships and sales pipelines.", "SWOT analysis identifies strengths, weaknesses, opportunities, and threats.");
        List<Document> documents = sentence.stream().map(Document::new).toList(); //不可修改的 List（immutable）
        vectorStore.add(documents);
    }
}

/**
 * Spring Boot 啟動
 * │
 * ▼
 * Spring 建立 ApplicationContext
 * │
 * ├─ 掃描 @Component → 建立 RandomDataLoader Bean
 * │
 * ├─ 檢查建構子參數 → 自動注入 VectorStore 物件
 * │
 * ├─ Bean 建立完成
 * │
 * └─ 執行 @PostConstruct 方法 → LoadSentenceIntoVectorStore()
 */

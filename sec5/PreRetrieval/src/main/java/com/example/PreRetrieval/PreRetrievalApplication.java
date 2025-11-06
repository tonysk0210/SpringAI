package com.example.PreRetrieval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 標記此類別為 Spring Boot 自動配置與元件掃描的起始點。
public class PreRetrievalApplication {

    public static void main(String[] args) {
        // 啟動 Spring Boot 應用程式並建立整個應用程式的運行環境。
        SpringApplication.run(PreRetrievalApplication.class, args);
    }

}

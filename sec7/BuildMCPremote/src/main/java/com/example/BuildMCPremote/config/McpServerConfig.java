package com.example.BuildMCPremote.config;

import com.example.BuildMCPremote.tools.HelpDeskTools;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class McpServerConfig {
    // ToolCallback 是 Spring AI 在「模型呼叫工具」時，自動回傳工具執行結果的機制
    @Bean
    List<ToolCallback> toolCallbacks(HelpDeskTools helpDeskTools) {
        return List.of(ToolCallbacks.from(helpDeskTools));
    }
}

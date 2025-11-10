package com.example.mcpClientGithub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class McpClientGithubApplication {

	public static void main(String[] args) {
        System.out.println("OPENAI_API_KEY=" + System.getenv("OPENAI_API_KEY"));
        System.out.println("GITHUB_PAT=" + System.getenv("GITHUB_PAT"));
		SpringApplication.run(McpClientGithubApplication.class, args);
	}

}

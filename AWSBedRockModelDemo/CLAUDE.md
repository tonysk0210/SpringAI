# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

DockerModelDemo is a Spring Boot 3.5.6 application demonstrating integration with Spring AI 1.0.3 to interact with OpenAI-compatible chat models. Despite the name, this project is configured to work with a local AI model server (running at localhost:12434) rather than OpenAI directly.

## Technology Stack

- Java 21
- Spring Boot 3.5.6
- Spring AI 1.0.3 (spring-ai-starter-model-openai)
- Maven for build management
- JUnit 5 for testing

## Build and Run Commands

### Build the project
```bash
./mvnw clean install
```

### Run the application
```bash
./mvnw spring-boot:run
```

The application starts on the default port (8080) unless configured otherwise.

### Run tests
```bash
./mvnw test
```

### Run a specific test class
```bash
./mvnw test -Dtest=DockerModelDemoApplicationTests
```

### Run a specific test method
```bash
./mvnw test -Dtest=DockerModelDemoApplicationTests#contextLoads
```

### Package the application
```bash
./mvnw package
```

## Architecture

### Application Structure

The application follows standard Spring Boot conventions with a simple REST API architecture:

- **Main Application**: `DockerModelDemoApplication` - Standard Spring Boot entry point
- **REST Controller**: `ChatController` - Exposes `/api/chat` endpoint for AI interactions
- **AI Integration**: Uses Spring AI's `ChatClient` with builder pattern for dependency injection

### AI Model Configuration

The application uses Spring AI's OpenAI integration but is configured to point to a local model server:

- **Base URL**: `http://localhost:12434/engines` (configured in application.properties)
- **Model**: `ai/gemma3` (likely Gemma 3 running locally)
- **API Key**: Set to "dummy" (not validated by local server)

This allows development and testing with a local LLM without requiring OpenAI API credentials.

### ChatClient Pattern

The `ChatController` uses constructor injection to receive `ChatClient.Builder` and builds a `ChatClient` instance. This builder is auto-configured by Spring AI based on the application.properties settings. The ChatClient provides a fluent API:

```java
chatClient.prompt(message).call().content()
```

This pattern chains: prompt creation → model call → content extraction.

## API Endpoints

### GET /api/chat

Sends a message to the AI model and returns the response.

**Query Parameter**: `message` - The user's message to the AI

**Example**:
```bash
curl "http://localhost:8080/api/chat?message=Hello"
```

## Configuration Notes

- Logging pattern is customized to show timestamp, level, thread name, logger, and message with color coding
- The application name is "DockerModelDemo" but the POM artifact name shows "OpenAIDemo" (inconsistency to be aware of)
- Spring DevTools is included for development hot-reload

## Testing

Tests follow standard Spring Boot testing conventions using `@SpringBootTest`. The current test suite only includes a context load test to verify the application starts correctly.

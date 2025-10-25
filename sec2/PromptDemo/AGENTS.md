# Repository Guidelines

## Language Preference
All documentation, comments, commit messages, and communication should be written in **Traditional Chinese (繁體中文)** unless interfacing with external English-only APIs or libraries. Code identifiers (class names, method names, variables) follow English naming conventions for compatibility, but all explanatory text uses Traditional Chinese.

## Project Structure & Module Organization
Java sources live under `src/main/java/com/example/PromptDemo`; use `ChatController` for REST endpoints and `chatClientConfig/ChatClientConfig.java` for model wiring so new components should follow the same package to inherit auto-configuration. The application entry point is `PromptDemoApplication.java`. Shared configuration belongs in `src/main/resources/application.properties`, while test fixtures and integration setups sit alongside test classes in `src/test/java/com/example/PromptDemo`.

## Build, Test, and Development Commands
- `./mvnw spring-boot:run` launches the API locally with devtools hot reloading; prefer this during feature work.
- `./mvnw clean package` produces the runnable jar in `target/` and reruns unit tests; use before publishing artifacts.
- `./mvnw test` executes JUnit 5 tests without packaging; ideal for quick verification.
- `./mvnw verify -DskipTests=false` runs the full Maven lifecycle plus checks from applied plugins; use in CI or before large merges.

## Coding Style & Naming Conventions
Target Java 21 (per `pom.xml`). Use four-space indentation, K&R braces, and keep lines under 120 characters. Class names are `PascalCase`; components end with role-specific suffixes (e.g., `ChatController`, `ChatClientConfig`). Methods and variables use `camelCase`; constants are `UPPER_SNAKE_CASE`. Rely on Spring annotations for dependency injection instead of manual wiring. Run `./mvnw spotless:apply` if a formatting profile is added; otherwise, match the existing style.

## Testing Guidelines
Tests reside in `src/test/java` and use `spring-boot-starter-test` (JUnit Jupiter + Mockito). Name test classes `*Tests` and describe scenarios in method names, e.g., `respondsWithAiMessageWhenPromptProvided`. Cover new endpoints with both service-level unit tests and lightweight `@SpringBootTest` slices when HTTP wiring is involved. Ensure tests pass with `./mvnw test` before submitting changes; add integration tests when touching external model calls.

## Commit & Pull Request Guidelines
The current history favors short, imperative subjects such as `complete system role demo`; follow that style and limit to 72 characters. Reference issue IDs in the body when available and note any configuration updates (e.g., new environment variables). Pull requests should include: a concise summary of behavior changes, test evidence or command output, environment or secret prerequisites, and screenshots or sample requests when UI or API responses change.

## Configuration & Security Notes
Do not commit secrets; `application.properties` may reference placeholders, but supply actual keys via environment variables (e.g., `SPRING_AI_OPENAI_API_KEY`). Document any new configuration flags in that file and mirror them in README or deployment notes. Rotate API keys after demos and scrub sample prompts of sensitive data.
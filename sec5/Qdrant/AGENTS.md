# Repository Guidelines

本指南協助新貢獻者快速掌握 PromptTemplateDemo 專案的結構、流程與品質要求，以維持一致的開發體驗。

## 專案結構與模組組織
- 核心程式碼位於 `src/main/java/com/example/PromptTemplateDemo`，`PromptTemplateDemoApplication.java` 為 Spring Boot 入口點，REST 控制器集中於 `PromptTemplateController.java`，模型與客戶端設定則置於 `chatClientConfig/ChatClientConfig.java` 以延續 Spring AI 自動設定。
- 測試資源存放在 `src/test/java/com/example/PromptTemplateDemo`，共用測試資料與輔助類別請放在對應測試套件旁，以便維護與重用。
- 應用程式設定集中於 `src/main/resources/application.properties`；新增旗標或外部整合時務必同步更新說明文件與部署腳本。

## 建置、測試與開發指令
- `./mvnw spring-boot:run`：啟動內建伺服器並啟用 devtools 熱重載，適合本地開發與快速迭代。
- `./mvnw clean package`：清理、編譯並於 `target/` 產生可執行 jar，同步重新執行單元測試。
- `./mvnw test`：僅執行 JUnit 測試；`./mvnw verify -DskipTests=false`：執行完整 Maven 生命週期與附加外掛檢查，作為 CI 或重大合併前驗證。

## 程式風格與命名規範
- 目標平台為 Java 21，採四空白縮排、K&R 花括號、每行不超過 120 字元。
- 類別命名使用 PascalCase，方法與變數採 camelCase，常數則以 UPPER_SNAKE_CASE 命名。
- 透過 Spring 註解 (`@Component`、`@Service`、`@Configuration`) 進行依賴注入；新增組態時請集中於現有配置模組並提供註釋。
- 若引入格式化流程，先執行 `./mvnw spotless:apply`，避免同時使用衝突的格式化器。

## 測試指南
- 使用 `spring-boot-starter-test`（JUnit Jupiter + Mockito）。新功能至少涵蓋服務與控制器的成功與失敗情境。
- 測試類別命名為 `*Tests`，方法名稱建議以 `should...When...` 或 `respondsWith...` 描述行為與條件。
- 在提交前執行 `./mvnw test`；涉及 HTTP 交互時補充 `@SpringBootTest` 或 `@WebMvcTest` 切片測試，必要時搭配模擬的模型回應。

## 提交與合併請求規範
- Commit 標題維持 72 字元內且使用祈使句，例如 `Add prompt template fallback`；需要時於內文引用 Issue 編號或補充說明。
- PR 必須包含功能摘要、測試證據（指令輸出或截圖）、新環境變數或設定調整與對應文件連結，API 變更需提供請求／回應樣本。
- 引入設定或依賴更新時，請補充部署注意事項並通知維運角色，視需求新增回滾計畫。

## 安全與設定提示
- 所有敏感金鑰以環境變數提供，例如 `OPENAI_API_KEY`，禁止硬編碼或提交至版本控制。
- 更新 `application.properties` 後，務必同步更新 README 或部署筆記並註明預設值與覆寫方式。
- 範例提示或測試資料不得包含敏感資訊。完成示範後請旋轉金鑰並清理不必要的輸出檔案。

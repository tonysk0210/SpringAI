package com.example.ToolCallingWithDB.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZoneId;

@Component
public class TimeTools {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeTools.class);

    @Tool(name = "getCurrentLocalTime", description = "Get current time in the user's timezone")
    public String getCurrentLocalTime() {
        LOGGER.info("Returning current time in the user's timezone");
        return LocalTime.now().toString();
    }

    // @ToolParam 讓你為方法參數添加語義說明與提示資訊，讓模型更容易正確使用工具
    @Tool(name = "getCurrentTime", description = "Get the current time in the specified time zone.")
    public String getCurrentTime(@ToolParam(description = "Value representing the time zone") String timeZone) {
        LOGGER.info("Returning the current time in the timezone {}", timeZone);
        return LocalTime.now(ZoneId.of(timeZone)).toString();
    }
    /**
     * 資訊來源	內容
     * ① 使用者輸入	「現在台北幾點？」 → 模型知道地點是台北
     * ② 工具說明（@Tool 的 description）	"Get the current time in the specified time zone"
     * ③ 參數說明（@ToolParam 的 description）	"Value representing the time zone, e.g., Asia/Taipei"
     *
     * Spring AI 傳給模型的 prompt 包含工具說明：
     * {
     *   "tools": [
     *     {
     *       "name": "getCurrentTime",
     *       "description": "Get the current time in the specified time zone.",
     *       "parameters": {
     *         "type": "object",
     *         "properties": {
     *           "timeZone": {
     *             "type": "string",
     *             "description": "Value representing the time zone, e.g., Asia/Taipei"
     *           }
     *         },
     *         "required": ["timeZone"]
     *       }
     *     }
     *   ]
     * }
     */
}

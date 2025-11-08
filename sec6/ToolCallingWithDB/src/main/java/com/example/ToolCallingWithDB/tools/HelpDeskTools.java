package com.example.ToolCallingWithDB.tools;

import com.example.ToolCallingWithDB.entity.HelpDeskTicket;
import com.example.ToolCallingWithDB.model.TicketRequest;
import com.example.ToolCallingWithDB.service.HelpDeskTicketService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HelpDeskTools {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelpDeskTools.class);

    private final HelpDeskTicketService helpDeskTicketService;

    /**
     * 當 Spring AI 把請求送給 LLM（例如 GPT-4 / Ollama Gemma）時，
     * 它只會包含：
     * 1. 使用者訊息 (user(message))
     * 2. 系統提示詞（若有）
     * 3. 工具描述（@Tool 與 @ToolParam 的 metadata）
     * <p>
     * 實際送出的 JSON 結構大概像這樣👇
     * {
     * "name": "createTicket",                                                    ← 工具名稱
     * "description": "Create a Support Ticket",                                  ← 工具的說明
     * "parameters": {                                                            ← 工具輸入的結構定義 (JSON Schema)
     * "type": "object",
     * "properties": {                                                          properties 層 — 模型要填的欄位
     * "ticketRequest": {                                                     你方法的 @ToolParam TicketRequest ticketRequest 一個物件
     * "type": "object",
     * "description": "Details to create a Support Ticket",
     * "properties": {                                                      properties 層 — 模型要填的欄位
     * "issue": {                                                         TicketRequest 裡的 String issue 欄位字串內容
     * "type": "string",
     * "description": "issue"
     * }
     * },
     * "required": ["issue"]                                                 required 層 — 告訴模型哪些欄位「一定要有」
     * }
     * },
     * "required": ["ticketRequest"]
     * }
     * }
     */
    // 讓 LLM 知道這是一個可呼叫的「功能（function/tool）」
    @Tool(name = "createTicket", description = "Create a Support Ticket")
    String createTicket(@ToolParam(description = "Details to create a Support Ticket") TicketRequest ticketRequest, // @ToolParam — 告訴 LLM 這個參數是可被模型提供的輸入
                        ToolContext toolContext) {
        String username = (String) toolContext.getContext().get("username"); // 從 ToolContext 取得使用者名稱
        LOGGER.info("Creating support ticket for user: {} with details : {}", username, ticketRequest);
        HelpDeskTicket savedTicket = helpDeskTicketService.createTicket(ticketRequest, username); // 呼叫 Service 層方法創建工單
        LOGGER.info("Support ticket created successfully for user: {} with Ticket ID: {}", savedTicket.getUsername(), savedTicket.getId());
        return "Ticket #" + savedTicket.getId() + " created successfully for user " + savedTicket.getUsername();
    }


    @Tool(name = "getTicketStatus", description = "Fetch the status of the open tickets based on the user's username")
    List<HelpDeskTicket> getTicketStatus(ToolContext toolContext) {
        String username = (String) toolContext.getContext().get("username");
        LOGGER.info("Fetching tickets status for user: {}", username);
        List<HelpDeskTicket> tickets = helpDeskTicketService.getTicketsByUsername(username);
        LOGGER.info("Found {} tickets for user: {}", tickets.size(), username);
        return tickets;
    }
}

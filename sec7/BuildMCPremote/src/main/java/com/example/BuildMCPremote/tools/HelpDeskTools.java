package com.example.BuildMCPremote.tools;

import com.example.BuildMCPremote.entity.HelpDeskTicket;
import com.example.BuildMCPremote.model.TicketRequest;
import com.example.BuildMCPremote.service.HelpDeskTicketService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HelpDeskTools {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelpDeskTools.class);

    private final HelpDeskTicketService helpDeskTicketService;

    // 讓 LLM 知道這是一個可呼叫的「功能（function/tool）」
    @Tool(name = "createTicket", description = "Create a Support Ticket")
    String createTicket(@ToolParam(description = "Details to create a Support Ticket") TicketRequest ticketRequest) {
        LOGGER.info("Creating support ticket for user: {} with details : {}", ticketRequest);
        HelpDeskTicket savedTicket = helpDeskTicketService.createTicket(ticketRequest); // 呼叫 Service 層方法創建工單
        LOGGER.info("Support ticket created successfully for user: {} with Ticket ID: {}", savedTicket.getUsername(), savedTicket.getId());
        return "Ticket #" + savedTicket.getId() + " created successfully for user " + savedTicket.getUsername();
    }


    @Tool(name = "getTicketStatus", description = "Fetch the status of the open tickets based on the user's username")
    List<HelpDeskTicket> getTicketStatus(@ToolParam(description = "Username to fetch the status of the help desk ticket") String username) {
        LOGGER.info("Fetching tickets status for user: {}", username);
        List<HelpDeskTicket> tickets = helpDeskTicketService.getTicketsByUsername(username);
        LOGGER.info("Found {} tickets for user: {}", tickets.size(), username);
        return tickets;
    }
}


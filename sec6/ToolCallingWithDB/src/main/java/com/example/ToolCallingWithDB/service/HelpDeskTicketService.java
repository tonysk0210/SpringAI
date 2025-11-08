package com.example.ToolCallingWithDB.service;

import com.example.ToolCallingWithDB.entity.HelpDeskTicket;
import com.example.ToolCallingWithDB.model.TicketRequest;
import com.example.ToolCallingWithDB.repository.HelpDeskTicketRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HelpDeskTicketService {

    private final HelpDeskTicketRepo helpDeskTicketRepo;

    public HelpDeskTicket createTicket(TicketRequest ticketInput, String username) {
        HelpDeskTicket ticket = HelpDeskTicket.builder()
                .issue(ticketInput.issue())
                .username(username)
                .status("OPEN")
                .createdAt(LocalDateTime.now())
                .eta(LocalDateTime.now().plusDays(7))
                .build();
        return helpDeskTicketRepo.save(ticket);
    }

    public List<HelpDeskTicket> getTicketsByUsername(String username) {
        return helpDeskTicketRepo.findByUsername(username);
    }
}

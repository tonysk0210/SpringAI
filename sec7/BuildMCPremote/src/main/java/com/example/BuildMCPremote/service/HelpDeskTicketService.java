package com.example.BuildMCPremote.service;

import com.example.BuildMCPremote.entity.HelpDeskTicket;
import com.example.BuildMCPremote.model.TicketRequest;
import com.example.BuildMCPremote.repo.HelpDeskTicketRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HelpDeskTicketService {

    private final HelpDeskTicketRepo helpDeskTicketRepo;

    public HelpDeskTicket createTicket(TicketRequest ticketInput) {
        HelpDeskTicket ticket = HelpDeskTicket.builder()
                .issue(ticketInput.issue())
                .username(ticketInput.username())
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

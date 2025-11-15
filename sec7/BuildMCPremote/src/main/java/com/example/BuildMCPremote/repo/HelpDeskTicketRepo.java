package com.example.BuildMCPremote.repo;

import com.example.BuildMCPremote.entity.HelpDeskTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HelpDeskTicketRepo extends JpaRepository<HelpDeskTicket, Long> {

    // SELECT * FROM helpdesk_ticket WHERE username = ?;
    List<HelpDeskTicket> findByUsername(String username); // Derived Query Method
}

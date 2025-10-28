package com.example.PojoJson;

import com.example.PojoJson.model.CountryCities;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PojoJsonController {
    
    private final ChatClient chatClient;

    @Autowired
    public PojoJsonController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/pojo-json")
    public ResponseEntity<CountryCities> chat(@RequestParam(value = "message") String message) {
        CountryCities pojo = chatClient.prompt().user(message).call().entity(CountryCities.class);
        return ResponseEntity.ok(pojo);
    }
}
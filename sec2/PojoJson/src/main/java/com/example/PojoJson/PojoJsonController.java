package com.example.PojoJson;

import com.example.PojoJson.model.CountryCities;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PojoJsonController {

    private final ChatClient chatClient;

    @Autowired
    public PojoJsonController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    // return POJO from JSON response
    @GetMapping("/pojo-json")
    public ResponseEntity<CountryCities> chat(@RequestParam(value = "message") String message) {
        CountryCities pojo = chatClient.prompt().user(message).call().entity(CountryCities.class);
        return ResponseEntity.ok(pojo);
    }

    // return POJO from JSON response with BeanOutputConverter
    @GetMapping("/bean")
    public ResponseEntity<CountryCities> bean(@RequestParam(value = "message") String message) {
        CountryCities bean = chatClient.prompt().user(message).call().entity(new BeanOutputConverter<>(CountryCities.class));
        return ResponseEntity.ok(bean);
    }


    // return list of String
    @GetMapping("/list")
    public ResponseEntity<List<String>> list(@RequestParam(value = "message") String message) {
        List<String> listOfString = chatClient.prompt().user(message).call().entity(new ListOutputConverter());
        return ResponseEntity.ok(listOfString);
    }

    // return map of String to Object
    @GetMapping("/map")
    public ResponseEntity<Map<String, Object>> map(@RequestParam(value = "message") String message) {
        Map<String, Object> map = chatClient.prompt().user(message).call().entity(new MapOutputConverter());
        return ResponseEntity.ok(map);
    }

    // return list of POJO from JSON response
    @GetMapping("/listOfPojo")
    public ResponseEntity<List<CountryCities>> listOfPojo(@RequestParam(value = "message") String message) {
        List<CountryCities> listOfPojo = chatClient.prompt().user(message).call().entity(new ParameterizedTypeReference<List<CountryCities>>() {
        });
        return ResponseEntity.ok(listOfPojo);
    }
}
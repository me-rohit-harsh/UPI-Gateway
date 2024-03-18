package com.emailSender.Controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Controller
public class MyController {
	 @PostMapping("/post")
	    public ResponseEntity<String> handlePostRequest(@RequestBody String requestBody) {
	        // Handle the POST request here
	        return ResponseEntity.ok("Received POST request with data: " + requestBody);
	    }
	
	@GetMapping("/post")
	public String checkPost() {
		return "posttest";
	}
	
    private final WebClient webClient;

    public MyController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:9090").build();
    }

    @PostMapping("/sendData")
    @ResponseBody
    public Mono<String> sendData(@RequestBody String data) {
        // Make POST request using WebClient
        return webClient.post()
                .uri("/post")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(data))
                .retrieve()
                .bodyToMono(String.class); // Convert response to Mono<String>
    }
}

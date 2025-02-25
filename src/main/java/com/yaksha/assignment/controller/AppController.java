package com.yaksha.assignment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
public class AppController {

	private final RestTemplate restTemplate;

	public AppController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@GetMapping("/sendApiRequest")
	public String sendApiRequest(@RequestParam String apiUrl) {
		try {
			// Sending GET request to the provided API URL
			ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

			// Return the response body if the request is successful
			return processApiResponse(response.getBody());
		} catch (HttpClientErrorException e) {
			// Handle client-side errors (e.g., 404 or 400 errors)
			return "Error: Client-side error - " + e.getStatusCode();
		} catch (Exception e) {
			// Handle general errors (e.g., connection issues)
			return "Error: " + e.getMessage();
		}
	}

	public String processApiResponse(String response) {
		// Example processing of the response, this can be customized as needed
		if (response == null || response.isEmpty()) {
			return "Error: Empty response from API";
		}
		return "API Response: " + response;
	}
}

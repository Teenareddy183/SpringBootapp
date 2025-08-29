package com.example.bajaj;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {

    private static final String GENERATE_URL =
            "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

    private static final String SUBMIT_URL =
            "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WebhookService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public void startProcess() {
        try {
            // 1. Call generateWebhook
            String body = """
                    {
                      "name": "maramreddy teenareddy",
                      "regNo": "22BDS0218",
                      "email": "mteenareddy@gmail.com"
                    }
                    """;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(GENERATE_URL, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String webhookUrl = jsonNode.get("webhook").asText();
                String accessToken = jsonNode.get("accessToken").asText();

                System.out.println("✅ Webhook URL: " + webhookUrl);
                System.out.println("✅ Access Token: " + accessToken);

                // 2. Solve SQL (you must open the right link manually and write SQL)
                String finalQuery = "SELECT * FROM your_table;"; // Replace with actual SQL

                // 3. Submit solution
                submitSolution(webhookUrl, accessToken, finalQuery);
            } else {
                System.out.println("❌ Failed: " + response.getStatusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void submitSolution(String webhookUrl, String token, String sqlQuery) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            String body = """
                    {
                      "finalQuery": "%s"
                    }
                    """.formatted(sqlQuery);

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(SUBMIT_URL, request, String.class);

            System.out.println("✅ Submission Response: " + response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

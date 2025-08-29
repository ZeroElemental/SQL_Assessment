package Assessment.Bajaj_22BIT0113;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Component
public class AppRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Application has started. Beginning the process...");

        // === Part 1: Generate the Webhook ===
        RestTemplate restTemplate = new RestTemplate();
        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        // *** IMPORTANT: FILL IN YOUR DETAILS HERE ***
        RegistrationRequest registration = new RegistrationRequest("Your Name", "REG12347", "your.email@example.com");

        HttpEntity<RegistrationRequest> requestEntity = new HttpEntity<>(registration);

        WebhookResponse webhookResponse = restTemplate.postForObject(generateUrl, requestEntity, WebhookResponse.class);

        if (webhookResponse == null || webhookResponse.getWebhookURL() == null) {
            System.err.println("Failed to get webhook response.");
            return;
        }

        System.out.println("Successfully received webhook URL: " + webhookResponse.getWebhookURL());
        System.out.println("Successfully received accessToken.");

        // === Part 2: Determine and Solve the SQL Problem ===
        String regNo = registration.getRegNo();
        int lastTwoDigits = Integer.parseInt(regNo.substring(regNo.length() - 2));

        String finalSqlQuery;
        if (lastTwoDigits % 2 != 0) {
            // Odd Number Logic for Question 1
            System.out.println("Registration number " + regNo + " ends in an odd number. Using solution for Question 1.");
            // *** THE SQL QUERY IS PASTED HERE ***
            finalSqlQuery = "SELECT p.AMOUNT AS SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, d.DEPARTMENT_NAME FROM PAYMENTS p JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID WHERE EXTRACT(DAY FROM p.PAYMENT_TIME) != 1 ORDER BY p.AMOUNT DESC LIMIT 1;";
        } else {
            // Even Number Logic for Question 2
            System.out.println("Registration number " + regNo + " ends in an even number. Using solution for Question 2.");
            // If your number is even, you would solve Question 2 and paste its query here.
            finalSqlQuery = "REPLACE_THIS_WITH_YOUR_SQL_QUERY_FOR_QUESTION_2";
        }
        
        System.out.println("Final SQL Query to be submitted: " + finalSqlQuery);

        // === Part 3: Submit the Solution ===
        String submitUrl = webhookResponse.getWebhookURL();
        String accessToken = webhookResponse.getAccessToken();

        SubmissionRequest submission = new SubmissionRequest(finalSqlQuery);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<SubmissionRequest> submissionEntity = new HttpEntity<>(submission, headers);
        
        ResponseEntity<String> submissionResponse = restTemplate.postForEntity(submitUrl, submissionEntity, String.class);

        if (submissionResponse.getStatusCode() == HttpStatus.OK) {
            System.out.println("✅ Solution submitted successfully!");
            System.out.println("Response: " + submissionResponse.getBody());
        } else {
            System.err.println("❌ Failed to submit solution. Status code: " + submissionResponse.getStatusCode());
            System.err.println("Response: " + submissionResponse.getBody());
        }
    }
}

// === Helper Classes for JSON Bodies (No changes needed here) ===

class RegistrationRequest {
    private String name;
    private String regNo;
    private String email;

    public RegistrationRequest(String name, String regNo, String email) {
        this.name = name;
        this.regNo = regNo;
        this.email = email;
    }
    public String getName() { return name; }
    public String getRegNo() { return regNo; }
    public String getEmail() { return email; }
}

class WebhookResponse {
    private String webhookURL;
    private String accessToken;

    public String getWebhookURL() { return webhookURL; }
    public void setWebhookURL(String webhookURL) { this.webhookURL = webhookURL; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}

class SubmissionRequest {
    private String finalQuery;

    public SubmissionRequest(String finalQuery) {
        this.finalQuery = finalQuery;
    }
    public String getFinalQuery() { return finalQuery; }
}
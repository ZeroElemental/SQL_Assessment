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

        RestTemplate restTemplate = new RestTemplate();
        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        RegistrationRequest registration = new RegistrationRequest("Shreyash Anand", "22BIT0113", "anandshreyash747@gmail.com");

        HttpEntity<RegistrationRequest> requestEntity = new HttpEntity<>(registration);

        WebhookResponse webhookResponse = restTemplate.postForObject(generateUrl, requestEntity, WebhookResponse.class);

        if (webhookResponse == null || webhookResponse.getWebhookURL() == null) {
            System.err.println("Failed to get webhook response.");
            return;
        }

        System.out.println("Successfully received webhook URL: " + webhookResponse.getWebhookURL());
        System.out.println("Successfully received accessToken.");


        String finalSqlQuery;
        
            
        finalSqlQuery = "SELECT p.AMOUNT AS SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, d.DEPARTMENT_NAME FROM PAYMENTS p JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID WHERE EXTRACT(DAY FROM p.PAYMENT_TIME) != 1 ORDER BY p.AMOUNT DESC LIMIT 1;";
        
        
        System.out.println("Final SQL Query to be submitted: " + finalSqlQuery);

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
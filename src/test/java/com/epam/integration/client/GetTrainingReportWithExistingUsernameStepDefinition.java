package com.epam.integration.client;

import com.epam.integration.dto.TrainingReportResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GetTrainingReportWithExistingUsernameStepDefinition {

    private String port = "8888";

    @Autowired
    private RestTemplate restTemplate;
    private ResponseEntity<TrainingReportResponse> trainingReportResponse;
    private String urlWithParams;
    private Logger logger = LoggerFactory.getLogger(GetTrainingReportWithExistingUsernameStepDefinition.class);



    @Given("the user wants to fetch the training report from report service by {string} username and the report exists")
    public void get_training_report_by_existing_username(String username){
        String url =  "http://localhost:" + port + "/api/training-reports";
        urlWithParams = UriComponentsBuilder.fromUriString(url)
                .queryParam("trainerUsername", username)
                .toUriString();
        logger.info("The url with request parameter trainerUsername {} was formed", username);
    }

    @When("the system receives the get request then the training report is fetched from the database")
    public void the_system_receives_the_get_request(){
        HttpEntity<?>httpEntity = HttpEntity.EMPTY;
        trainingReportResponse = restTemplate.exchange(urlWithParams,
                HttpMethod.GET,
                httpEntity,
                TrainingReportResponse.class
        );
        logger.info("The get request for fetching the training report was sent to the report microservice");
    }

    @Then("the TrainingReport is received as a response with status {int}")
    public void the_system_respond_with_status_200_and_training_report(int expected){
        assertNotNull(trainingReportResponse);
        assertEquals(expected, trainingReportResponse.getStatusCode().value());
        TrainingReportResponse response = trainingReportResponse.getBody();
        assertNotNull(response);
        assertFalse(response.getYears().isEmpty());
        logger.info("The Training Report was retrieved successfully with the following response "+ response);
    }

}

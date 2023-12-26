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
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetTrainingReportWithNonExistingUsernameStepDefinition {

    private String port = "8888";

    @Autowired
    private RestTemplate restTemplate;
    private ResponseEntity<TrainingReportResponse> trainingReportResponse;
    private String urlWithParams;
    private Logger logger = LoggerFactory.getLogger(GetTrainingReportWithNonExistingUsernameStepDefinition.class);

    @Given("the user wants to fetch the training report from report service by username {string} and the report does not exist")
    public void get_training_report_by_non_existing_username(String username){
        String url =  "http://localhost:" + port + "/api/training-reports";
        urlWithParams = UriComponentsBuilder.fromUriString(url)
                .queryParam("trainerUsername", username)
                .toUriString();
        logger.info("The url with request parameter for non existing trainerUsername  {} was formed", username);
    }

    @When("the system receives the request and tries to fetch the report from the database")
    public void the_system_receives_the_get_request_with_non_existing_username(){
        HttpEntity<?>httpEntity = HttpEntity.EMPTY;
        trainingReportResponse = restTemplate.exchange(urlWithParams,
                HttpMethod.GET,
                httpEntity,
                TrainingReportResponse.class
        );
        logger.info("The get request for fetching the training report for not existing username was sent to the report microservice");
    }

    @Then("the training report for the given username is not found, the empty Training Report is returned")
    public void the_system_respond_with_status_200_and_training_report(){
        assertNotNull(trainingReportResponse);
        TrainingReportResponse response = trainingReportResponse.getBody();
        assertNotNull(response);
        assertTrue(response.getYears().isEmpty());
        logger.info("The Training Report was retrieved successfully with the empty response: "+ response);
    }

}

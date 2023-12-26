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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class GetTrainingRecordWithNullRequestParamStepDefinition {

    private String port = "8888";

    @Autowired
    private RestTemplate restTemplate;
    private Logger logger = LoggerFactory.getLogger(GetTrainingReportWithNonExistingUsernameStepDefinition.class);
    private String url;
    private HttpClientErrorException.BadRequest badRequestException;

    @Given("the user wants to fetch the training report from report service but forget to pass a username as a param")
    public void get_training_report_without_request_param() {
        url = "http://localhost:" + port + "/api/training-reports";
        logger.info("The url without a request parameter was formed");
    }

    @When("the system receives the request and failed to validate the request")
    public void the_system_fails_to_validate_the_request() {
        HttpEntity<?> httpEntity = HttpEntity.EMPTY;
        try {
            logger.info("The get request without request parameter was sent to the report microservice");
            restTemplate.exchange(url,
                    HttpMethod.GET,
                    httpEntity,
                    TrainingReportResponse.class
            );
        } catch (HttpClientErrorException.BadRequest ex) {
            logger.error("The BadRequest exception was thrown due to null username parameter");
            badRequestException = ex;
        }
    }

    @Then("the system throws an exception and the ResponseEntity with the status {int} bad request is returned")
    public void the_system_respond_with_status_400(int response) {
        assertNotNull(badRequestException);
        assertEquals(response, badRequestException.getStatusCode().value());
    }
}

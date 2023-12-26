package com.epam.integration.client;

import com.epam.integration.dto.AuthenticationDTO;
import com.epam.integration.dto.JwtResponseDTO;
import com.epam.integration.dto.RegistrationResponse;
import com.epam.integration.dto.TrainerRegisterRequest;
import com.epam.integration.dto.TrainingCreateRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.Calendar;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AddTrainingFailureStepDefinition {

    @Autowired
    private RestTemplate restTemplate;
    private String port = "8080";
    private RegistrationResponse trainerRegistrationResponse;
    private HttpEntity<TrainingCreateRequest> requestEntity;
    private JwtResponseDTO jwtResponse;
    private Logger logger = LoggerFactory.getLogger(AddTrainingFailureStepDefinition.class);
    private HttpClientErrorException.BadRequest badRequestException;


    @Given("a user sends the post request to the specified url to add new training to the system but missed to insert trainee username")
    public void add_training_with_invalid_data() {
        addTrainer();
        String trainerUsername = trainerRegistrationResponse.getLogin();
        String trainerPassword = trainerRegistrationResponse.getPassword();
        getToken(trainerUsername, trainerPassword);
        String jwt = jwtResponse.getJwt();
        requestEntity = new HttpEntity<>(trainingCreateDto(trainerUsername),
                getHeaders(jwt));
        logger.info("The request entity with TrainingCreateRequest is created with missed trainee username");

    }

    @When("the system receives the request and validates data the exception is thrown due to bad request data")
    public void receive_the_request_and_validate_and_throws_the_exception() {
        String url = "http://localhost:" + port + "/trainings";
        try{
            restTemplate.exchange(url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
        }catch (HttpClientErrorException.BadRequest e) {
            logger.error("The exception was thrown due to invalid data");
            badRequestException = e;
        }
    }

    @Then("the status {int} - Bad Request is returned to the user")
    public void return_status_400_bad_request(int expected) {
        assertNotNull(badRequestException);
        assertEquals(HttpStatus.valueOf(expected), badRequestException.getStatusCode());
        logger.info("The status code {} was received as a response", badRequestException.getStatusCode().value());
    }

    private void addTrainer() {
        TrainerRegisterRequest trainerRegisterRequest = new TrainerRegisterRequest();
        trainerRegisterRequest.setFirstName("Lara");
        trainerRegisterRequest.setLastName("Black");
        trainerRegisterRequest.setSpecializationId(3L);
        String url = "http://localhost:" + port + "/trainers";
        trainerRegistrationResponse = restTemplate.postForObject(url, trainerRegisterRequest, RegistrationResponse.class);
        assertNotNull(trainerRegistrationResponse);
        logger.info("The new trainer {} was added to the gym microservice", trainerRegistrationResponse.getLogin());
    }


    private void getToken(String login, String password) {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO();
        authenticationDTO.setPassword(password);
        authenticationDTO.setUsername(login);
        String url = "http://localhost:" + port + "/users/login";
        jwtResponse = restTemplate.postForObject(url, authenticationDTO, JwtResponseDTO.class);
        assertNotNull(jwtResponse);
        logger.info("The jwt was extracted successfully");
    }

    private HttpHeaders getHeaders(String token) {
        String auth = "Authorization";
        HttpHeaders headers = new HttpHeaders();
        headers.set(auth, "Bearer " + token);
        return headers;
    }

    private TrainingCreateRequest trainingCreateDto(String trainerUsername) {
        TrainingCreateRequest trainingCreateRequest = new TrainingCreateRequest();
        Date trainingTime = new Date(124, Calendar.JANUARY, 5);
        trainingCreateRequest.setTrainerUsername(trainerUsername);
        trainingCreateRequest.setTrainingName("Group training");
        trainingCreateRequest.setTrainingDate(trainingTime);
        trainingCreateRequest.setDuration(90);
        return trainingCreateRequest;
    }

}

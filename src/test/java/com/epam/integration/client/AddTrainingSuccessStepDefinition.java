package com.epam.integration.client;

import com.epam.integration.dto.AuthenticationDTO;
import com.epam.integration.dto.JwtResponseDTO;
import com.epam.integration.dto.RegistrationResponse;
import com.epam.integration.dto.TraineeRegisterRequest;
import com.epam.integration.dto.TrainerRegisterRequest;
import com.epam.integration.dto.TrainingCreateRequest;
import com.epam.integration.dto.TrainingReportResponse;
import io.cucumber.java.en.And;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AddTrainingSuccessStepDefinition {

    @Autowired
    private RestTemplate restTemplate;
    private String port = "8080";
    private RegistrationResponse traineeRegistrationResponse;
    private RegistrationResponse trainerRegistrationResponse;
    private HttpEntity<TrainingCreateRequest> requestEntity;
    private ResponseEntity<String> responseEntity;
    private JwtResponseDTO jwtResponse;
    private Logger logger = LoggerFactory.getLogger(AddTrainingSuccessStepDefinition.class);


    @Given("a user wants to add a new training to the system and insert valid data")
    public void add_training_with_valid_data() {
        addTrainer();
        addTrainee();
        String traineeUsername = traineeRegistrationResponse.getLogin();
        String trainerUsername = trainerRegistrationResponse.getLogin();
        String trainerPassword = trainerRegistrationResponse.getPassword();
        getToken(trainerUsername, trainerPassword);
        String jwt = jwtResponse.getJwt();
        requestEntity = new HttpEntity<>(trainingCreateDto(traineeUsername, trainerUsername),
                getHeaders(jwt));
        logger.info("The request entity with TrainingCreateRequest is created successfully ");

    }

    @When("the system receives the post request and validates data then is saves new training to the system")
    public void receive_the_request_and_validate_and_saves_training_to_the_system() {
        String url = "http://localhost:" + port + "/trainings";
        responseEntity = restTemplate.exchange(url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        logger.info("The post request with the TrainingCreateRequest was sent successfully");
    }

    @Then("the operation was a success with status code {int} and the new training was added to the database")
    public void return_status_200_ok(int expected) {
        assertEquals(HttpStatus.valueOf(expected), responseEntity.getStatusCode());
        logger.info("The status code {} was received as a response", responseEntity.getStatusCode().value());
    }

    @And("the new training report request was sent to message broker and processed by training report service")
    public void check_the_training_report_processed_the_message() {
        ResponseEntity<TrainingReportResponse> trainingReportResponse = trainingReportResponse();
        assertNotNull(trainingReportResponse);
        TrainingReportResponse response = trainingReportResponse.getBody();
        assertNotNull(response);
        assertFalse(response.getYears().isEmpty());
    }

    private String urlWithParams(String url) {
        return UriComponentsBuilder.fromUriString(url)
                .queryParam("trainerUsername", trainerRegistrationResponse.getLogin())
                .toUriString();
    }

    private ResponseEntity<TrainingReportResponse> trainingReportResponse() {
        String servicePort = "8888";
        String url = "http://localhost:" + servicePort + "/api/training-reports";
        HttpEntity<?> httpEntity = HttpEntity.EMPTY;
        return restTemplate.exchange(urlWithParams(url),
                HttpMethod.GET,
                httpEntity,
                TrainingReportResponse.class);
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

    private void addTrainee() {
        TraineeRegisterRequest traineeRegisterRequest = new TraineeRegisterRequest();
        traineeRegisterRequest.setFirstName("William");
        traineeRegisterRequest.setLastName("Defoe");
        Date dateOfBirth = new Date(100, Calendar.JULY, 1);
        traineeRegisterRequest.setDateOfBirth(dateOfBirth);
        traineeRegisterRequest.setAddress("Privet Drive, 4");
        String url = "http://localhost:" + port + "/trainees";
        traineeRegistrationResponse = restTemplate.postForObject(url, traineeRegisterRequest, RegistrationResponse.class);
        assertNotNull(traineeRegistrationResponse);
        logger.info("The new trainee {} was added to the gym microservice", traineeRegistrationResponse.getLogin());

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

    private TrainingCreateRequest trainingCreateDto(String traineeUsername, String trainerUsername) {
        TrainingCreateRequest trainingCreateRequest = new TrainingCreateRequest();
        Date trainingTime = new Date(124, Calendar.JANUARY, 5);
        trainingCreateRequest.setTrainerUsername(trainerUsername);
        trainingCreateRequest.setTraineeUsername(traineeUsername);
        trainingCreateRequest.setTrainingName("Group training");
        trainingCreateRequest.setTrainingDate(trainingTime);
        trainingCreateRequest.setDuration(90);
        return trainingCreateRequest;
    }


}

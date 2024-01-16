package com.epam.integration.client;

import com.epam.integration.dto.AuthenticationDTO;
import com.epam.integration.dto.JwtResponseDTO;
import com.epam.integration.dto.RegistrationResponse;
import com.epam.integration.dto.TraineeRegisterRequest;
import com.epam.integration.dto.TrainerRegisterRequest;
import com.epam.integration.dto.TrainingCreateRequest;
import com.epam.integration.dto.TrainingReportResponse;
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

public class GetTrainingReportWithExistingUsernameStepDefinition {

    private String port = "8888";
    private String gymServicePort = "8080";

    private RegistrationResponse traineeRegistrationResponse;
    private RegistrationResponse trainerRegistrationResponse;
    private HttpEntity<TrainingCreateRequest> requestEntity;
    private ResponseEntity<String> gymServiceResponse;
    private JwtResponseDTO jwtResponse;

    @Autowired
    private RestTemplate restTemplate;
    private ResponseEntity<TrainingReportResponse> trainingReportResponse;
    private String urlWithParams;
    private Logger logger = LoggerFactory.getLogger(GetTrainingReportWithExistingUsernameStepDefinition.class);


    @Given("the user wants to fetch the training report from report service by username after it was added to the gym service")
    public void get_training_report_by_existing_username(){
        addNewTrainingToGymService();
        String url =  "http://localhost:" + port + "/api/training-reports";
        urlWithParams = UriComponentsBuilder.fromUriString(url)
                .queryParam("trainerUsername", trainerRegistrationResponse.getLogin())
                .toUriString();
        logger.info("The url with request parameter trainerUsername {} was formed", trainerRegistrationResponse.getLogin());
    }

    private void addNewTrainingToGymService(){
        addTrainer();
        addTrainee();
        String traineeUsername = traineeRegistrationResponse.getLogin();
        String trainerUsername = trainerRegistrationResponse.getLogin();
        String trainerPassword = trainerRegistrationResponse.getPassword();
        getToken(trainerUsername, trainerPassword);
        String jwt = jwtResponse.getJwt();
        requestEntity = new HttpEntity<>(trainingCreateDto(traineeUsername, trainerUsername),
                getHeaders(jwt));
        String url = "http://localhost:" + gymServicePort + "/trainings";
        gymServiceResponse = restTemplate.exchange(url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        assertEquals(HttpStatus.valueOf(200), gymServiceResponse.getStatusCode());

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

    private void addTrainer() {
        TrainerRegisterRequest trainerRegisterRequest = new TrainerRegisterRequest();
        trainerRegisterRequest.setFirstName("Lara");
        trainerRegisterRequest.setLastName("Black");
        trainerRegisterRequest.setSpecializationId(3L);
        String url = "http://localhost:" + gymServicePort + "/trainers";
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
        String url = "http://localhost:" + gymServicePort + "/trainees";
        traineeRegistrationResponse = restTemplate.postForObject(url, traineeRegisterRequest, RegistrationResponse.class);
        assertNotNull(traineeRegistrationResponse);
        logger.info("The new trainee {} was added to the gym microservice", traineeRegistrationResponse.getLogin());

    }

    private void getToken(String login, String password) {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO();
        authenticationDTO.setPassword(password);
        authenticationDTO.setUsername(login);
        String url = "http://localhost:" + gymServicePort + "/users/login";
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

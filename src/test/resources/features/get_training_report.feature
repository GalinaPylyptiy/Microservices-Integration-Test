Feature: Operation for fetching training report by trainer username with successful fetch and with empty data

  Scenario: The outcome with training report  fetched from the database
    Given the user wants to fetch the training report from report service by username after it was added to the gym service
    When the system receives the get request then the training report is fetched from the database
    Then the TrainingReport is received as a response with status 200

  Scenario: The outcome with empty training report returned as a response
    Given the user wants to fetch the training report from report service by username "Trainer.Username" and the report does not exist
    When the system receives the request and tries to fetch the report from the database
    Then the training report for the given username is not found, the empty Training Report is returned

  Scenario: The bad request status outcome
    Given the user wants to fetch the training report from report service but forget to pass a username as a param
    When the system receives the request and failed to validate the request
    Then the system throws an exception and the ResponseEntity with the status 400 bad request is returned

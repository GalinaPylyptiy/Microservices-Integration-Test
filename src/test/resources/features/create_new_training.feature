Feature: Operation for adding new training record with success outcome and with failure

  Scenario: Adding new training record with success outcome
    Given a user wants to add a new training to the system and insert valid data
    When the system receives the post request and validates data then is saves new training to the system
    Then the operation was a success with status code 200 and the new training was added to the database
    Then the new training report request was sent to message broker and processed by training report service

  Scenario: Adding new training record with failure due to the invalid data
    Given a user sends the post request to the specified url to add new training to the system but missed to insert trainee username
    When the system receives the request and validates data the exception is thrown due to bad request data
    Then the status 400 - Bad Request is returned to the user and the training was not added to the database
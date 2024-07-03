Feature: U1 - Register a new user

  Scenario: AC2 - Successful Registration
    Given There is no user with the email "testAccount@canterbury.ac.nz"
    When I register with the email "testAccount@canterbury.ac.nz" and have valid values for my details
    Then a new user is created into the system with the email "testAccount@canterbury.ac.nz"
Feature: U38c - Update Club

  Scenario: AC5 - Successful update on club
    Given I am editing my club's details
    When I submit the changes to my club's details
    Then I am brought back to my club's homepage with the changes visible

  Scenario: AC3 - Invalid name and description
    Given I am editing my club's details
    When I enter invalid values and submit the form
    Then I see error messages saying the fields contain invalid values
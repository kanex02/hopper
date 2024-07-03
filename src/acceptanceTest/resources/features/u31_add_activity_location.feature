Feature: U31 - Add activity location

  Scenario: AC1 Add activity location
    Given I am on the create activity form
    And I specify a valid activity and an address
    When I submit the form
    Then I do not receive any errors
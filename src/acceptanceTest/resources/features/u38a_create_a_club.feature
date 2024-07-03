Feature: U38a - Create a club

  Scenario: AC1 - Navigate to club creation page
    Given I am on the home page and I want to create a club
    When I navigate to the club creation page
    Then I am directed to the club creation form

  Scenario: AC1 - Successful club creation
    Given I am on the club creation form
    And I specify a valid name, description, teamIds, sport, city and country
    When I submit the club creation form
    Then The club is created
    And I am redirected to the club's homepage
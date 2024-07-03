Feature: U25 - Join Team
  Scenario: AC2 - Joining a team using an invitation token
    Given The user "fabian" wants to join a team
    And I have a valid team invitation token for a team
    When I input the invitation token that is associated with the team
    Then I am added as a member to the team

  Scenario: AC3 - Error message for invalid token
    Given The user "miguel" wants to join a team
    And I have an invalid team invitation token
    When I input the invitation token
    Then An error message tells me the token is invalid
Feature: U30 - As Julie, Ashley, I want to edit a scheduled activity so that  I can add more details or fix typos and mistakes in them.

  Scenario: AC3 - Edit Activity
    Given I am on the edit activity page as a valid user
    When I enter valid values and click the save activity button
    Then The activity is updated and I am shown the activitys page

  Scenario: AC3 - Edit Activity with Invalid ID
    Given I try to navigate to the edit activity page of a non-existent Activity
    When I should be taken to the page
    Then I am taken to an error page


  Scenario: AC3 - Edit Activity as Invalid User
    Given I try to navigate the edit activity page as an invalid user
    When I enter valid values and click the save activity button
    Then I am taken to an error page telling me I do not have permission to edit this activity


  Scenario: AC5 - Edit Activity with no team when activity type is "GAME"
    Given I am on the edit activity page as a valid user
    When I enter valid values but no team with activity type set to GAME and click the save activity button
    Then I am shown an error that I need a team for this activity

  Scenario: AC5 - Edit Activity with no team when activity type is "FRIENDLY"
    Given I am on the edit activity page as a valid user
    When I enter valid values but no team with activity type set to FRIENDLY and click the save activity button
    Then I am shown an error that I need a team for this activity


  Scenario: AC7 - Edit Activity with no description
    Given I am on the edit activity page as a valid user
    When I enter valid values but no description and click the save activity button
    Then I am shown an error that I need a description for this activity

  Scenario: AC10 - Edit Activity with invalid start time
    Given I am on the edit activity page as a valid user
    When I enter valid values with invalid start time and click the save activity button
    Then I am shown an error that I need a valid start time for this activity

  Scenario: AC9 - Edit Activity with invalid end time
    Given I am on the edit activity page as a valid user
    When I enter valid values with invalid end time and click the save activity button
    Then I am shown an error that I need a valid end time for this activity
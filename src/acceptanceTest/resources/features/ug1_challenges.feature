Feature: UG1 - As Teka, I want to be able to participate in challenges that encourage me to work towards my
  personal goals

  Scenario: AC3 - Complete Challenges
    Given I am viewing a currently available challenge
    When I click a button to mark the challenge as complete
    Then The challenge is marked as complete
    And I cannot complete it again

  Scenario: AC5 - Earning hops from challenges
    Given I am logged in and viewing a currently available challenge with ID 2
    When I try to complete a challenge without uploading media
    Then I am not rewarded with the hops for that challenge
    And The challenge is not marked as complete

    When I mark the challenge as completed with uploaded media
    Then I am rewarded with a small amount of hops (xp) for that challenge

    When I try to complete a challenge I have already completed
    Then I am not rewarded with the hops for that challenge




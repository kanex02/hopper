Feature: U120 - Share Challenge

  Scenario: AC4 - Pending Rewards
    Given I am logged in and viewing a currently available challenge with ID 3
    And The challenge is shared with another user
    When I mark the challenge as completed with uploaded media
    Then The challenge is marked as complete for me
    But The challenge is not marked as complete
    And I am not rewarded with the hops for that challenge

  Scenario: AC5 - Completion Rewards
    Given I am logged in and viewing a currently available challenge with ID 4
    And The challenge is shared with another user
    And The invited user has completed the challenge
    When I mark the challenge as completed with uploaded media
    Then I am rewarded with a small amount of hops (xp) for that challenge
    And The other user is also rewarded with the hops for that challenge
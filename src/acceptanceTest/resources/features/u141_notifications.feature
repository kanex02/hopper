Feature: U141 - Notifications: I want to receive notifications for activity involving my account

  Scenario: AC6 - Link challenge share notification to challenge
    Given I am logged in as a user who has received a challenge share notification
    When I click on the notification
    Then I am taken to the challenge page

#  TODO: See feature file

#  Scenario: AC3 - Share challenge notifications
#    Given I am logged in as a user
#    When another user shares a challenge with me
#    Then I can see the corresponding share challenge notification in my notifications
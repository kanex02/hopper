@ActivityStatistics
Feature: U34+5 - As Ashley, Julie, I want to record statistics about an activity so that I can keep track of what
  happened during that activity.

  Scenario: AC1 - Add score value to activity
    Given I have an activity
    When I submit a score with one integer for each team
    Then The score is saved

  Scenario: AC3 - Add substitution event to activity
    Given I have an activity
    When I want to record a substitution event with time 30
    Then The substitution is created successfully

  Scenario Outline: AC4 - Recording Facts for an Activity
    Given I have an activity
    When I add facts about the activity in the form of a fact <description> and an optional <time> when that fact happened
    Then The fact is saved
    Examples:
    | description         | time             |
    | "Player scored a goal"     | "2023-08-10T14:11:04.462288164" |
    | "Opponent received a red card" | "2023-08-10T15:11:04.462288164" |
    | "Referee made a controversial call" | "2023-08-10T16:11:04.462288164" |
    | "Team captain got injured"  |         ""          |
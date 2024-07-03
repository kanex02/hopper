Feature: U4 - Edit User Profile

  Scenario: AC2 - Successful Edit
    Given I am on the edit profile form
    And   I enter valid values for my first name, last name, email, and date of birth
    When  I hit the save changes button
    Then  The new details are saved


  Scenario Outline: AC3 - Unsuccessful Edit with Invalid Name
    Given I am on the edit profile form
    And   I enter <name> for my first name and last name
    When  I hit the save changes button
    Then  The error message tells me the first name and last name fields contain invalid values

    Examples:
    | name          |
    | ""            |
    | "test2"       |
    | "😀😀😀"     |
    | "  "          |
    | "a"           |
    | "John$"       |
    | "-John"       |
    | " John"       |
    | "John  Smith" |

  Scenario Outline: AC4 - Unsuccessful Edit with Invalid Email
    Given I am on the edit profile form
    And   I enter <email> for my email address
    When  I hit the save changes button
    Then  The error message tells me the email address is invalid

    Examples:
    | email                     |
    | ""                        |
    | "a@a"                     |
    | "   "                     |
    | "test@exam+ple.com"       |
    | "test@@example.com"       |
    | "  test@example.com"      |
    | "test..user@example.com"  |
    | "test@.com"               |
    | "@example.com"            |

  Scenario: AC5 - Unsuccessful Edit with Invalid Date of Birth
    Given I am on the edit profile form
    And   I enter a date of birth of someone younger than 13 years old
    When  I hit the save changes button
    Then  The error message tells me the date of birth is invalid

  Scenario: AC6 - Unsuccessful Edit with Existing Email
    Given I am on the edit profile form
    And   I enter an existing email address
    When  I hit the save changes button
    Then  The error message tells met he email address already exists




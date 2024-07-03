#Feature: As Julie, Ashley, Teka, I want to change my password so that I can make sure it is complex enough or different from a stolen password of mine.
#  Scenario: AC1 - Display change password form
#    Given I am on the edit profile form
#    When I hit the change password button
#    Then a dedicated form is shown with three text fields: "old password", "new password", and "retype password"
#
#  Scenario: AC2 - Incorrect old password
#    Given I am on the change password form
#    And I enter an old password that does not match the password in file
#    Then an error message tells me the old password is wrong
#
#  Scenario: AC3 - New and retyped passwords do not match
#    Given I am on the change password form
#    And I enter two different passwords in "new" and "retype password" fields
#    When I hit the save button
#    Then an error message tells me the passwords do not match
#
#  Scenario Outline: AC4 - Weak password
#    Given I am on the change password form
#    And I enter a <weak_password>
#    When I hit the save button
#    Then an error message tells me the password is too weak and provides me with the requirements for a strong password
#    Examples:
#    | weak_password |
#    | "sdasdsads"   |
#    | "111111111"   |
#    | "Ben123!"     |
#    | "1"           |
#
#  Scenario: AC5 - Successful password change
#    Given I am on the change password form
#    When I enter fully compliant details
#    Then my password is updated
#    And an email is sent to my email address to confirm that my password was updated

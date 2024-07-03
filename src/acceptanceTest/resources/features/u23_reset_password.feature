  @txn
  Feature: U23 - Reset Password

    Scenario: AC1 start resetting password
      Given I am on the login page and want to reset my password
      When I hit the forgot password button
      Then I see a form asking me for my email address

    Scenario: AC2 invalid email format
      Given I am on the lost password form
      When I submit an email with an invalid format
      Then an error message tells me the email address is invalid

    Scenario: AC3 valid email format not known to system
      Given I am on the lost password form
      When I enter a valid email that is not known to the system
      Then a confirmation message tells me that an email was sent to the address if it was recognised.

    Scenario: AC4 Taken to reset password page
      Given "Greg" received an email to reset my password
      When I go to the given URL passed in the link
      Then I am asked to supply a new password with new password and retype password fields

    Scenario: AC5 different passwords
      Given I am on the reset password form
      And I enter two different passwords in new and retype password fields
      When I hit the save button
      Then an error message tells me the passwords do not match.

    Scenario: AC6 New password too weak
      Given I am on the reset password form
      And I enter a weak password
      When I hit the save button
      Then then an error message tells me the password is too weak and provides me with the requirements for a strong password.

    Scenario: AC7 Valid passwords entered
      Given I am on the reset password form
      And I enter fully compliant details
      When I hit the save button
      Then my password is updated, and an email is sent to my email address to confirm that my password has been updated.

    Scenario: AC9 Token expiration
      Given A reset link was created
      When One hour has passed since the link was created
      Then The reset token is deleted
      And It cannot be used to reset a password anymore
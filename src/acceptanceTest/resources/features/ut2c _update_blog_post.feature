Feature: UT2c - Update Blog Post

  Scenario: Update Blog Post - Blue Sky
    Given I have a blog post with title "My Blog Post" and description "My Blog Post Description"
    And I am the author of the blog post
    And I am on the edit blog post page
    When I enter new information for the blog post
    And I click the Edit Post button
    Then that blog post is updated

  Scenario: Delete Blog Post - Confirm
    Given I have a blog post with title "My Blog Post" and description "My Blog Post Description"
    And I am the author of the blog post
    When I click the corresponding delete button for that blog post and confirm the deletion
    Then that blog post is deleted

  Scenario: Update Blog Post - Invalid input
    Given I have a blog post with title "My Blog Post" and description "My Blog Post Description"
    And I am the author of the blog post
    And I am on the edit blog post page
    When I enter invalid title and description for the blog post
    And I click the Edit Post button
    Then I see an error message for the invalid fields

  Scenario: Update Blog Post - Challenge Post
    Given I have a challenge blog post with title "My Blog Post" and description "My Blog Post Description"
    And I am the author of the challenge blog post
    When I am on the edit challenge blog post page
    Then I can change the post media
    And I click the Edit Post button
    And The post is updated
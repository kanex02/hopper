Feature: UT2b - Create Blog Post

  Scenario: Create Blog Post
    Given I am on the "Create Blog Post" page
    And I enter the title "My First Blog Post"
    And I link a challenge
    And I enter the description "This is my first blog post"
    And I set the visibility to "PUBLIC"
    And I upload a video
    When I submit the blog post
    Then The blog post is created successfully

  Scenario Outline: Create Blog Post - Invalid Title
    Given I am on the "Create Blog Post" page
    And I enter the title <title>
    And I link a challenge
    And I enter the description "This is my first blog post"
    And I set the visibility to "PUBLIC"
    And I upload a video
    When I submit the blog post
    Then The blog post is not created successfully
    And I see an error message for the title
    Examples:
      |   title  |
      |    ""    |
      |    " "   |
      |   " *"   |

  Scenario Outline: Create Blog Post - Invalid Description
    Given I am on the "Create Blog Post" page
    And I enter the title "My First Blog Post"
    And I link a challenge
    And I enter the description <description>
    And I set the visibility to "PUBLIC"
    And I upload a video
    When I submit the blog post
    Then The blog post is not created successfully
    And I see an error message for the description
    Examples:
      |   description   |
      |       ""        |
      |       " "       |
      |      " %"       |

  Scenario: Create Blog Post - Invalid Media
    Given I am on the "Create Blog Post" page
    And I enter the title "My First Blog Post"
    And I link a challenge
    And I enter the description "This is my first blog post"
    And I set the visibility to "PUBLIC"
    And I upload an invalid file
    When I submit the blog post
    Then The blog post is not created successfully
    And I see an error message for the media
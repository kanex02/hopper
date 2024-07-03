# Hopper - Team 1000

An app to connect people and teams, and promote friendly competition. 

Implemented using `gradle`, `Spring Boot`, `Thymeleaf`, and `GitLab CI`, with a touch of `Unity 3D`.

## How to build 

From the root directory

```
./gradlew build
```

To build the Java application into `/build/libs/`

### Building Unity

This project also makes use of Unity 3D for the interactive map. The WebGL build artifact is kept in this repo in the directory `src/main/resources/static/unity`. These files are not to be edited, they are build artifacts. Instead, the Unity project lies in [`/unity/Hopper/`](./unity/Hopper/). This is done because building the Unity project requires that the Unity Editor be installed, and this is not available on the Erskine Lab machines. 
To build the Unity Project from the command line, run the following gradle task:

```
./gradlew buildUnity
```

Currently, this build task is only set up to work on Windows. Furthermore, you should make sure that the PATH correctly includes the Unity Editor exe so that the command will work. 

Make sure that this command is run *before*, building the rest of the application, so that the unity build artifacts can be placed into the resources folder and included with the final build. 

The Unity Editor version being used is `2021.3.8f1`.

## How to run
### 1 - Running the project
From the root directory ...

On Linux:
```
./gradlew bootRun
```

On Windows:
```
gradlew bootRun
```

By default, the application will run on local port 8080 [http://localhost:8080](http://localhost:8080)

### 2 - Using the application

You will need to create a user on the registration page
before you can use much in the application. After creating
your user, you should be logged in automatically. You can 
then view a page that lists every user account and view 
their details.

The localhost instance will use an in memory H2 database.

## How to run tests

### Unit Tests
To execute JUnit unit tests, run the following command:

Linux:
```
./gradlew test
```

Windows:
```
gradlew test
```

### Cucumber Acceptance Tests

To execute the acceptance tests, run the following command
```
./gradlew acceptanceTest
```

### Playwright End to End Tests

Important note: Playwright is integrated into our project as required for the course, however our team has made the deliberate
decision to NOT use these tests, and so this does nothing.

To execute the Playwright End to End tests, run the following commands: 
```
./gradlew bootRun
```

Then once the application is started run the following command in a separate terminal: 
```
./gradlew end2end
```

## Contributors

- SENG302 teaching team
- Ben Meneghini
- Ben Williams
- Daniel Cha
- Michael Alpenfels
- James Billows
- Jacob Tinning
- Finn van Dorsser
- Kane Xie

## References

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Thymeleaf Docs](https://www.thymeleaf.org/documentation.html)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=17797&section=8)
- [Spring Security Docs](https://docs.spring.io/spring-security/reference/index.html)
- [JUnit Docs [Used in testing only]](https://junit.org/junit5/docs/current/user-guide/)
- [MultiSelect Dropdown](https://github.com/admirhodzic/multiselect-dropdown)
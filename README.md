# Citizen UI

## About

This is the UI for the Citizen facing application for Job Seekers Allowance.
This allows a user to complete a series of questions about their eligibility to apply for JSA and gathers information about their circumstances.


## Getting Started

### Prerequisites

* Java 8
* Maven
* Redis (you can run in mock redis mode - see below)

### Installing

mvn install

`java -jar target/citizen-ui-0.0.1-SNAPSHOT.jar`

Browse to: http://localhost:8080/

### Running with a mock redis server
Use the spring.profiles.active flag to choose the mockredis profile
This will start a mock redis server for the application to run against

e.g.
`java -jar -Dspring.profiles.active=dev,mockredis target/citizen-ui-0.0.1-SNAPSHOT.jar`

## Running the tests
mvn -Dspring.profiles.active=test,mockredis test

`-Dspring.profiles.active=test,mockredis` ensures that the test environment is set and we are using mockredis.
Running the test without `mockredis` will use a local redis installation on port `6379`

## Running Pa11y
We use Pa11y from the command line. To be able to run it you should install NPM (comes with Node - download link: https://nodejs.org/en/download/), and then install the pa11y npm package using NPM:

    npm install -g pa11y

Once the command line tool is installed, you can run pa11y to check the site against A, AA and AAA accessibility standards.

In the command line go to the project root and run:

    pa11y http://localhost:8080

pa11y has a configuration file in the project root

## PublicKey

In application.properties, the services.publicKey needs to be populated with a good RSA key.
To create this, and set it, run ./createPublicKey.sh.  This is a one time operation.  Please take
care not to check this change in.

# Dependencies

This service requires nsjsa-commons to build.

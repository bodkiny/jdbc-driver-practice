# JDBC Driver Practice

This is a Java project that I used for consolidating acquired knowledge of working with JDBC to interact with a SQL database. The project uses Maven for dependency management and build automation.

## Project Structure

The project is organized into several packages:

- `com.example.entity`: Contains the `Customer` class, which represents a customer in the database.
- `com.example.dao`: Contains the `CustomerDao` interface and its implementation `CustomerSQLDao`. These classes are responsible for performing database operations related to customers.
- `com.example.connection`: Contains the `ConnectionProvider` interface and its implementation `SQLConnectionProvider`. These classes are responsible for providing a connection to the database.
- `com.example.exception`: Contains the `SQLRuntimeException` class, which is a custom unchecked exception used in the project.

## Database

The project uses an H2 database for testing. The database schema and initial data are defined in the `customers_initialization.sql` file.

## Testing

The project includes unit tests for the `CustomerSQLDao` class. The tests use JUnit 5 and Mockito.

## Code Coverage

The project is configured to use the JaCoCo Maven plugin to generate code coverage reports. The configuration for the plugin is specified in the `pom.xml` file.

## Building and Running

The project can be built and packaged into a JAR file using Maven. The `pom.xml` file specifies the project dependencies and build configuration.
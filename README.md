# Enterprise Credentials Manager (ECM)

Enterprise Credentials Manager is a simple tool to securely store credentials encrypted in a database and retrieve them through a web UI or a REST API (API implementation in progress).

This project aims to provide a minimal, self-hosted credentials vault for teams and enterprise applications, with conventional Spring Boot patterns, Flyway migrations, and LDAP-based authentication.

## Features
- Secure storage of credentials with encryption at rest
- Web UI built with Spring MVC and Thymeleaf
- LDAP-based authentication (configurable)
- Database migrations with Flyway
- Actuator endpoints for basic health and info
- OpenAPI/Swagger UI wiring present (REST endpoints are in progress)

## Tech stack
- Java 17
- Spring Boot 3.5.x (Web, Security, Data JPA, Actuator)
- Thymeleaf + Layout dialect
- Flyway for database migrations
- MySQL (default runtime driver), but any JPA-compatible DB can be wired
- springdoc-openapi for API documentation

## Getting started

### Prerequisites
- Java 17 (JDK)
- Maven 3.9+
- A running database (default examples assume MySQL)

### Configuration
Application configuration lives in `src/main/resources/application.properties`. At minimum, set your database and (optionally) LDAP properties before running.

Typical properties to review:
- Spring datasource (URL, username, password, driver)
- JPA dialect and DDL settings
- Flyway (enabled by default)
- Security / LDAP settings (server URL/base DN, user DN, groups, etc.)

Note: Encryption configuration (key management, algorithm selection) is part of the application internals. Ensure your deployment provides the necessary secrets/keys via environment variables or externalized configuration as appropriate for your environment.

### Build
```
mvn clean package
```
This produces a runnable JAR under `target/`.

### Run (development)
```
mvn spring-boot:run
```

### Run (packaged JAR)
```
java -jar target/ecm-0.0.1-SNAPSHOT.jar
```

### Database migrations
Flyway will run migrations at startup. Ensure your database is reachable and the configured user has appropriate privileges.

### API and Swagger UI
The project includes `springdoc-openapi`. Once REST endpoints are available, Swagger UI is typically accessible at:
- `/swagger-ui/index.html`

Note: The REST API is currently a work in progress.

## Development
- Standard Spring Boot project structure
- Unit and integration tests can be run with:
```
mvn test
```
- Hot reload can be used via Spring Boot DevTools (enabled as runtime optional dependency)

## Security
- Authentication via Spring Security with LDAP support
- Follow your organization’s policies for credential encryption keys and secret management
- Always restrict access to the application and its database to trusted networks and users

## Status
- Web UI: usable
- REST API: in progress

## License
This project is licensed under the GNU General Public License v3.0 (GPL-3.0). See the `LICENSE` file for details.

## Disclaimer
This project is provided "as is" without warranty. Evaluate security, compliance, and operational fit before deploying in production.

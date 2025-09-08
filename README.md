# Enterprise Credentials Manager (ECM) 🔐

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=tiglate_ecm&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=tiglate_ecm)

Enterprise Credentials Manager is a self-hosted tool to store credentials encrypted in a database and retrieve them via a web UI or a REST API.

The project follows conventional Spring Boot patterns with Flyway migrations and LDAP-based authentication. It aims to stay minimal and straightforward.

## Features
- Encryption at rest using AES-GCM; supports PBKDF2-derived keys or raw AES keys with AAD binding
- Web UI built with Spring MVC and Thymeleaf
- LDAP-based authentication (configurable)
- REST API v1 for credential retrieval under `/api/v1` with API key authentication
- API key management with per-client ID and optional host restriction
- Database migrations with Flyway
- Actuator endpoints for basic health and info
- OpenAPI/Swagger UI wiring

## Tech stack ⚙️
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
- Crypto-related settings (secrets/keys provided via environment or external config)

Note: Encryption uses AES/GCM. Keys can be derived from a passphrase using PBKDF2 or provided as a raw AES key. Additional Authenticated Data (AAD) is used to bind ciphertext to deployment-specific context.

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
Flyway runs migrations at startup. Ensure your database is reachable and the configured user has appropriate privileges.

## REST API v1 📡
The REST API is available under `/api/v1`. API requests are protected by an API key filter.

Authentication headers (or query params):
- `X-API-CLIENT-ID` (or `clientId`)
- `X-API-KEY` (or `apiKey`)

Endpoint: Retrieve a password
- Method: GET
- Path: `/api/v1/credential`
- Query parameters:
  - `appCode` (required)
  - `environment` (required; e.g., DEV, QA, UAT, PROD)
  - `credentialType` (required; e.g., DATABASE, API_KEY, WINDOWS, LINUX)
  - `username` (required)

Example request:
```
curl -G \
  -H "X-API-CLIENT-ID: demo-client" \
  -H "X-API-KEY: s3cr3t" \
  --data-urlencode "appCode=ERP" \
  --data-urlencode "environment=DEV" \
  --data-urlencode "credentialType=DATABASE" \
  --data-urlencode "username=john" \
  http://localhost:8080/api/v1/credential
```

Example success response (200):
```
{ "password": "s3cr3t!" }
```

Error responses:
- 400: validation errors (missing/invalid parameters)
- 401: missing or invalid API key / client ID
- 403: API key not allowed from the calling host
- 404: credential not found
- 500: generic server error

### Swagger UI
If enabled, Swagger UI is typically available at `/swagger-ui/index.html`. Note that API key authentication applies to `/api/**` requests.

## Development 🛠️
- Standard Spring Boot project structure
- Run tests:
```
mvn test
```
- Optional hot reload via Spring Boot DevTools

## Security notes
- Authentication via Spring Security with LDAP support (for the web UI)
- API key authentication for REST API calls under `/api/**`
- Follow your organization’s policies for key and secret management
- Restrict access to the application and its database to trusted networks and users

## What’s new ✨
- Added API key–based authentication filter for `/api/**` using `X-API-CLIENT-ID` and `X-API-KEY`
- Implemented AES-GCM crypto service with PBKDF2 key derivation or raw key mode, with AAD support
- Introduced REST endpoint `/api/v1/credential` with validation and consistent JSON error responses
- Expanded unit tests for crypto, password service, API key flow, and REST controller behavior

## Status
- Web UI: usable
- REST API: available for credential retrieval

## License
This project is licensed under the GNU General Public License v3.0 (GPL-3.0). See the `LICENSE` file for details.

## Disclaimer
This project is provided "as is" without warranty. Evaluate security, compliance, and operational fit before deploying in production.

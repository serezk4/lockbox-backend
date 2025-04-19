![Image alt](https://github.com/LockBox-for-business/frontend/blob/dev/lockbox-frontend/public/logo_full.svg)
# Backend-module

## Description



## Launch Guide

### Step #0: Ensure Docker is Running
Make sure Docker is installed and running on your system.

### Step #1: Start Services
Run the following command to start all required services:
```bash
sh start.sh
```

---

## Project Structure

```plaintext
.
├── README.md
├── api
│   ├── Makefile
│   ├── avatar
│   ├── box
│   ├── config
│   ├── flat
│   ├── gateway
│   └── user
├── doc
│   └── file-api.md
├── env
│   ├── dev
│   └── prod
├── prepare.sh
├── service
│   ├── Makefile
│   ├── elsticsearch
│   ├── grafana
│   ├── keycloak
│   ├── kibana
│   ├── logstash
│   ├── minio
│   ├── postgres
│   └── prometheus
├── start.sh
└── tests
    ├── endpoint
    ├── load
    ├── requirements.txt
    ├── test_results.json
    └── test_results_protected.json

24 directories, 9 files
```

### Module Links
- **API Modules**:
  - [Box](./api/box)
  - [Flat](./api/flat)
  - [User](./api/user)
  - [Avatar](./api/avatar)
  - [Gateway](./api/gateway)
- **Environment Configurations**:
  - [Development](./env/dev)
  - [Production](./env/prod)
- **Service Modules**:
  - [Keycloak](./service/keycloak)
  - [MinIO](./service/minio)
  - [PostgreSQL](./service/postgres)
  - [Elk](./service/elsticsearch)
  - [Kibana](./service/kibana)
  - [logstash](./service/logstash)
  - [grafana](./service/grafana)
  - [prometheus](./service/prometheus)

---

## Dependencies


### Core Dependencies
| Dependency                        | Version    | Link to Maven Repo                               | Official Site                    | Comment                                              |
|------------------------------------|------------|-------------------------------------------------|----------------------------------|------------------------------------------------------|
| Spring Boot                       | 3.3.2      | [Maven](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter) | [Spring](https://spring.io/)    | Core framework for building Spring applications.    |
| Spring Cloud Gateway              | 4.1.4      | [Maven](https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-gateway) | [Spring](https://spring.io/)    | Used for building API gateways.                     |
| Spring WebFlux                    | 3.3.2      | [Maven](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-webflux) | [Spring](https://spring.io/)    | Reactive programming support for the web layer.     |
| Spring Security                   | 6.3.3      | [Maven](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security) | [Spring](https://spring.io/)    | Security for OAuth2 and JWT.                        |
| PostgreSQL Driver                 | 42.5.4     | [Maven](https://mvnrepository.com/artifact/org.postgresql/postgresql) | [PostgreSQL](https://www.postgresql.org/) | Database connectivity.                              |
| Flyway                            | 10.15.0    | [Maven](https://mvnrepository.com/artifact/org.flywaydb/flyway-core) | [Flyway](https://flywaydb.org/) | Database migrations.                                 |
| Keycloak Admin Client             | 26.0.0     | [Maven](https://mvnrepository.com/artifact/org.keycloak/keycloak-admin-client) | [Keycloak](https://www.keycloak.org/) | Keycloak integration for user management.           |
| Jackson Databind                  | 2.15.2     | [Maven](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind) | [Jackson](https://github.com/FasterXML/jackson) | JSON serialization/deserialization.                |
| Jackson Core                      | 2.15.2     | [Maven](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core) | [Jackson](https://github.com/FasterXML/jackson) | Core functionality for Jackson library.            |
| Jackson Annotations               | 2.15.2     | [Maven](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations) | [Jackson](https://github.com/FasterXML/jackson) | Annotation support for Jackson.                    |
| Lombok                            | 1.18.28    | [Maven](https://mvnrepository.com/artifact/org.projectlombok/lombok) | [Lombok](https://projectlombok.org/) | Boilerplate code reduction.                         |
| MapStruct                         | 1.5.5.Final| [Maven](https://mvnrepository.com/artifact/org.mapstruct/mapstruct) | [MapStruct](https://mapstruct.org/) | Object mapping framework.                           |

### Documentation Dependencies
| Dependency                        | Version    | Link to Maven Repo                               | Official Site                    | Comment                                              |
|------------------------------------|------------|-------------------------------------------------|----------------------------------|------------------------------------------------------|
| Springdoc OpenAPI WebFlux UI      | 2.5.0      | [Maven](https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-starter-webflux-ui) | [Springdoc](https://springdoc.org/) | Swagger/OpenAPI integration.                        |

### Test Dependencies
| Dependency                        | Version    | Link to Maven Repo                               | Official Site                    | Comment                                              |
|------------------------------------|------------|-------------------------------------------------|----------------------------------|------------------------------------------------------|
| Testcontainers                    | 1.20.4     | [Maven](https://mvnrepository.com/artifact/org.testcontainers/testcontainers) | [Testcontainers](https://www.testcontainers.org/) | Containerized tests.                                |
| Spring Boot Starter Test          | 3.3.2      | [Maven](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test) | [Spring](https://spring.io/)    | Core testing utilities.                             |
| Reactor Test                      | 3.5.4      | [Maven](https://mvnrepository.com/artifact/io.projectreactor/reactor-test) | [Reactor](https://projectreactor.io/) | Reactive stream testing utilities.                  |
| Spring Security Test              | 6.3.3      | [Maven](https://mvnrepository.com/artifact/org.springframework.security/spring-security-test) | [Spring](https://spring.io/)    | Security testing utilities.                         |

---


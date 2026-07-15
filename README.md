# 🔐 FileVault — Secure File Upload & Metadata API

A production-ready **secure file storage API** built with **Spring Boot 4.1**. FileVault enables authenticated users to upload, download, list, and soft-delete files with robust multi-layer validation, AOP-based audit logging, and scheduled orphan file cleanup.

---

## ✨ Features

- **JWT Authentication** — Stateless Bearer token auth with JJWT 0.12.6
- **Role-Based Access Control** — `USER` and `ADMIN` roles via Spring Security
- **Multi-layer File Validation** — Filename sanitization, extension, MIME type, file size, and **magic-byte (Apache Tika)** signature checking
- **Soft Delete** — Files are logically deleted via Hibernate `@SQLDelete`; physical cleanup is done by a scheduled background task
- **Orphan File Cleanup** — Scheduled service removes physical files that lack corresponding DB records
- **Download Audit Log** — Every file download is logged via Spring AOP (`@DownloadAudited` annotation)
- **API Versioning** — Header-based versioning (`X-VERSION-API`) via Spring MVC 4.1
- **Compromised Password Detection** — Integration with HaveIBeenPwned API on registration
- **Structured Error Responses** — Unified `BaseResponse` wrapper with typed error codes
- **OpenAPI / Swagger UI** — Auto-generated interactive docs at `/swagger-ui.html`
- **Flyway Migrations** — Database schema managed via versioned SQL scripts
- **Multi-profile Configuration** — `local` (H2), `dev` (PostgreSQL), `prod` (PostgreSQL + secrets)
- **Docker & Docker Compose** — Containerized deployment with Docker secrets support

---

## 🛠 Tech Stack

| Category           | Technology                               |
|--------------------|------------------------------------------|
| Language           | Java 17                                  |
| Framework          | Spring Boot 4.1                          |
| Security           | Spring Security 6, JJWT 0.12.6          |
| Persistence        | Spring Data JPA, Hibernate, Flyway       |
| Database           | PostgreSQL (prod/dev), H2 (local)        |
| File Detection     | Apache Tika 3.3.1                        |
| Mapping            | MapStruct 1.6.3                          |
| API Docs           | SpringDoc OpenAPI 2.8.8 (Swagger UI)     |
| AOP                | Spring AOP (AspectJ)                     |
| Build              | Maven 3.9+                               |
| Containerization   | Docker, Docker Compose                   |
| Code Generation    | Lombok                                   |

---

## 📁 Project Structure

```
src/main/java/com/codems/filevault/
├── FileVaultApplication.java
├── common/
│   ├── audit/             # ApplicationLoggingAspect
│   ├── config/            # WebConfig, OpenApiConfig, @ConfigurationProperties
│   ├── constants/         # ApplicationConstants
│   ├── exceptions/        # GlobalExceptionHandler, BaseException, ErrorType hierarchy
│   ├── security/          # JWT filter, security configs (local/dev/prod), SecurityPaths
│   ├── util/              # Shared utilities
│   └── validation/        # @UniqueEmail, @CompromisedPassword validators
└── domain/
    ├── auth/              # AuthController, AuthService, DTOs
    ├── base/              # BaseEntity, BaseResponse, PageResponse
    ├── file/              # FileController, FileService, validators, audit, storage
    └── user/              # User entity, UserRepository, UserMapper
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- Docker & Docker Compose (for `dev` / `prod` profiles)

---

### 1. Local Profile (H2 — no Docker needed)

```bash
# Clone the repository
git clone https://github.com/Sarvar55/FileVault.git
cd FileVault

# Run with local profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

- App: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

> **Note:** In `local` profile the login endpoint accepts **any password** for existing users.
> Read the section below to understand why.

#### 🧩 How `LocalAuthenticationProvider` works

In `local` mode, the standard `DaoAuthenticationProvider` (which verifies BCrypt passwords) is
replaced by a custom `LocalAuthenticationProvider`. This provider:

- Looks up the user by **email only**
- Checks that the account is **enabled** and **not locked**
- **Skips password verification entirely** and marks the authentication as successful

**Why?** The `local` profile uses an in-memory H2 database. Every application restart drops and
recreates the schema, so there are no pre-seeded users with known BCrypt hashes. Typing the correct
hash on every restart would be painful. `LocalAuthenticationProvider` lets you log in with any
password so you can test API flows immediately after registering a user — no tooling needed.

> ⚠️ `LocalAuthenticationProvider` is **only active when `SPRING_PROFILES_ACTIVE=local`**.
> It is never compiled into `dev` or `prod` builds because the `@Profile("local")` annotation
> prevents Spring from registering it in any other environment.

---

### 2. Dev Profile (PostgreSQL via Docker)

```bash
# Copy environment file
cp .env.example .env

# Create the secrets directory and files
mkdir -p secrets
echo "your_db_password" > secrets/DB_PASSWORD
echo "your-jwt-secret-at-least-32-chars!!" > secrets/JWT_SECRET

# Start PostgreSQL + App
SPRING_PROFILES_ACTIVE=dev docker compose up --build
```

---

### 3. Production Profile

```bash
cp .env.prod.example .env.prod

# Fill in production values in .env.prod, then:
docker compose -f docker-compose-prod.yml --env-file .env.prod up -d
```

---

## 📡 API Reference

Base path: `/api` — All endpoints are versioned with header `X-VERSION-API: 1.0`

### Authentication

| Method | Endpoint               | Auth     | Description                      |
|--------|------------------------|----------|----------------------------------|
| POST   | `/api/auth/register`   | Public   | Register a new user account      |
| POST   | `/api/auth/login`      | Public   | Login and receive a JWT token    |

### Files

| Method | Endpoint                    | Auth     | Description                                      |
|--------|-----------------------------|----------|--------------------------------------------------|
| POST   | `/api/files`                | USER     | Upload a file (multipart/form-data)              |
| GET    | `/api/files`                | USER     | List own files (paginated)                       |
| GET    | `/api/files/{id}/download`  | USER     | Download a file (audited)                        |
| DELETE | `/api/files/{id}`           | USER     | Soft-delete a file                               |

**Supported file types:** `jpg`, `jpeg`, `png`, `pdf`, `docx`

**Max upload size:** `10 MB`

---

## 🔒 Security Model

- All API routes are protected by JWT Bearer authentication
- Passwords are BCrypt-encoded via Spring's `DelegatingPasswordEncoder`
- **Compromised password detection** via [HaveIBeenPwned API](https://haveibeenpwned.com/Passwords) on registration
- JWT signing uses HMAC-SHA256
- Path traversal attacks are blocked in filename validation
- Magic-byte (file signature) validation prevents MIME-type spoofing

---

## ⚙️ Configuration Reference

| Property                       | Default (dev)               | Description                         |
|--------------------------------|-----------------------------|-------------------------------------|
| `app.jwt.secret`               | *(required)*                | JWT signing key (min 32 chars)      |
| `app.jwt.expiration`           | `3600000` (1 hour)          | Token expiration in milliseconds    |
| `file.storage.root-path`       | `./storage/dev`             | Root directory for stored files     |
| `file.upload.max-size`         | `10MB`                      | Maximum file upload size            |
| `file.cleanup.enabled`         | `true`                      | Enable/disable orphan file cleanup  |
| `file.cleanup.fixed-delay`     | `PT1H`                      | Cleanup job interval (ISO 8601)     |
| `file.cleanup.orphan-grace-period` | `PT24H`                | Grace period before orphan deletion |

---

## 🌍 Environment Files

The repository ships with ready-to-use example files. Copy the one that matches your target
environment and fill in the placeholders.

| File | Purpose | Copy to |
|------|---------|----------|
| `.env.example` | Docker Compose base (non-secret vars) | `.env` |
| `.env.dev.example` | Dev profile — PostgreSQL via Docker | `.env.dev` |
| `.env.prod.example` | Production — full variable reference | `.env.prod` |

> ⚠️ Files ending in `.env` and `.env.*` (except `*.example`) are ignored by Git.
> **Never commit real credentials.**

### Secrets (prod / dev profiles)

The `dev` and `prod` Docker Compose stacks read sensitive values from the `./secrets/` directory
using [Docker secrets](https://docs.docker.com/compose/how-tos/use-secrets/), not plain env vars.
Create the files before starting the stack:

```bash
mkdir -p secrets
printf 'your_db_password'                            > secrets/DB_PASSWORD
printf 'your-jwt-secret-at-least-32-characters!!'   > secrets/JWT_SECRET
```

> The `secrets/` directory is Git-ignored. Only `secrets/*.example` placeholder files are tracked.

---

## 🧪 Running Tests

```bash
./mvnw test
```

Tests include:
- `FileValidationServiceTests` — multi-layer upload validation (unit)
- `OrphanFileCleanupServiceTests` — cleanup logic with Mockito (unit)

---

## 🐳 Docker

```bash
# Build the image
docker build -t filevault .

# Run with Docker Compose (dev)
docker compose up --build
```

The Dockerfile uses a **multi-stage build**:
1. **Build stage**: `maven:3.9.9-eclipse-temurin-17` — compiles the app
2. **Runtime stage**: `gcr.io/distroless/java17-debian12:nonroot` — minimal, rootless image

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

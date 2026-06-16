# NayePankh VIMS — Volunteer Information Management System

REST API backend for **NayePankh Foundation** — a registered Indian NGO running food drives, clothing distribution, health-awareness campaigns, and education initiatives.

This backend is the **single source of truth** consumed by:
- 📱 **Android app** (Kotlin, Jetpack Compose, Retrofit) — volunteer-facing
- 🖥️ **Java Swing desktop app** — staff/admin-facing

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 (LTS) |
| Framework | Spring Boot 3.4.5 |
| Build | Maven (`./mvnw`) |
| ORM | Spring Data JPA + Hibernate |
| Validation | Jakarta Bean Validation |
| Database (dev) | H2 in-memory |
| Database (prod) | PostgreSQL (Supabase) |
| API Docs | springdoc-openapi (Swagger UI) |

---

## Quick Start (Local Development)

### Prerequisites
- Java 21+
- Git

### Run
```bash
# Clone the repository
git clone <repo-url>
cd Backend

# Run with dev profile (H2 in-memory DB, seed data auto-loaded)
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080` with the `dev` profile by default.

### Key URLs (dev)
| URL | Description |
|---|---|
| `http://localhost:8080/swagger-ui.html` | Swagger UI — interactive API docs |
| `http://localhost:8080/h2-console` | H2 Database console (JDBC URL: `jdbc:h2:mem:vimsdb`) |
| `http://localhost:8080/api/v1/volunteers` | Volunteers API |
| `http://localhost:8080/api/v1/campaigns` | Campaigns API |
| `http://localhost:8080/api/v1/enrollments` | Enrollments API |
| `http://localhost:8080/api/v1/dashboard/summary` | Dashboard summary |

### Seed Data
On the `dev` profile, the app auto-seeds:
- 5 volunteers (various cities, skills, statuses)
- 5 campaigns (all types, mix of UPCOMING/ACTIVE/COMPLETED)
- 11 enrollments (REGISTERED, ATTENDED with hours, NO_SHOW, CANCELLED)

---

## Spring Profiles

| Profile | DB | DDL | SQL Logging | H2 Console |
|---|---|---|---|---|
| `dev` (default) | H2 in-memory | `create-drop` | ✅ | ✅ |
| `prod` | PostgreSQL (Supabase) | `update` | ❌ | ❌ |

---

## API Contract

**Base path:** `/api/v1`

All list endpoints support **pagination + sorting** via Spring Data `Pageable`:
```
?page=0&size=20&sort=name,asc
```

### Volunteers
| Method | Path | Description |
|---|---|---|
| `POST` | `/volunteers` | Register a new volunteer |
| `GET` | `/volunteers` | List volunteers (filters: `city`, `skill`, `status`) |
| `GET` | `/volunteers/{id}` | Get volunteer by ID |
| `PUT` | `/volunteers/{id}` | Update volunteer details |
| `PATCH` | `/volunteers/{id}/status` | Activate/deactivate a volunteer |
| `GET` | `/volunteers/{id}/campaigns` | List volunteer's enrollments |
| `GET` | `/volunteers/{id}/summary` | Get volunteer stats (hours, campaigns) |

### Campaigns
| Method | Path | Description |
|---|---|---|
| `POST` | `/campaigns` | Create a new campaign |
| `GET` | `/campaigns` | List campaigns (filters: `status`, `type`, `upcoming`) |
| `GET` | `/campaigns/{id}` | Get campaign details + enrollment counts |
| `PUT` | `/campaigns/{id}` | Update campaign details |
| `PATCH` | `/campaigns/{id}/status` | Update campaign status |
| `GET` | `/campaigns/{id}/volunteers` | List enrolled volunteers |
| `GET` | `/campaigns/{id}/report` | Get campaign report (attendance, hours) |

### Enrollments
| Method | Path | Description |
|---|---|---|
| `POST` | `/enrollments` | Enroll a volunteer in a campaign |
| `PATCH` | `/enrollments/{id}/attendance` | Update attendance + hours |
| `DELETE` | `/enrollments/{id}` | Cancel enrollment (soft-delete) |

### Dashboard
| Method | Path | Description |
|---|---|---|
| `GET` | `/dashboard/summary` | Aggregate dashboard stats |

### Error Response Format
All errors follow a consistent shape:
```json
{
  "timestamp": "2026-06-16T06:00:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Campaign is at capacity",
  "path": "/api/v1/enrollments"
}
```
Validation errors (400) include a `fieldErrors` map:
```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/volunteers",
  "fieldErrors": {
    "email": "Must be a valid email address",
    "name": "Name is required"
  }
}
```

---

## Business Rules

1. **Enrollment capacity** — 409 Conflict if campaign is at capacity (REGISTERED + ATTENDED ≥ capacity)
2. **Duplicate enrollment** — 409 Conflict if non-cancelled enrollment exists for the same (volunteer, campaign)
3. **Enrollment eligibility** — 400 if campaign is not UPCOMING/ACTIVE or volunteer is INACTIVE
4. **Soft delete** — Volunteers are deactivated (INACTIVE), never hard-deleted
5. **Hours logging** — `hoursLogged` can only be set when enrollment status is ATTENDED
6. **Campaign date** — `eventDate` must be today or future at creation time

---

## Deployment — Render (Docker) + Supabase

The app is deployed to Render as a **Docker** web service. The multi-stage
`Dockerfile` builds the jar with the Maven wrapper (Java 21 JDK) and runs it on a
slim JRE image — no native Java buildpack required.

### Render Web Service Configuration

| Setting | Value |
|---|---|
| Runtime | `Docker` (auto-detected from `Dockerfile`) |
| Branch | `master` |
| Build / Start Command | *(leave blank — the `Dockerfile` handles both)* |
| Environment | `SPRING_PROFILES_ACTIVE=prod` |

> The container exposes the app via Spring's `${PORT:8080}` (`application.yml`),
> so Render's injected `PORT` is used automatically — do **not** set `PORT` manually.

### Build & run the container locally

```bash
# Build the image
docker build -t nayepankh-vims .

# Run it (dev profile, H2 in-memory)
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev nayepankh-vims
```

### Required Environment Variables

| Variable | Description | Example |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Spring profile | `prod` |
| `SPRING_DATASOURCE_URL` | JDBC connection string | `jdbc:postgresql://aws-0-<region>.pooler.supabase.com:5432/postgres?sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | DB username | `postgres.<project-ref>` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `<your-db-password>` |
| `PORT` | Server port (auto-set by Render) | `10000` |
| `APP_CORS_ALLOWED_ORIGINS` | Allowed CORS origins (optional) | `https://your-app.com` |

### ⚠️ Supabase Connection — IPv4/IPv6 Gotcha

Supabase's **direct** connection string (`db.<ref>.supabase.co:5432`) is **IPv6-only** on free projects. Render's free tier is **IPv4-only**, so direct connections will **silently fail**.

**Solution:** Use the Supabase **Connection Pooler (Supavisor)** — it provides an IPv4 endpoint:

```
jdbc:postgresql://aws-0-<region>.pooler.supabase.com:5432/postgres?sslmode=require
```

Key points:
- ✅ Use **session mode (port 5432)** for JPA/Hibernate
- ✅ Username includes the project ref: `postgres.<project-ref>` (not plain `postgres`)
- ✅ Append `?sslmode=require`
- ⚠️ If using **transaction mode (port 6543)**, disable prepared-statement caching — session mode is simpler

### Cold Start Note
Render's free tier spins down the service after inactivity. The first request after idle will have a cold-start delay (~30-60s). Clients should handle this with appropriate timeouts.

---

## Project Structure

```
Dockerfile                        # Multi-stage build (JDK build → JRE runtime)
.dockerignore                     # Excludes target/, .git, IDE files from build context
src/main/java/com/nayepankh/vims/
├── VimsApplication.java          # Entry point
├── config/
│   ├── CorsConfig.java           # CORS policy (env-configurable)
│   ├── DataSeeder.java           # Dev seed data
│   └── OpenApiConfig.java        # Swagger metadata
├── controller/
│   ├── CampaignController.java   # /api/v1/campaigns
│   ├── DashboardController.java  # /api/v1/dashboard
│   ├── EnrollmentController.java # /api/v1/enrollments
│   └── VolunteerController.java  # /api/v1/volunteers
├── dto/
│   ├── request/                  # Incoming DTOs with validation
│   └── response/                 # Outgoing DTOs
├── entity/                       # JPA entities + enums
├── exception/                    # Custom exceptions + global handler
├── mapper/                       # Entity ↔ DTO mapping
├── repository/                   # Spring Data JPA interfaces
└── service/                      # Business logic
```

---


# NayePankh VIMS — Backend

This is the backend for **NayePankh Foundation**, an Indian NGO that runs food
drives, clothing distribution, health-awareness campaigns, and education programs.

It's a REST API that keeps track of **volunteers**, **campaigns**, and who
**signed up** for what. Two apps talk to it:

- 📱 an **Android app** for volunteers
- 🖥️ a **Java Swing desktop app** for staff (see the `Java/` project)

**Live API:** https://nayepankh-vims-backend.onrender.com

---

## What's under the hood

Java 21 · Spring Boot 3.4.5 · Spring Data JPA · H2 (local) / PostgreSQL (live) ·
Swagger for docs. Built with Maven.

---

## Run it locally

You just need **Java 21**. Then:

```bash
./mvnw spring-boot:run
```

That's it — it starts on `http://localhost:8080` using an in-memory H2 database
that's wiped and re-seeded every restart (5 volunteers, 5 campaigns, a handful of
sign-ups), so you always have something to play with.

Handy links once it's running:

- **API docs (try it live):** http://localhost:8080/swagger-ui.html
- **Database console:** http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:vimsdb`)

---

## The API

Everything lives under `/api/v1`. List endpoints support paging and sorting like
`?page=0&size=20&sort=name,asc`.

**Volunteers**
- `POST /volunteers` — add one
- `GET /volunteers` — list (filter by `city`, `skill`, `status`)
- `GET /volunteers/{id}` — one volunteer
- `PUT /volunteers/{id}` — edit
- `PATCH /volunteers/{id}/status` — activate / deactivate
- `GET /volunteers/{id}/campaigns` — what they've signed up for
- `GET /volunteers/{id}/summary` — their hours & campaign counts

**Campaigns**
- `POST /campaigns` — create
- `GET /campaigns` — list (filter by `status`, `type`, `upcoming`)
- `GET /campaigns/{id}` — details + how many enrolled
- `PUT /campaigns/{id}` — edit
- `PATCH /campaigns/{id}/status` — change status
- `GET /campaigns/{id}/volunteers` — who's enrolled
- `GET /campaigns/{id}/report` — attendance & hours

**Enrollments**
- `POST /enrollments` — sign a volunteer up
- `PATCH /enrollments/{id}/attendance` — mark attendance + log hours
- `DELETE /enrollments/{id}` — cancel (it's a soft cancel, not a real delete)

**Dashboard**
- `GET /dashboard/summary` — the headline numbers

When something goes wrong you get a clean JSON error with a `message`, and for
bad form input a `fieldErrors` map telling you which field was off.

---

## The rules it enforces

- A campaign can't take more people than its capacity.
- You can't sign the same volunteer up for the same campaign twice.
- You can only enroll in campaigns that are upcoming or active, and only active
  volunteers.
- Volunteers are never truly deleted — they're just set to inactive.
- Hours only count once a sign-up is marked **attended**.
- A new campaign's date has to be today or later.

---

## Going live (Render + Supabase)

The app ships as a **Docker** image (see the `Dockerfile`), so Render just builds
and runs it — no special setup. On Render, pick the **Docker** runtime, leave the
build/start commands blank, and set these environment variables:

| Variable | What it is |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `SPRING_DATASOURCE_URL` | your Postgres JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | DB user |
| `SPRING_DATASOURCE_PASSWORD` | DB password |

Don't set `PORT` — Render fills that in automatically.

Want to test the image locally first?

```bash
docker build -t nayepankh-vims .
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev nayepankh-vims
```

**Two things that will bite you with Supabase:**

1. Use the **connection pooler** URL (`aws-0-<region>.pooler.supabase.com:5432`),
   not the direct `db.<ref>.supabase.co` one — the direct one is IPv6-only and
   Render can't reach it. Add `?sslmode=require` on the end.
2. The username must include your project ref: `postgres.<project-ref>`, not just
   `postgres`.

Also note: Render's free tier sleeps when idle, so the first request after a quiet
spell takes ~30–60s to wake up. That's normal.

---

## How the code is laid out

```
src/main/java/com/nayepankh/vims/
├── VimsApplication.java   # starts everything
├── config/                # CORS, Swagger, dev seed data
├── controller/            # the HTTP endpoints
├── dto/                   # request/response shapes
├── entity/                # database tables + enums
├── exception/             # error handling
├── mapper/                # entity ↔ dto conversion
├── repository/            # database queries
└── service/               # the actual logic
```

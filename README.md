# DevOps-Ready API

A deliberately simple URL shortener — the point of this project isn't the domain logic, it's the
pipeline around it: containerization, observability, and CI/CD, done the way a small team would
actually run a Spring Boot service in production.

## Stack

- Java 21, Spring Boot 4, Spring Data JPA, PostgreSQL
- Micrometer + Prometheus for metrics, Spring Boot Actuator for health
- Multi-stage Docker build, docker-compose (app + Postgres + Prometheus)
- GitHub Actions: test on every PR, build & push a container image to GHCR on `main`
- `render.yaml` for one-click deploy to Render

## API

| Method | Path                     | Description                              |
|--------|--------------------------|-------------------------------------------|
| POST   | `/api/urls`              | Shorten a URL, returns its code           |
| GET    | `/{code}`                | 302 redirect to the original URL, counts the hit |
| GET    | `/api/urls/{code}/stats` | Hit count, creation time, original URL    |

```bash
CODE=$(curl -s -X POST localhost:8080/api/urls \
  -H 'Content-Type: application/json' \
  -d '{"originalUrl":"https://github.com/yourname"}' | jq -r .code)

curl -i localhost:8080/$CODE          # 302 → original URL
curl localhost:8080/api/urls/$CODE/stats
```

Codes are Base62 encodings of the row's auto-generated ID (`util/Base62Encoder`) — short, URL-safe,
collision-free without needing a uniqueness-retry loop.

## Running locally

Copy `.env.example` to `.env` and fill in real values (`.env` is gitignored):

```bash
cp .env.example .env
docker compose up --build
```

Brings up the app (`:8080`), Postgres, and Prometheus (`:9090`, scraping
`/actuator/prometheus` every 15s — see `ops/prometheus/prometheus.yml`). Actuator health is at
`/actuator/health`.

## The pipeline

- **`Dockerfile`** — multi-stage build (JDK for compiling, slim JRE for running), non-root user,
  container `HEALTHCHECK` against the app port.
- **`.github/workflows/ci-cd.yml`** — every push/PR runs `./gradlew build` (compile + test). On
  push to `main`, a second job builds the image and pushes `ghcr.io/<repo>:latest` and
  `ghcr.io/<repo>:<sha>` to GitHub Container Registry.
- **`render.yaml`** — Render blueprint wiring the Docker image to a managed Postgres instance via
  environment variables, so `render deploy` (or connecting the repo in the Render dashboard) is a
  working deployment with no manual config.
- **Metrics** — `micrometer-registry-prometheus` exposes `/actuator/prometheus`; point any
  Prometheus/Grafana stack at it for request latency, JVM, and datasource pool metrics for free.

## Tests

```bash
./gradlew test
```

- `util/Base62EncoderTest` — pure unit tests for the code generator
- `service/ShortUrlServiceTest` — Mockito tests for hit-counting and code assignment
- `controller/ShortUrlControllerTest` — `@WebMvcTest` slice covering validation and the redirect
  status/`Location` header
- `integration/ShortUrlIntegrationTest` — full create → redirect → stats flow against a real
  PostgreSQL container via Testcontainers

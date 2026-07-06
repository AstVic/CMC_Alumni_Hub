# Production deployment checklist

## Required configuration

Create a deployment-specific `.env` file and never commit it. At minimum set:

```dotenv
APP_PROFILE=prod
DB_URL=jdbc:postgresql://host:5432/database?sslmode=require
DB_USERNAME=...
DB_PASSWORD=...

# At least 64 random characters; the local default is rejected in production.
JWT_SECRET=...
APP_FRONTEND_URL=https://alumni.example.com

ADMIN_EMAIL=admin@example.com
# At least 12 characters. admin123, password and 123456 are rejected.
ADMIN_PASSWORD=...

APP_SEED_DEMO=false
SMTP_HOST=...
SMTP_PORT=587
SMTP_USERNAME=...
SMTP_PASSWORD=...
SMTP_FROM=CMC Alumni Hub <no-reply@example.com>
```

`APP_FRONTEND_URL` must be one exact HTTP(S) origin without a wildcard or path.
If SMTP is omitted the service starts, but logs a prominent production warning
and invitation emails are not delivered. Message bodies and raw invite tokens
are never written to logs.

## Validate and start

```bash
docker compose --env-file .env.prod -f docker-compose.prod.yml config
docker compose --env-file .env.prod -f docker-compose.prod.yml up --build -d
```

The frontend waits until the backend container is healthy. The production
compose exposes the frontend on port 80; terminate TLS in an external reverse
proxy or hosting platform.

## Verify

```bash
# Public health endpoint; response must be {"status":"UP"}.
curl --fail http://localhost/actuator/health

# Inspect startup, Flyway and SMTP warnings without printing application secrets.
docker compose --env-file .env.prod -f docker-compose.prod.yml logs backend

# Confirm all containers are healthy/running.
docker compose --env-file .env.prod -f docker-compose.prod.yml ps
```

Flyway owns the schema and runs on every backend startup. Hibernate uses
`ddl-auto=validate` and must never create or update production tables. A
checksum mismatch must be investigated; do not edit a migration already
applied to production.

## Security expectations

- Only `/actuator/health` is exposed by Actuator.
- Swagger/OpenAPI is disabled under the `prod` profile.
- `/api/admin/**` requires `ADMIN`; admin-account management requires `OWNER`.
- `/api/alumni/**` requires `ALUMNI`.
- `/uploads/**` is public and must contain only validated JPEG, PNG or WebP.
- Uploaded files live in the `uploads_data` Docker volume. Back it up if local
  uploads are business-critical; object storage is a recommended future step.

## Before public launch

- Put the frontend behind HTTPS and restrict direct access to backend/database.
- Use a managed PostgreSQL backup and restore policy.
- Use a transactional SMTP provider and monitor delivery failures.
- Add centralized logs/metrics and alert on failed healthchecks.
- Plan external object storage and malware scanning for uploaded photos.

# API Specification

Base URL: `/api`

## Dashboard

- `GET /dashboard`

## Admin

- `POST /admin/login`

Request:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

Response:

```json
{
  "username": "admin",
  "displayName": "Demo Administrator",
  "role": "ADMIN"
}
```

## Incidents

- `GET /incidents`
- `GET /incidents?status=DELEGATED`
- `GET /incidents/{id}`
- `POST /incidents`
- `PATCH /incidents/{id}/communicate`
- `PATCH /incidents/{id}/assess`
- `PATCH /incidents/{id}/delegate`
- `PATCH /incidents/{id}/resolve`
- `PATCH /incidents/{id}/rca`
- `PATCH /incidents/{id}/close`
- `POST /incidents/{id}/timeline`

## Knowledge Base

- `GET /knowledge-base`
- `GET /knowledge-base?q=cache`
- `GET /knowledge-base/{id}`
- `POST /knowledge-base`
- `PUT /knowledge-base/{id}`

## Status Values

`DETECTED`, `COMMUNICATING`, `ASSESSING`, `DELEGATED`, `RESOLVED`, `RCA`, `CLOSED`

## Severity Values

`SEV1`, `SEV2`, `SEV3`

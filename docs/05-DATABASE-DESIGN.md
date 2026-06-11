# Database Design

The schema is normalized around incident lifecycle artifacts.

## Tables

- `incidents`: Core incident record, severity, lifecycle status, roles, and timestamps
- `incident_assessment`: One-to-one impact and hypothesis assessment
- `incident_resolution`: One-to-one mitigation and resolution summary
- `incident_rca`: One-to-one root cause analysis and approval
- `incident_timeline`: One-to-many event stream for incident history
- `knowledge_base`: Runbooks and process articles

## Scripts

- Schema: `source-code/backend/src/main/resources/schema.sql`
- Sample data: `source-code/backend/src/main/resources/data.sql`
- Docker Compose: `docker-compose.yml`

The scripts are MySQL-compatible and also run in H2 MySQL mode for tests.

The recommended demo setup runs MySQL in Docker:

```bash
docker compose up -d mysql
```

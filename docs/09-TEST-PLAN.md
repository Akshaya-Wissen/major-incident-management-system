# Test Plan

## Automated Tests

Backend tests are under `source-code/backend/src/test/java`.

- Repository tests verify seeded incident and knowledge base persistence.
- Service tests cover incident creation, communication, assessment, resolution, RCA creation, closure, and invalid closure.
- Tests run against H2 in MySQL compatibility mode.

Run:

```bash
cd source-code/backend
mvn test
```

## Manual Demo Tests

1. Confirm dashboard loads metrics and recent incidents.
2. Create an incident from the Incident Board.
3. Open the incident detail page and submit lifecycle actions in order.
4. Confirm timeline entries are appended.
5. Confirm RCA page includes resolved/RCA/closed incidents.
6. Search and create Knowledge Base articles.

# Major Availability Incident Management System

Portfolio-grade monolithic demo application for managing major availability incidents through the workflow:

Detect -> Communicate -> Assess -> Delegate -> Resolve -> RCA -> Close

## Stack

- Java 17
- Spring Boot 3
- Spring Data JPA
- MySQL
- React + Vite
- Tailwind CSS
- Axios
- React Router

No authentication, authorization, JWT, SSO, email integration, microservices, Kafka, or RabbitMQ are included.

## Project Layout

- `source-code/backend` - Spring Boot REST API, schema, sample data, and tests
- `source-code/frontend` - React command center UI
- `docs` - product, architecture, API, database, testing, and implementation notes

## Run MySQL With Docker

From the project root:

```bash
docker compose up -d mysql
```

This starts a MySQL container with:

```properties
database=ims_demo
username=root
password=password
port=3306
```

The data is stored in the Docker volume `ims_mysql_data`.

## Run Backend

```bash
cd source-code/backend
mvn spring-boot:run
```

The API starts on `http://localhost:8080/api`.

The backend defaults match the Docker MySQL service. To override them:

```bash
DB_URL=jdbc:mysql://localhost:3306/ims_demo?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=password
```

## Run Frontend

```bash
cd source-code/frontend
npm install
npm run dev
```

The UI starts on `http://localhost:5173`.

Demo admin login:

```text
username: admin
password: admin123
```

You can override these values before starting the backend:

```bash
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin123
ADMIN_DISPLAY_NAME="Demo Administrator"
```

## View Database Data

Open a MySQL shell inside the Docker container:

```bash
docker exec -it ims-mysql mysql -uroot -ppassword ims_demo
```

Useful queries:

```sql
SHOW TABLES;
SELECT * FROM incidents;
SELECT * FROM incident_timeline ORDER BY incident_id, occurred_at;
SELECT * FROM incident_assessment;
SELECT * FROM incident_resolution;
SELECT * FROM incident_rca;
SELECT * FROM knowledge_base;
```

One-line option from PowerShell:

```powershell
docker exec ims-mysql mysql -uroot -ppassword ims_demo -e "SELECT id,title,severity,status,impacted_service FROM incidents;"
```

## Stop MySQL

```bash
docker compose down
```

To also delete the database volume and reset all data:

```bash
docker compose down -v
```

## Demo Flow

1. Open the dashboard to view current incident load.
2. Create a new incident from the Incident Board.
3. Open an incident detail page.
4. Submit communication, assessment, delegation, resolution, RCA, and closure forms in order.
5. Use the RCA page to track incidents ready for post-incident review.
6. Use the Knowledge Base for runbooks and RCA standards.

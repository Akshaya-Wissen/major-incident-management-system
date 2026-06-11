# Technical Requirements Document

## Runtime

- Java 17
- Spring Boot 3
- Node.js compatible with Vite 5
- Docker
- MySQL 8 container or compatible database

## Backend Requirements

- Layered Spring Boot monolith
- REST API under `/api`
- Demo admin login endpoint under `/api/admin/login`
- DTO validation with Jakarta Validation
- Spring Data JPA repositories
- MySQL schema and startup seed data
- H2-backed tests

## Frontend Requirements

- React + Vite
- Tailwind CSS
- Axios
- React Router
- Responsive dashboard, board, detail, RCA, and knowledge base pages

## Non-Requirements

No JWT, SSO, email integration, microservices, Kafka, or RabbitMQ. The admin login is a demonstration-only session gate and does not implement role-based authorization.

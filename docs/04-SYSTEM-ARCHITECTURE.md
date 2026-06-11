# System Architecture

## Architecture Style

Monolithic application with separate backend and frontend projects.

## Backend

- Spring Boot 3 REST API
- Layered architecture: Controller -> Service -> Repository
- DTOs for request and response payloads
- JPA entities for normalized persistence
- Global exception handling
- SQL schema and seed data

## Frontend

- React + Vite
- React Router for pages
- Axios for API calls
- Tailwind CSS for styling
- Reusable badges, cards, loading states, error banners, and page headers

## Data Flow

The React UI calls Spring Boot REST endpoints under `/api`. The backend validates payloads, enforces lifecycle transitions in the service layer, persists changes through Spring Data JPA, and records timeline entries for operational traceability.

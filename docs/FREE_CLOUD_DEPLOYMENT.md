# Free Cloud Deployment

This project can run with a no-cost setup:

- Backend: Render free web service
- Frontend: Vercel Hobby static deployment
- Database: Neon free Postgres

The app starts with no seeded incident or knowledge-base data. Login users remain available because they are configured in backend code and environment properties.

## 1. Create A Free Postgres Database

Create a Neon Postgres project and copy the connection details.

Use these backend environment variables:

```text
DB_PLATFORM=postgresql
DB_DRIVER=org.postgresql.Driver
DB_URL=jdbc:postgresql://<host>/<database>?sslmode=require
DB_USERNAME=<database-user>
DB_PASSWORD=<database-password>
```

## 2. Deploy Backend On Render

Create a Render web service from this repository.

Use:

```text
Root Directory: source-code/backend
Build Command: mvn clean package -DskipTests
Start Command: java -jar target/major-incident-management-system-0.0.1-SNAPSHOT.jar
Plan: Free
```

Set these environment variables:

```text
DB_PLATFORM=postgresql
DB_DRIVER=org.postgresql.Driver
DB_URL=jdbc:postgresql://<host>/<database>?sslmode=require
DB_USERNAME=<database-user>
DB_PASSWORD=<database-password>
ADMIN_USERNAME=<your-admin-username>
ADMIN_PASSWORD=<your-admin-password>
ADMIN_DISPLAY_NAME=Demo Administrator
GEMINI_API_KEY=<your-gemini-key>
GEMINI_MODEL=gemini-3.1-flash-lite
CORS_ALLOWED_ORIGINS=https://<your-vercel-app>.vercel.app
```

After the frontend is deployed, update `CORS_ALLOWED_ORIGINS` with the real Vercel URL and redeploy the backend.

## 3. Deploy Frontend On Vercel

Create a Vercel project from this repository.

Use:

```text
Root Directory: source-code/frontend
Build Command: npm run build
Output Directory: dist
```

Set:

```text
VITE_API_BASE_URL=https://<your-render-backend>.onrender.com/api
```

Redeploy after setting the variable.

## 4. Local Docker Still Works

Local MySQL still works with:

```powershell
docker compose up -d
```

Then start the backend from `source-code/backend` and frontend from `source-code/frontend`.

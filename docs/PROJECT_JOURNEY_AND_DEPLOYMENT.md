# Major Incident Management System - Project Journey And Deployment

This document captures the full project journey: what was built, how the application works, how it was prepared for production-style use, and how it was deployed on AWS.

## 1. Project Problem Statement

The project is a Jira-style major incident management tool for raising, tracking, escalating, resolving, and closing production incidents with role-based accountability.

It solves the problem of fragmented incident handling by giving teams one workflow for ticket creation, SLA/ETA tracking, escalation ownership, RCA, knowledge-base references, and AI-assisted incident support.

## 2. Tech Stack

Backend:

```text
Java 17
Spring Boot
Spring Web
Spring Data JPA
Hibernate
MySQL for local Docker
PostgreSQL for AWS RDS
Maven
```

Frontend:

```text
React
Vite
Tailwind CSS
Lucide React icons
Axios
React Router
```

Cloud and DevOps:

```text
AWS EC2
AWS RDS PostgreSQL
AWS S3 Static Website Hosting
Nginx
systemd
GitHub Actions
AWS CLI
SSH/SCP
```

AI:

```text
Gemini API
Configured model: gemini-3.1-flash-lite
```

## 3. Main Features Built

Incident workflow:

```text
Create incident
Track incident across lifecycle board
Communicate
Assess
Escalate
Resolve
RCA
Close
```

Role-based handling:

```text
Admin
Team Lead
Escalation Manager
Senior Manager
Communication Lead
Technical Lead
```

Approval and escalation flow:

```text
Team Lead -> Escalation Manager -> Senior Manager
```

SLA and ETA:

```text
Each incident has a resolution ETA
Escalation is allowed only after ETA is missed
Dashboard and board show SLA state
```

Production-style UI:

```text
Clickable dashboard cards
Clickable incident board cards
Responsive layout
Filters and search
CSV export
Clean login page without exposing passwords
Modal/popup success and error messages
```

AI assistant:

```text
Floating AI chatbot
Can answer using incident ID like INC-1, incident 1, ticket 1, or just 1
Fetches incident context from backend database
Can summarize incidents, suggest actions, draft updates, check SLA risk, and outline RCA
```

Clean data setup:

```text
Old seeded incidents and KB records removed from production startup
Login users are preserved in backend service code
Test data moved into src/test/resources/test-data.sql
```

## 4. Important Repository Files

Backend config:

```text
source-code/backend/src/main/resources/application.properties
source-code/backend/src/main/resources/schema-mysql.sql
source-code/backend/src/main/resources/schema-postgresql.sql
source-code/backend/src/main/resources/data-mysql.sql
source-code/backend/src/main/resources/data-postgresql.sql
```

Deployment:

```text
scripts/aws/ec2-bootstrap.sh
.github/workflows/backend-ec2-deploy.yml
amplify.yml
source-code/frontend/vercel.json
render.yaml
```

Deployment docs:

```text
docs/FREE_CLOUD_DEPLOYMENT.md
docs/AWS_FREE_TIER_CICD_DEPLOYMENT.md
docs/PROJECT_JOURNEY_AND_DEPLOYMENT.md
```

Local ignored secrets:

```text
.deploy-secrets.local
```

This file is intentionally ignored in `.gitignore`.

## 5. Local Development Setup

Start local MySQL:

```powershell
docker compose up -d
```

Start backend:

```powershell
cd source-code/backend
mvn spring-boot:run
```

Start frontend:

```powershell
cd source-code/frontend
npm install
npm run dev
```

Local URLs:

```text
Frontend: http://localhost:5173
Backend: http://localhost:8080/api
```

Run backend tests:

```powershell
cd source-code/backend
mvn clean test
```

Build frontend:

```powershell
cd source-code/frontend
npm run build
```

## 6. Application Configuration

The backend uses environment variables with safe defaults:

```text
PORT
DB_PLATFORM
DB_DRIVER
DB_URL
DB_USERNAME
DB_PASSWORD
ADMIN_USERNAME
ADMIN_PASSWORD
ADMIN_DISPLAY_NAME
CORS_ALLOWED_ORIGINS
GEMINI_API_KEY
GEMINI_MODEL
```

For AWS deployment:

```text
DB_PLATFORM=postgresql
DB_DRIVER=org.postgresql.Driver
DB_URL=jdbc:postgresql://<rds-endpoint>:5432/imsdb?sslmode=require
```

For local Docker:

```text
DB_PLATFORM=mysql
DB_DRIVER=com.mysql.cj.jdbc.Driver
DB_URL=jdbc:mysql://localhost:3306/ims_demo?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

## 7. AWS Architecture Used

Final deployed architecture:

```text
React frontend -> AWS S3 Static Website
Spring Boot backend -> AWS EC2 Amazon Linux 2023
Backend reverse proxy -> Nginx on EC2
Database -> AWS RDS PostgreSQL
Backend process manager -> systemd
CI/CD backend pipeline -> GitHub Actions over SSH/SCP
```

Live URLs:

```text
Frontend:
http://ims-frontend-477170635976-ap-south-1.s3-website.ap-south-1.amazonaws.com

Backend:
http://13.204.82.174/api
```

RDS endpoint:

```text
ims-db.ch020c6c69gl.ap-south-1.rds.amazonaws.com
```

AWS region:

```text
ap-south-1
```

## 8. AWS Resources Created

EC2:

```text
Instance public IP: 13.204.82.174
Instance user: ec2-user
Backend security group: ims-backend-sg
```

RDS:

```text
DB identifier: ims-db
Engine: PostgreSQL
DB name: imsdb
DB security group: ims-rds-sg
Subnet group: ims-db-subnet-group
```

S3:

```text
Bucket: ims-frontend-477170635976-ap-south-1
Hosting: Static website hosting enabled
Public read policy enabled for frontend files
```

## 9. AWS CLI And SSH Setup

AWS CLI was installed and verified:

```powershell
aws --version
aws sts get-caller-identity
```

SSH key permissions were fixed on Windows:

```powershell
icacls .\ims-backend-key.pem /inheritance:r
icacls .\ims-backend-key.pem /remove:g "DX002WZS\CodexSandboxUsers"
icacls .\ims-backend-key.pem /grant:r "$($env:USERNAME):R"
```

SSH command:

```powershell
ssh -i .\ims-backend-key.pem ec2-user@13.204.82.174
```

## 10. EC2 Bootstrap

The EC2 bootstrap script:

```text
scripts/aws/ec2-bootstrap.sh
```

It performs:

```text
Installs Java 17
Installs Nginx
Creates ims system user
Creates /opt/ims
Creates /opt/ims/ims-backend.env
Creates systemd service ims-backend
Creates Nginx reverse proxy for /api
Enables Nginx
Enables backend service
```

Manual bootstrap command used:

```bash
chmod +x /home/ec2-user/ec2-bootstrap.sh
/home/ec2-user/ec2-bootstrap.sh
```

## 11. Backend Deployment

Backend jar was built locally:

```powershell
cd source-code/backend
mvn clean package -DskipTests
```

Jar uploaded to EC2:

```text
/opt/ims/ims-backend.jar
```

Backend env file:

```text
/opt/ims/ims-backend.env
```

Backend service:

```text
ims-backend.service
```

Start backend:

```bash
sudo systemctl start ims-backend
```

Stop backend:

```bash
sudo systemctl stop ims-backend
```

Restart backend:

```bash
sudo systemctl restart ims-backend
```

Check backend status:

```bash
sudo systemctl status ims-backend --no-pager
```

View backend logs:

```bash
sudo journalctl -u ims-backend -f
```

## 12. Frontend Deployment

Frontend was built with the deployed backend API:

```powershell
$env:VITE_API_BASE_URL='http://13.204.82.174/api'
cd source-code/frontend
npm run build
```

Frontend files were uploaded to S3:

```text
source-code/frontend/dist -> s3://ims-frontend-477170635976-ap-south-1
```

S3 static website URL:

```text
http://ims-frontend-477170635976-ap-south-1.s3-website.ap-south-1.amazonaws.com
```

Backend CORS was updated to allow the S3 frontend:

```text
CORS_ALLOWED_ORIGINS=http://ims-frontend-477170635976-ap-south-1.s3-website.ap-south-1.amazonaws.com,http://13.204.82.174,http://localhost:5173,http://127.0.0.1:5173
```

## 13. AI Chatbot Setup

Gemini API key is configured on EC2 in:

```bash
sudo nano /opt/ims/ims-backend.env
```

Important values:

```text
GEMINI_API_KEY=<your-key>
GEMINI_MODEL=gemini-3.1-flash-lite
```

After editing:

```bash
sudo systemctl restart ims-backend
```

Nano save and exit:

```text
Ctrl + O
Enter
Ctrl + X
```

## 14. GitHub Actions CI/CD

Backend workflow:

```text
.github/workflows/backend-ec2-deploy.yml
```

Pipeline behavior:

```text
Push to main
Run Maven tests
Build Spring Boot jar
Copy jar to EC2
Restart ims-backend service
```

GitHub secrets required:

```text
EC2_HOST=13.204.82.174
EC2_USER=ec2-user
EC2_SSH_PRIVATE_KEY=<full contents of ims-backend-key.pem>
```

Where to add secrets:

```text
GitHub repository
Settings
Secrets and variables
Actions
New repository secret
```

## 15. Verification Performed

Backend API:

```text
GET http://13.204.82.174/api/dashboard
```

Result:

```text
200 OK
Zero incidents from clean RDS database
```

Frontend:

```text
GET http://ims-frontend-477170635976-ap-south-1.s3-website.ap-south-1.amazonaws.com
```

Result:

```text
200 OK
```

CORS:

```text
Backend allows S3 frontend origin
```

Login:

```text
POST http://13.204.82.174/api/admin/login
```

Result:

```text
Login successful with generated admin credentials
```

## 16. Cost Control Notes

To control AWS Free Tier usage:

```text
Use one EC2 micro instance
Use one RDS micro instance
Avoid NAT Gateway
Avoid Load Balancer
Monitor S3 usage
Create AWS budget alerts
Stop or delete unused resources
```

Stopping only the backend service does not stop AWS billing:

```bash
sudo systemctl stop ims-backend
```

To reduce compute charges, stop EC2 from AWS Console.

RDS must be stopped or deleted separately if not needed.

## 17. Operational Commands

SSH to EC2:

```powershell
ssh -i C:\Users\user\Downloads\ims-backend-key.pem ec2-user@13.204.82.174
```

Backend status:

```bash
sudo systemctl status ims-backend --no-pager
```

Restart backend:

```bash
sudo systemctl restart ims-backend
```

Nginx status:

```bash
sudo systemctl status nginx --no-pager
```

Backend logs:

```bash
sudo journalctl -u ims-backend -f
```

Edit backend environment:

```bash
sudo nano /opt/ims/ims-backend.env
```

## 18. Known Follow-Ups

Recommended next improvements:

```text
Add HTTPS using CloudFront or an Application Load Balancer
Move frontend CI/CD to Amplify or GitHub Actions S3 sync
Store secrets in AWS Secrets Manager or SSM Parameter Store
Add proper user database and password hashing
Add backend health endpoint
Add CloudWatch alarms
Add custom domain
Add database migrations with Flyway
```

## 19. Current Final State

The application is deployed and running on AWS.

```text
Frontend: AWS S3 static website
Backend: EC2 + Nginx + Spring Boot systemd service
Database: RDS PostgreSQL
AI: Gemini key configured through backend env file
CI/CD: backend GitHub Actions workflow present, secrets pending/user-managed
```

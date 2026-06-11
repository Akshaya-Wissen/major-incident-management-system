# AWS Free Tier CI/CD Deployment

Yes, this project can be deployed on an AWS Free Tier account if you keep the resources small and monitor usage.

Recommended low-cost architecture:

- Frontend: AWS Amplify Hosting
- Backend: EC2 `t3.micro` or `t4g.micro`
- Database: RDS PostgreSQL `db.t3.micro` or `db.t4g.micro`
- CI/CD: GitHub Actions for backend, Amplify branch deploy for frontend

This avoids ECS Fargate plus an Application Load Balancer, which can become chargeable quickly.

## 1. Create AWS Budget Alert

Before deploying anything:

1. Open AWS Billing.
2. Create a monthly budget.
3. Set alert at `1 USD` and `5 USD`.

## 2. Create RDS PostgreSQL

1. Open RDS.
2. Create database.
3. Engine: PostgreSQL.
4. Template: Free tier.
5. DB instance: `db.t3.micro` or `db.t4g.micro`.
6. DB name: `imsdb`.
7. Public access: `No`.
8. Save username and password.

Security group rule:

```text
Type: PostgreSQL
Port: 5432
Source: EC2 backend security group
```

Backend JDBC URL:

```text
jdbc:postgresql://<rds-endpoint>:5432/imsdb?sslmode=require
```

## 3. Create EC2 Backend Server

1. Open EC2.
2. Launch instance.
3. AMI: Amazon Linux 2023.
4. Type: `t3.micro` or `t2.micro` based on your free-tier eligibility.
5. Storage: keep small, for example `8 GiB`.
6. Security group inbound:

```text
SSH  22  your-ip-only
HTTP 80  0.0.0.0/0
```

7. Download the `.pem` key.

## 4. Bootstrap EC2

SSH into the instance:

```bash
ssh -i your-key.pem ec2-user@<ec2-public-ip>
```

Copy and run:

```bash
curl -o ec2-bootstrap.sh https://raw.githubusercontent.com/<your-github-user>/<your-repo>/main/scripts/aws/ec2-bootstrap.sh
chmod +x ec2-bootstrap.sh
./ec2-bootstrap.sh
```

Edit environment variables:

```bash
sudo nano /opt/ims/ims-backend.env
```

Set:

```text
PORT=8080
DB_PLATFORM=postgresql
DB_DRIVER=org.postgresql.Driver
DB_URL=jdbc:postgresql://<rds-endpoint>:5432/imsdb?sslmode=require
DB_USERNAME=<db-user>
DB_PASSWORD=<db-password>
ADMIN_USERNAME=<admin-user>
ADMIN_PASSWORD=<strong-admin-password>
ADMIN_DISPLAY_NAME=Demo Administrator
GEMINI_API_KEY=<gemini-key>
GEMINI_MODEL=gemini-3.1-flash-lite
CORS_ALLOWED_ORIGINS=https://<amplify-domain>
```

## 5. Configure GitHub Secrets

In GitHub repository settings, add:

```text
EC2_HOST=<ec2-public-ip-or-dns>
EC2_USER=ec2-user
EC2_SSH_PRIVATE_KEY=<contents-of-your-private-key-pem>
```

The backend workflow is already included:

```text
.github/workflows/backend-ec2-deploy.yml
```

On push to `main`, it will:

1. Run backend tests.
2. Build the Spring Boot jar.
3. Copy the jar to EC2.
4. Restart the `ims-backend` systemd service.

## 6. First Backend Deployment

Push to `main`, or manually run the GitHub Actions workflow:

```text
Actions -> Backend CI/CD to AWS EC2 -> Run workflow
```

Check service status on EC2:

```bash
sudo systemctl status ims-backend --no-pager
sudo journalctl -u ims-backend -f
```

Backend API URL:

```text
http://<ec2-public-dns>/api
```

## 7. Deploy Frontend With Amplify

1. Open AWS Amplify.
2. Create app.
3. Connect GitHub repository.
4. Select branch: `main`.
5. Amplify will use `amplify.yml`.
6. Add environment variable:

```text
VITE_API_BASE_URL=http://<ec2-public-dns>/api
```

7. Deploy.

## 8. Update Backend CORS

After Amplify gives you a URL:

```text
https://<branch>.<app-id>.amplifyapp.com
```

Update EC2:

```bash
sudo nano /opt/ims/ims-backend.env
```

Set:

```text
CORS_ALLOWED_ORIGINS=https://<branch>.<app-id>.amplifyapp.com
```

Restart:

```bash
sudo systemctl restart ims-backend
```

## 9. Validate

1. Open Amplify URL.
2. Log in using your admin credentials.
3. Create a new incident.
4. Confirm the dashboard shows it.
5. Ask the AI assistant about the incident ID.
6. Check logs:

```bash
sudo journalctl -u ims-backend -n 100 --no-pager
```

## 10. Cost Controls

To stay near free tier:

- Use one EC2 micro instance only.
- Use one RDS micro instance only.
- Do not create a NAT Gateway.
- Do not use an Application Load Balancer for this setup.
- Keep Amplify build minutes low.
- Stop/delete resources when finished.

## Useful Commands

Restart backend:

```bash
sudo systemctl restart ims-backend
```

View logs:

```bash
sudo journalctl -u ims-backend -f
```

Check Nginx:

```bash
sudo systemctl status nginx --no-pager
```

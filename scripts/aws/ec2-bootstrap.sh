#!/usr/bin/env bash
set -euo pipefail

sudo dnf update -y
sudo dnf install -y java-17-amazon-corretto-headless nginx

sudo useradd --system --home /opt/ims --shell /sbin/nologin ims || true
sudo mkdir -p /opt/ims
sudo chown -R ims:ims /opt/ims

sudo tee /opt/ims/ims-backend.env >/dev/null <<'ENV'
PORT=8080
DB_PLATFORM=postgresql
DB_DRIVER=org.postgresql.Driver
DB_URL=jdbc:postgresql://REPLACE_RDS_ENDPOINT:5432/imsdb?sslmode=require
DB_USERNAME=REPLACE_DB_USERNAME
DB_PASSWORD=REPLACE_DB_PASSWORD
ADMIN_USERNAME=admin
ADMIN_PASSWORD=REPLACE_STRONG_ADMIN_PASSWORD
ADMIN_DISPLAY_NAME=Demo Administrator
GEMINI_API_KEY=REPLACE_GEMINI_API_KEY
GEMINI_MODEL=gemini-3.1-flash-lite
CORS_ALLOWED_ORIGINS=https://REPLACE_AMPLIFY_DOMAIN
ENV

sudo chmod 600 /opt/ims/ims-backend.env
sudo chown ims:ims /opt/ims/ims-backend.env

sudo tee /etc/systemd/system/ims-backend.service >/dev/null <<'SERVICE'
[Unit]
Description=Major Incident Management Backend
After=network.target

[Service]
User=ims
Group=ims
WorkingDirectory=/opt/ims
EnvironmentFile=/opt/ims/ims-backend.env
ExecStart=/usr/bin/java -jar /opt/ims/ims-backend.jar
Restart=always
RestartSec=10
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
SERVICE

sudo tee /etc/nginx/conf.d/ims-backend.conf >/dev/null <<'NGINX'
server {
    listen 80;
    server_name _;

    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
NGINX

sudo systemctl daemon-reload
sudo systemctl enable ims-backend
sudo systemctl enable --now nginx

echo "Bootstrap complete. Edit /opt/ims/ims-backend.env, upload the jar, then run: sudo systemctl start ims-backend"

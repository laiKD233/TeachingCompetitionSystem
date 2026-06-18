#!/usr/bin/env bash

set -euo pipefail

APP_ROOT="/opt/teaching-competition"
BACKEND_DIR="${APP_ROOT}/backend"
FRONTEND_DIR="${APP_ROOT}/frontend"
WEB_ROOT="/var/www/teaching-competition"
UPLOAD_ROOT="/opt/teaching-competition/uploads"

# Cleanup potentially stale source files left untracked on server
rm -f "${BACKEND_DIR}/src/main/java/com/teaching/competition/config/JacksonConfig.java"

echo "[1/6] Build backend"
cd "${BACKEND_DIR}"
mvn clean package -DskipTests

echo "[2/6] Build frontend"
if [[ "${SKIP_FRONTEND_BUILD:-0}" == "1" ]]; then
  echo "Skip frontend build (SKIP_FRONTEND_BUILD=1)"
else
  cd "${FRONTEND_DIR}"
  npm ci
  npm run build
fi

echo "[3/6] Publish frontend dist"
sudo mkdir -p "${WEB_ROOT}"
sudo rm -rf "${WEB_ROOT:?}"/*
sudo cp -r "${FRONTEND_DIR}/dist/"* "${WEB_ROOT}/"
if [[ -d "${APP_ROOT}/deploy/demo-uploads" ]]; then
  sudo mkdir -p "${UPLOAD_ROOT}"
  sudo cp -r "${APP_ROOT}/deploy/demo-uploads/"* "${UPLOAD_ROOT}/"
fi
if [[ -d "${APP_ROOT}/deploy/seed-uploads" ]]; then
  sudo mkdir -p "${UPLOAD_ROOT}"
  sudo cp -r "${APP_ROOT}/deploy/seed-uploads/"* "${UPLOAD_ROOT}/"
fi

echo "[4/6] Install nginx config"
sudo cp "${APP_ROOT}/deploy/nginx/teaching-competition.conf" /etc/nginx/conf.d/teaching-competition.conf
sudo nginx -t
sudo systemctl restart nginx

echo "[5/6] Install backend service"
sudo cp "${APP_ROOT}/deploy/systemd/teaching-competition-backend.service" /etc/systemd/system/teaching-competition-backend.service
sudo systemctl daemon-reload
sudo systemctl enable teaching-competition-backend

echo "[6/6] Restart backend service"
sudo systemctl restart teaching-competition-backend
sudo systemctl status teaching-competition-backend --no-pager

echo "Deploy finished."

#!/usr/bin/env bash
# =========================================================
# Tech Knife - Automated MongoDB Atlas / Local Backup Script
# =========================================================

set -e

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_DIR="./backups/mongodb_${TIMESTAMP}"
MONGODB_URI="${MONGODB_URI:-mongodb://localhost:27017/techknife_dev}"

echo "[INFO] Starting Tech Knife MongoDB Backup at ${TIMESTAMP}..."

mkdir -p "${BACKUP_DIR}"

# Execute mongodump
mongodump --uri="${MONGODB_URI}" --out="${BACKUP_DIR}" --gzip

# Compress Archive
tar -czf "${BACKUP_DIR}.tar.gz" -C "./backups" "mongodb_${TIMESTAMP}"
rm -rf "${BACKUP_DIR}"

echo "[SUCCESS] MongoDB Backup Completed: ${BACKUP_DIR}.tar.gz"

# Optional Retention Policy: Remove backups older than 30 days
find ./backups -type f -name "mongodb_*.tar.gz" -mtime +30 -exec rm -f {} \;

#!/usr/bin/env bash
# =========================================================
# Tech Knife - Automated MongoDB Atlas / Local Restore Script
# =========================================================

set -e

if [ -z "$1" ]; then
  echo "[ERROR] Please specify backup tar.gz archive path."
  echo "Usage: ./scripts/restore-mongodb.sh ./backups/mongodb_20260726_120000.tar.gz"
  exit 1
fi

ARCHIVE_PATH="$1"
MONGODB_URI="${MONGODB_URI:-mongodb://localhost:27017/techknife_dev}"
TEMP_EXTRACT_DIR="./backups/temp_restore"

echo "[INFO] Restoring Tech Knife MongoDB from ${ARCHIVE_PATH}..."

mkdir -p "${TEMP_EXTRACT_DIR}"
tar -xzf "${ARCHIVE_PATH}" -C "${TEMP_EXTRACT_DIR}"

# Execute mongorestore
mongorestore --uri="${MONGODB_URI}" --drop --gzip "${TEMP_EXTRACT_DIR}"/*

# Cleanup
rm -rf "${TEMP_EXTRACT_DIR}"

echo "[SUCCESS] Tech Knife MongoDB Database Restored Successfully."

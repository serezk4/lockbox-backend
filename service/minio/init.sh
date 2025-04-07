#!/bin/sh

sleep 10

ACCESS_KEY="${MINIO_ROOT_USER}"
SECRET_KEY="${MINIO_ROOT_PASSWORD}"

if [ -z "$ACCESS_KEY" ] || [ -z "$SECRET_KEY" ]; then
  echo "MinIO credentials are missing!"
  exit 1
fi

mc alias set local http://localhost:9000 "$ACCESS_KEY" "$SECRET_KEY"
mc ls local/avatars || mc mb local/avatars
echo "Bucket created"

#!/bin/sh

print_header() {
  local text=$1
  local border="========================================"
  echo
  echo -e "\033[1;36m$border\033[0m"
  echo -e "\033[1;36m  $text\033[0m"
  echo -e "\033[1;36m$border\033[0m"
  echo
}

print_success() {
  echo -e "\033[1;32m✔ $1\033[0m"
}

print_error() {
  echo -e "\033[1;31m✖ $1\033[0m"
}

print_progress() {
  echo -e "\033[1;34m➜ $1\033[0m"
}

print_timing() {
  local seconds=$1
  echo -e "\033[1;33m⏳ Completed in ${seconds}s\033[0m"
}

wait_for_health() {
  local container_name=$1
  local max_wait_time=$2
  local elapsed=0

  print_progress "Waiting for container \033[1;35m$container_name\033[0m to become healthy..."
  while [ $elapsed -lt $max_wait_time ]; do
    local health_status=$(docker inspect --format='{{json .State.Health.Status}}' "$container_name" 2>/dev/null | tr -d '"')
    if [ "$health_status" = "healthy" ]; then
      print_success "Container $container_name is healthy!"
      return 0
    fi
    sleep 1
    elapsed=$((elapsed + 1))
  done
  print_error "Container $container_name did not become healthy within $max_wait_time seconds!"
  return 1
}

# Main script execution
print_header "Preparing Networks"
print_progress "Initializing networks..."
start_time=$(date +%s)
sh prepare.sh >/dev/null 2>&1
docker network list
end_time=$(date +%s)
print_success "Networks prepared successfully!"
print_timing $((end_time - start_time))

print_header "Preparing Configuration"
CURRENT_PATH=$(pwd)
DEV_PATH="$CURRENT_PATH/env/dev"
print_progress "Using environment path: \033[1;33m$DEV_PATH\033[0m"
export CONFIG_BASE_PATH="$DEV_PATH"
print_success "Configuration prepared successfully!"

print_header "Starting Services"

start_service() {
  local service_name=$1
  local path=$2
  local container_name=$3
  local max_wait_time=$4

  print_progress "Starting \033[1;36m$service_name\033[0m..."
  start_time=$(date +%s)
  (cd "$path" && make >/dev/null 2>&1)
  wait_for_health "$container_name" "$max_wait_time" || exit 1
  end_time=$(date +%s)
  print_success "$service_name started successfully!"
  print_timing $((end_time - start_time))
}

start_service "PostgreSQL" "./service/postgres" "postgres" 40
start_service "Keycloak" "./service/keycloak" "keycloak" 40
start_service "MinIO" "./service/minio" "minio" 40

print_header "Starting API"
start_time=$(date +%s)
(cd api && make >/dev/null 2>&1)
end_time=$(date +%s)
print_success "API started successfully!"
print_timing $((end_time - start_time))

print_header "Active Containers"
docker ps --format "table {{.ID}}\t{{.Names}}\t{{.Status}}\t{{.Ports}}"

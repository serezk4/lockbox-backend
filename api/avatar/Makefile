.PHONY: run down build up clean reset restart logs ps

run: down build up

down:
	@echo "Stopping and removing containers..."
	docker compose down

build:
	@echo "Building services..."
	docker compose build

up:
	@echo "Starting containers..."
	docker compose up -d

reset:
	@echo "Resetting by removing containers and volumes..."
	docker compose down -v

restart: down up

logs:
	@echo "Showing logs..."
	docker compose logs -f

ps:
	@echo "Listing running containers..."
	docker compose ps

lint:
	@echo "Running linter..."
	./gradlew checkstyleMain checkstyleTest

# Run tests
test:
	@echo "Running tests..."
	./gradlew test

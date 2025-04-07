.PHONY: all run down build up clean reset restart logs ps

SUBDIRS := $(wildcard */)
SUBDIRS := $(filter %/, $(foreach dir, $(SUBDIRS), $(if $(wildcard $(dir)Makefile), $(dir),)))

define run_in_subdirs
	@for dir in $(SUBDIRS); do \
		echo "Running '$(1)' in $$dir..."; \
		$(MAKE) -C $$dir $(1); \
	done
endef

all: run

run: down build up
	$(call run_in_subdirs,run)

down:
	@echo "Stopping and removing containers..."
	$(call run_in_subdirs,down)

build:
	@echo "Building services..."
	$(call run_in_subdirs,build)

up:
	@echo "Starting containers..."
	$(call run_in_subdirs,up)

reset:
	@echo "Resetting by removing containers and volumes..."
	$(call run_in_subdirs,reset)

restart: down up

logs:
	@echo "Showing logs..."
	$(call run_in_subdirs,logs)

ps:
	@echo "Listing running containers..."
	$(call run_in_subdirs,ps)


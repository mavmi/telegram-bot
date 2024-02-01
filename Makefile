BASE_IMG	=	bot_service_base_docker_image
ROOT_DIR	=	$$HOME/telegram-data
BOT_VOLUME	=	$(ROOT_DIR)/bot-files
DB_VOLUME	=	$(ROOT_DIR)/database

all: build background

parent:
	@docker build -t $(BASE_IMG) -f ./docker/baseImageDockerfile .

build: parent
	-mkdir -p $(BOT_VOLUME)
	-mkdir -p $(DB_VOLUME)
	-mkdir $(BOT_VOLUME)/shakal-bot
	-mkdir $(BOT_VOLUME)/water-stuff-bot
	-mkdir $(BOT_VOLUME)/monitoring-bot

	@mvn package -P PROD
	@docker compose build

foreground_dev:
	@docker compose --profile dev up

foreground_prod:
	@docker compose --profile prod up

background_dev:
	@docker compose --profile dev up -d

background_prod:
	@docker compose --profile prod up -d

stop:
	@docker compose stop

clean:
	@mvn clean
	@docker system prune -af

re: clean build background

.PHONY: all build task-manager foreground background stop clean re
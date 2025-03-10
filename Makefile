BASE_IMG				=	bot_service_base_docker_image
ROOT_DIR				=	$$HOME/services/telegram-bot/volumes
MONITORING_BOT_VOLUME	=	$(ROOT_DIR)/monitoring_telegram_bot
ROCKETCHAT_BOT_VOLUME	=	$(ROOT_DIR)/rocketchat_telegram_bot
SHAKAL_BOT_VOLUME		=	$(ROOT_DIR)/shakal_telegram_bot
WATER_STUFF_BOT_VOLUME	=	$(ROOT_DIR)/water_stuff_telegram_bot

prepareDirs:
	-mkdir -p $(MONITORING_BOT_VOLUME)/cert/mail-ca
	-mkdir -p $(MONITORING_BOT_VOLUME)/cert/server
	-mkdir -p $(MONITORING_BOT_VOLUME)/cert/postgres-client
	-mkdir -p $(MONITORING_BOT_VOLUME)/data
	-mkdir -p $(MONITORING_BOT_VOLUME)/spring

	-mkdir -p $(ROCKETCHAT_BOT_VOLUME)/cert/server
	-mkdir -p $(ROCKETCHAT_BOT_VOLUME)/cert/postgres-client
	-mkdir -p $(ROCKETCHAT_BOT_VOLUME)/data
	-mkdir -p $(ROCKETCHAT_BOT_VOLUME)/spring

	-mkdir -p $(SHAKAL_BOT_VOLUME)/cert/server
	-mkdir -p $(SHAKAL_BOT_VOLUME)/cert/postgres-client
	-mkdir -p $(SHAKAL_BOT_VOLUME)/data
	-mkdir -p $(SHAKAL_BOT_VOLUME)/spring

	-mkdir -p $(WATER_STUFF_BOT_VOLUME)/cert/server
	-mkdir -p $(WATER_STUFF_BOT_VOLUME)/cert/postgres-client
	-mkdir -p $(WATER_STUFF_BOT_VOLUME)/data
	-mkdir -p $(WATER_STUFF_BOT_VOLUME)/spring

all: build background

parent: prepareDirs
	@docker build -t $(BASE_IMG) -f ./docker/baseImageDockerfile .

build: prepareDirs
	@mvn package -P PROD -U
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

.PHONY: prepareDirs all parent build foreground_dev foreground_prod background_dev background_prod stop clean re
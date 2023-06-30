BASE_IMG	=	bot_service_base_docker_image
ROOT_DIR	=	$$HOME/telegram-data
BOT_VOLUME	=	$(ROOT_DIR)/bot-files
DB_VOLUME	=	$(ROOT_DIR)/database

all: build background

parent:
	@docker build -t $(BASE_IMG) -f ./docker/botServiceBaseImage .

build: parent
	-mkdir -p $(BOT_VOLUME)
	-mkdir -p $(DB_VOLUME)

	@mvn package
	@docker compose build

foreground:
	@docker compose up

background:
	@docker compose up -d

stop:
	@docker compose stop

clean:
	@mvn clean
	@docker system prune -af

re: clean build background

.PHONY: all build foreground background stop clean re
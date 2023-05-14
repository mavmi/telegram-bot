ROOT_DIR	=	$$HOME/SHAKAL-BOT
BOT_VOLUME	=	$(ROOT_DIR)/bot-volume
DB_VOLUME	=	$(ROOT_DIR)/db-volume

all: build run

build:
	-mkdir -p $(BOT_VOLUME)
	-mkdir -p $(DB_VOLUME)
	@mvn -f ./SHAKAL-BOT/pom.xml package
	@mvn -f ./WATER-STUFF-BOT/pom.xml package
	@docker compose build

run:
	@docker compose up

re: clean build run

clean:
	@mvn -f ./SHAKAL-BOT/pom.xml clean
	@mvn -f ./WATER-STUFF-BOT/pom.xml clean
	@docker system prune -af

.PHONY: all build run re clean

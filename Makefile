ROOT_DIR	=	$$HOME/SHAKAL-BOT
BOT_VOLUME	=	$(ROOT_DIR)/bot-volume
DB_VOLUME	=	$(ROOT_DIR)/db-volume

all: build background

build:
	-mkdir -p $(BOT_VOLUME)
	-mkdir -p $(DB_VOLUME)
	@mvn -f ./SHAKAL-BOT/pom.xml package
	@mvn -f ./WATER-STUFF-BOT/pom.xml package
	@docker compose build

foreground:
	@docker compose up

background:
	@docker compose up -d

stop:
	@docker compose stop

re: clean build background

clean:
	@mvn -f ./SHAKAL-BOT/pom.xml clean
	@mvn -f ./WATER-STUFF-BOT/pom.xml clean
	@docker system prune -af

.PHONY: all build foreground background stop re clean

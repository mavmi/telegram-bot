TAG = shakal-docker-image
HOME = $$HOME

all: build run

build:
	docker build . -t $(TAG)

run:
	-mkdir /home/username/pg_volume
	-mkdir /home/username/bot_volume
	docker run\
			-v /home/username/bot_volume:/bot-files\
			-v /home/username/pg_volume:/var/lib/postgresql/14/main \
			-it --rm $(TAG)

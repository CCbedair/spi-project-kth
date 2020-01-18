.PHONY: keys

build:
	cd .. && \
	docker build -f data-collector/Dockerfile -t data-collector . && \
	cd data-collector

run:
	docker run --net host --add-host=gm:127.0.0.1 -e GM_URL='https://gm/' \
		-e GENERAL_MESSAGE="YES" -p 8082:8082 --rm --name dc data-collector


run_mac:
    docker run --add-host=gm:127.0.0.1 -e GM_URL='https://gm/' \
    		-p 8082:8082 --rm --name dc data-collector

stop:
	docker stop dc
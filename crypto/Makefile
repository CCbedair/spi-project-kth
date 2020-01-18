UID=$(shell id -u)
GID=$(shell id -g)

crypto_builder_image:
	docker build -t crypto_builder .

crypto_lib:
	docker run --rm --name my_builder --user ${UID}:${GID} -it -v $(shell pwd):/crypto crypto_builder:latest

clean:
	docker rm my_builder
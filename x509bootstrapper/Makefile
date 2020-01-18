UID=$(shell id -u)
GID=$(shell id -g)

build_image:
	docker build -t bootstrapper .

build_jar:
	docker run --rm --name bootstrapping --user ${UID}:${GID} -it -v $(shell pwd):/bootstrapper bootstrapper

keys:
	openssl ecparam -name prime256v1 -genkey -noout -out private.key && \
		               openssl req -subj '/CN=client/emailAddress=client_instance@kth.se' -new -key private.key -out cert_request.csr
build_1:
	cd ../ && \
	docker build -f client/Dockerfile -t client_spi_1 . && \
	cd client/

build_2:
	cd ../ && \
	docker build -f client/Dockerfile -t client_spi_2 . && \
	cd client/

run_1:
	docker run --net host --add-host=gm:127.0.0.1 -e GM_URL='https://gm/' \
		--add-host=dc:127.0.0.1 -e DC_URL='https://dc:8082/' -e SCHEME='GM' \
		-e GENERAL_MESSAGE='NO' --rm --name  console_client_spi_1  client_spi_1

run_2:
	docker run --net host --add-host=gm:127.0.0.1 -e GM_URL='https://gm/' \
		--add-host=dc:127.0.0.1 -e DC_URL='https://dc:8082/' -e SCHEME='HYBRID' \
		-e GENERAL_MESSAGE='NO' --rm --name  console_client_spi_2  client_spi_2

stop:
	docker stop console_client_spi_1&&docker stop console_client_spi_2


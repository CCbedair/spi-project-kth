keys:
	openssl ecparam -name prime256v1 -genkey -noout -out private.key && \
		               openssl req -subj '/CN=gm/emailAddress=gm_instance@kth.se' -new -key private.key \
		               -out cert_request.csr
build:
	cd .. && docker build -t gm-server-image -f group-manager-server/Dockerfile . && cd group-manager-server

run:
	docker run --net host --add-host=ltca.vpki-nss-kth.se:34.98.123.91 -e N_CLIENT=50 -p 443:443 \
		--rm --name gm-server-container gm-server-image

run_mac:
	docker run --add-host=ltca.vpki-nss-kth.se:34.98.123.91 -e N_CLIENT=50 -p 443:443 \
		--rm --name gm-server-container gm-server-image

stop:
	docker stop gm-server-container

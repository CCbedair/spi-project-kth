# Client Side

### How to run the Client Console.

It uses the library Retrofit.

We assume that you manually put the following files in the ./client/ folder
* crypto-1.0-SNAPSHOT.jar
* liboracle.so

From the Terminal, this should be run on the same directory where the Dockerfile is located. 

First we build the image:
```bash
docker build -t client_spi .
```
You can use the spawning script to simulate a certain amount of clients, i.e., to run 12 clients run:
* First parameter the amount of clients to spawn
* Second parameter the GM server
* Third parameter the data collection server
* Fourth parameter should be TRUE if you want to run in "Midterm presentation mode", or empty otherwise
```bash
bash multiple_clients_mock.sh   12   http://192.168.43.55:9999/    http://192.168.43.33:8082/   TRUE
```

In case you want to run manually one instance/container then run it with:
* GM_URL Is the GM Server running instance
* DC_URL Is the Data Collection Server running instance
* MIDTERM_DEMO If we are in the "Midterm Demo" presentation Mode, then we might want to add TRUE to the flag MIDTERM_DEMO, otherwise empty
```bash
docker run  \
-e GM_URL='http://192.168.43.55:9999/' \
-e DC_URL='http://192.168.43.33:8082/' \
-e MIDTERM_DEMO='TRUE' \
--rm --name  console_client_spi  client_spi
```


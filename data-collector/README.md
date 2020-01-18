# Data collector

## Running
The data collector runs on Docker.

To run jar file: java -jar target/client-logs-services-0.0.1-SNAPSHOT.jar


Build: docker build -f data-collector/Dockerfile -t data-collector .


To see the images:  docker images


To see containers:  docker container list


To stop the docker container: docker stop <name> (not tag or container id)


Run: docker run -p 9999:8082 <image>

docker run -p 8082:8082 -dt data-collector

docker run -p 8082:8082 -t data-collector

The **docker system prune** command will remove all stopped containers, all dangling images, and all unused networks

Managing the dependencies:
install:install-file \   -Dfile=crypto/crypto.jar \   -Durl='file://${basedir}/crypto' \   -DgroupId=com.crypto \   -DartifactId=crypto \   -Dversion=0.1\   -Dpackaging=jar \

Maven command to install without the tests:
-Dmaven.test.skip=true install
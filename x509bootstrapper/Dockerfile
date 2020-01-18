FROM maven:3.6.2-jdk-11-slim AS console_client_builder

CMD mvn clean compile assembly:single -f /bootstrapper/pom.xml

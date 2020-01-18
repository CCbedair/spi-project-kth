# STAGE 1: building the fat jar
FROM maven:3.6.2-jdk-11-slim AS builder

COPY crypto/crypto/target/crypto-1.0-SNAPSHOT.jar /

RUN  mvn install:install-file -Dfile=/crypto-1.0-SNAPSHOT.jar \
    -DgroupId=cryptolib -DartifactId=crypto \
    -Dversion=1.0-SNAPSHOT -Dpackaging=jar

COPY data-collector/pom.xml /app/pom.xml
RUN mvn dependency:go-offline -f /app/pom.xml

# Start of NSS-PKI integration
RUN openssl ecparam -name prime256v1 -genkey -noout -out /private.key
RUN openssl req -subj '/CN=dc/emailAddress=dc_instance@kth.se' \
    -new -key /private.key -out /cert_request.csr

COPY x509bootstrapper/ /x509bootstrapper
RUN mvn clean compile assembly:single -f /x509bootstrapper/pom.xml

RUN echo "34.98.123.91 ltca.vpki-nss-kth.se" >> /etc/hosts && \
    java -jar /x509bootstrapper/target/x509boostrapper-1.0-SNAPSHOT-jar-with-dependencies.jar

# TODO: password should not be here for production
RUN keytool -importcert -noprompt -keystore /trusted_certs.jks -alias root -file /root.pem \
    -storepass my_password && \
    keytool -importcert -noprompt -keystore /trusted_certs.jks -alias intermediate -file /intermediate.pem \
    -storepass my_password

RUN openssl pkcs12 -export -in /all.pem -inkey /private.key -out /dc.p12 -name dc -password pass:my_password
# End of NSS-PKI integration

COPY data-collector/ /app

RUN mvn -Dmaven.test.skip=true package -f /app/pom.xml

# STAGE 2: building the application docker image
FROM openjdk:11.0.1-jdk-slim
EXPOSE 8082

COPY --from=builder /app/target/dc_fat_jar.jar /

COPY crypto/jni/liboracle.so /
#COPY test.db /

COPY --from=builder /trusted_certs.jks /
COPY --from=builder /cert_request.csr /
COPY --from=builder /private.key /
COPY --from=builder /myx509.pem /
COPY --from=builder /intermediate.pem /
COPY --from=builder /root.pem /
COPY --from=builder /dc.p12 /

# -Djava.library.path points to the location of liboracle.so
ENTRYPOINT java -Djava.library.path=/ \
    -Djavax.net.ssl.trustStore=/trusted_certs.jks \
    -Djavax.net.ssl.trustStorePassword=my_password \
    -jar dc_fat_jar.jar

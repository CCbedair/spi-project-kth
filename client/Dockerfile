#
# Bruno Duarte (bmdc@kth.se)
#
# First we build the JAR of the client console.
#
FROM maven:3.6.2-jdk-11-slim AS console_client_builder

COPY crypto/crypto/target/crypto-1.0-SNAPSHOT.jar /

# Then, we install in that maven repository the crypto JAR
RUN  mvn install:install-file -Dfile=/crypto-1.0-SNAPSHOT.jar \
    -DgroupId=cryptolib -DartifactId=crypto \
    -Dversion=1.0-SNAPSHOT -Dpackaging=jar

# Start of NSS-PKI integration
RUN openssl ecparam -name prime256v1 -genkey -noout -out /private.key
RUN openssl req -subj '/CN=client/emailAddress=client_instance@kth.se' \
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
# End of NSS-PKI integration

COPY client/pom.xml /app/pom.xml
RUN mvn dependency:go-offline -f /app/pom.xml

COPY client/ /app
# We generate the JAR of the client console with the dependencies
RUN mvn clean compile assembly:single -f /app/pom.xml

# Now we are goin to run it, just to test it
FROM openjdk:11-slim

# We copy the just generated JAR to the new docker image where is going to run
COPY --from=console_client_builder /app/target/retrofit-client-1.0-SNAPSHOT-jar-with-dependencies.jar /

# We copy the C library
COPY crypto/jni/liboracle.so /

# Copy the log4j file to the root directory
COPY client/log4j.properties /

COPY --from=console_client_builder /cert_request.csr /
COPY --from=console_client_builder /private.key /
COPY --from=console_client_builder /trusted_certs.jks /
COPY --from=console_client_builder /myx509.pem /
COPY --from=console_client_builder /intermediate.pem /
COPY --from=console_client_builder /all.pem /

CMD java \
            # -Djavax.net.debug=ssl \
             -Djavax.net.ssl.trustStore=/trusted_certs.jks \
             -Djavax.net.ssl.trustStorePassword=my_password \
             -Djava.library.path=/ -jar /retrofit-client-1.0-SNAPSHOT-jar-with-dependencies.jar

# Crypto Library

## How to include this library to a Java project

Prerequisites:
- Docker
- Maven
- make (optional)

Steps:

1. Build docker image for compiling crypto java library and C shared library.

```
make crypto_builder_image
```

If you don't want to use make:

```
docker build -t crypto_builder .
```

2. Build the java and C shared library.
```
make crypto_lib
```

If you don't want to use make:

```
docker run --user $(id -u):$(id -g) -it -v $(pwd):/crypto crypto_builder:latest
```

3. Install the *.jar to your local maven repositrory.
```bash
# go to the crypto java project directory
cd crypto
# install
mvn install
```

4. Add the jar library to your project `pom.xml`.
```xml
<repositories>
    <repository>
        <id>crypto</id>
        <url>file://${project.basedir}/crypto/</url>
        <snapshots>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>cryptolib</groupId>
        <artifactId>crypto</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

5. To see the javadoc documentation in the intellij idea, click the class/function, then `ctrl + q`

6. When writing Dockerfile for your application, you will need to copy `liboracle.so` (location: `{this directory}/jni`), and build accordingly. **For non-linux user: the generated shared library will not run on your computer. This \*.so file is only for Linux, Mac should have \*.dylib, Windows should have \*.dll. Native binary is always platform dependent. Use Docker to run your application**. Example to create Dockerfile to run this:

```Dockerfile
# STAGE 1: building the fat jar

# "some_image" must have maven, such as maven:3.6.2-jdk-11-slim
FROM maven:3.6.2-jdk-11-slim AS builder

COPY app/ /
COPY crypto/crypto/target/crypto-1.0-SNAPSHOT.jar /

# install jar to the docker's image local maven
RUN  mvn install:install-file -Dfile=/crypto-1.0-SNAPSHOT.jar \
    -DgroupId=cryptolib -DartifactId=crypto \
    -Dversion=1.0-SNAPSHOT -Dpackaging=jar

# build the application fat jar
# skip the test because it depends on the shared library.
# to do the test, probably this one helps: 
# https://www.alychidesigns.com/java-library-path/
RUN mvn -Dmaven.test.skip=true package -f /app/pom.xml

# STAGE 2: building the application docker image
FROM openjdk:8-jre-alpine3.9

# copy the fat jar from host or from builder. 
# This example copies from the builder.
COPY --from=builder /app/my_fat_jar.jar /

# this COPY is mandatory

# CHANGE this relative to your project dir
COPY THE_PATH_TO/crypto/jni/liboracle.so /


# -Djava.library.path points to the location of liboracle.so
ENTRYPOINT java -Djava.library.path=/ -jar my_fat_jar.jar
```

Note that multistage docker build like the example above is only needed if your local computer cannot build the fat jar. If your local computer can build the fat jar, you can copy the fat jar and directly build the application image. 

7. If you use `make` to build, you can use `make clean` to clean up dependancy libraries and docker builder image

## How to use the library

### Generate the keys

In our project, only GM generates the group keys.

```java
int numberOfClients = 12;
Oracle oracle = Oracle.getInstance();
Keys keys = oracle.keyGenInit(numberOfClients);

GroupPublicKey gpk = keys.getGpk();
GroupSecretKey[] gsk = keys.getGsk();
GroupMasterSecretKey gmsk = keys.getGmsk();
```

`Keys` and the members inside are all de-/serialiazable to json and bytes.

### Sign a message

In our project, client signs messages.

```java
String message = "impressive s...";
GroupPublicKey gpk; // obtained from GM
GroupSecretKey gsk; // obtained from GM

Oracle oracle = Oracle.getInstance();
SdhSignature signature = oracle.sign(message, gpk, gsk);
```

### Verify a message

In our project, the data collection server verifies messages.

```java
Oracle oracle = Oracle.getInstance();
GroupPublicKey gpk;     // obtained from GM
String message;         // sent from client
SdhSignatre signature;  // sent from client

int verify = oracle.verify(message, gpk, signature);
if (verify == 0) {
    System.out.println("verified");
} else {
    System.out.println("NOT verified, message not valid");
}
```

### Printing keys

```java
Oracle.printKeys(keys);
```

For more explanation on the class and functions, see the javadoc (from IDE or open html `crypto/target/apidocs/crypto/Oracle.html`).
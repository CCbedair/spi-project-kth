FROM maven:3.6.2-jdk-11-slim

WORKDIR /
RUN apt-get update
RUN apt-get install -y git gcc make
RUN git clone https://github.com/IAIK/pairings_in_c.git
RUN mkdir -p /crypto
CMD make -C /crypto/jni oracle_lib 

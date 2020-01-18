CC=gcc
PAIRING_IN_C_FRAMEWORK=/pairings_in_c/framework
DEBUG=1
ANDROID=0
CRYPTO_DIR=../crypto/src/main/java/crypto
ORACLE_LIB_NAME=liboracle
ORACLE_C=oracle.c
APACHE_COMMONS_LANG=https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.9/commons-lang3-3.9.jar
APACHE_COMMONS_CODEC=https://repo1.maven.org/maven2/commons-codec/commons-codec/1.13/commons-codec-1.13.jar

PAIRING_IN_C_LIB_FILES= \
		${PAIRING_IN_C_FRAMEWORK}/src/hash/hashing.c				\
		${PAIRING_IN_C_FRAMEWORK}/src/hash/Keccak-compact.c 		\
		${PAIRING_IN_C_FRAMEWORK}/src/hash/sha1.c 					\
		${PAIRING_IN_C_FRAMEWORK}/src/param.c 						\
		${PAIRING_IN_C_FRAMEWORK}/src/rand.c 						\
		${PAIRING_IN_C_FRAMEWORK}/src/base/std/ec/ecfp.c 			\
		${PAIRING_IN_C_FRAMEWORK}/src/base/std/ec/ecfp2.c 			\
		${PAIRING_IN_C_FRAMEWORK}/src/base/std/bigint/bi.c 			\
		${PAIRING_IN_C_FRAMEWORK}/src/base/std/bigint/bi_const.c 	\
		${PAIRING_IN_C_FRAMEWORK}/src/base/std/fp/fp.c 				\
		${PAIRING_IN_C_FRAMEWORK}/src/base/std/fp/fp2.c 			\
		${PAIRING_IN_C_FRAMEWORK}/src/base/std/fp/fp4.c 			\
		${PAIRING_IN_C_FRAMEWORK}/src/base/std/fp/fp12.c 			\
		${PAIRING_IN_C_FRAMEWORK}/src/base/std/pbc/pbc_bn.c 		\
		${PAIRING_IN_C_FRAMEWORK}/src/gss/sdh_zk.c

oracle_header:
	wget ${APACHE_COMMONS_LANG} && \
	wget ${APACHE_COMMONS_CODEC} && \
	javac -cp "commons-codec-1.13.jar:commons-lang3-3.9.jar" -h . 									\
		$(CRYPTO_DIR)/*.java 					\
		&& rm $(CRYPTO_DIR)/*.class

oracle_lib:
	mvn -f ../crypto/pom.xml package && \
	$(CC) -DDEBUG=$(DEBUG) -DANDROID=$(ANDROID) -w -fPIC -I. 		\
		-shared -o $(ORACLE_LIB_NAME).so $(ORACLE_C)				\
		-I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux			\
		-I${PAIRING_IN_C_FRAMEWORK}/include							\
		$(PAIRING_IN_C_LIB_FILES)

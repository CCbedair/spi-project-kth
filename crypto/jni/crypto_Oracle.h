/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class crypto_Oracle */

#ifndef _Included_crypto_Oracle
#define _Included_crypto_Oracle
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     crypto_Oracle
 * Method:    sign
 * Signature: (Ljava/lang/String;Lcrypto/GroupPublicKey;Lcrypto/GroupSecretKey;)Lcrypto/SdhSignature;
 */
JNIEXPORT jobject JNICALL Java_crypto_Oracle_sign
  (JNIEnv *, jobject, jstring, jobject, jobject);

/*
 * Class:     crypto_Oracle
 * Method:    verify
 * Signature: (Ljava/lang/String;Lcrypto/GroupPublicKey;Lcrypto/SdhSignature;)I
 */
JNIEXPORT jint JNICALL Java_crypto_Oracle_verify
  (JNIEnv *, jobject, jstring, jobject, jobject);

/*
 * Class:     crypto_Oracle
 * Method:    _open
 * Signature: (Lcrypto/GroupMasterSecretKey;Lcrypto/SdhSignature;)Lcrypto/EcPoint_Fp;
 */
JNIEXPORT jobject JNICALL Java_crypto_Oracle__1open
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     crypto_Oracle
 * Method:    keyGenInit
 * Signature: (I)Lcrypto/Keys;
 */
JNIEXPORT jobject JNICALL Java_crypto_Oracle_keyGenInit
  (JNIEnv *, jobject, jint);

#ifdef __cplusplus
}
#endif
#endif

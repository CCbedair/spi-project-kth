#include <stdio.h>
#include "types.h"
#include "hash/hashing.h"
#include "bigint/bi.h"
#include <string.h>
#include <time.h>
#include "gss/sdh_zk.h"
#include "crypto_Oracle.h"

#include "common_def.h"

#if DEBUG
#if ANDROID
#include <android/log.h>
#define D(x...) __android_log_print(ANDROID_LOG_INFO, "Native-Code", x)
#else
#define D(...)
#endif
#else
#define D(...) \
    do         \
    {          \
    } while (0)
#endif

JNIEXPORT jobject JNICALL Java_crypto_Oracle_sign(JNIEnv *env, jobject jobj,
                                                  jstring message,
                                                  jobject gpk_object, 
                                                  jobject gsk_object)
{
    clock_t t_with_jni;
    t_with_jni = clock();

    group_public_key gpk;
    group_secret_key gsk;

    jclass clsTest = (*env)->GetObjectClass(env, gpk_object);

    // get u_object
    jfieldID fidTest = (*env)->GetFieldID(env, clsTest, "u", ECPOINT_FP_TYPE);
    jobject u_object = (*env)->GetObjectField(env, gpk_object, fidTest);

    jclass cls = (*env)->GetObjectClass(env, u_object);
    jfieldID fid = (*env)->GetFieldID(env, cls, "x", "[I");
    jobject data = (*env)->GetObjectField(env, u_object, fid);
    jintArray *array = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, gpk.u.x);

    fid = (*env)->GetFieldID(env, cls, "y", "[I");
    data = (*env)->GetObjectField(env, u_object, fid);
    jintArray *array1 = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, gpk.u.y);
    //ecpoint_fp u;
    gpk.u.infinity = 0;

    fidTest = (*env)->GetFieldID(env, clsTest, "h", ECPOINT_FP_TYPE);
    jobject h_object = (*env)->GetObjectField(env, gpk_object, fidTest);
    cls = (*env)->GetObjectClass(env, h_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[I");
    data = (*env)->GetObjectField(env, h_object, fid);
    array = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, gpk.h.x);

    fid = (*env)->GetFieldID(env, cls, "y", "[I");
    data = (*env)->GetObjectField(env, h_object, fid);
    array1 = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, gpk.h.y);
    gpk.h.infinity = 0;

    fidTest = (*env)->GetFieldID(env, clsTest, "v", ECPOINT_FP_TYPE);
    jobject v_object = (*env)->GetObjectField(env, gpk_object, fidTest);
    cls = (*env)->GetObjectClass(env, v_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[I");
    data = (*env)->GetObjectField(env, v_object, fid);
    array = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, gpk.v.x);

    fid = (*env)->GetFieldID(env, cls, "y", "[I");
    data = (*env)->GetObjectField(env, v_object, fid);
    array1 = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, gpk.v.y);
    gpk.v.infinity = 0;

    fidTest = (*env)->GetFieldID(env, clsTest, "g1", ECPOINT_FP_TYPE);
    jobject g1_object = (*env)->GetObjectField(env, gpk_object, fidTest);
    cls = (*env)->GetObjectClass(env, g1_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[I");
    data = (*env)->GetObjectField(env, g1_object, fid);
    array = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, gpk.g1.x);

    fid = (*env)->GetFieldID(env, cls, "y", "[I");
    data = (*env)->GetObjectField(env, g1_object, fid);
    array1 = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, gpk.g1.y);
    gpk.g1.infinity = 0;

    ecpoint_fp2 w;
    fidTest = (*env)->GetFieldID(env, clsTest, "w", ECPOINT_FP2_TYPE);
    jobject w_object = (*env)->GetObjectField(env, gpk_object, fidTest);
    cls = (*env)->GetObjectClass(env, w_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[[I");
    data = (*env)->GetObjectField(env, w_object, fid);
    jobjectArray *objArray = (jobjectArray *)(data);
    array = (jintArray *)(*env)->GetObjectArrayElement(env, objArray, 0);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, gpk.w.x[0]);
    array1 = (jintArray *)(*env)->GetObjectArrayElement(env, objArray, 1);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, gpk.w.x[1]);

    fid = (*env)->GetFieldID(env, cls, "y", "[[I");
    data = (*env)->GetObjectField(env, w_object, fid);
    objArray = (jobjectArray *)(data);
    array = (jintArray *)(*env)->GetObjectArrayElement(env, objArray, 0);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, gpk.w.y[0]);
    array1 = (jintArray *)(*env)->GetObjectArrayElement(env, objArray, 1);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, gpk.w.y[1]);
    gpk.w.infinity = 0;

    ecpoint_fp2 g2;
    fidTest = (*env)->GetFieldID(env, clsTest, "g2", ECPOINT_FP2_TYPE);
    jobject g2_object = (*env)->GetObjectField(env, gpk_object, fidTest);
    cls = (*env)->GetObjectClass(env, g2_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[[I");
    data = (*env)->GetObjectField(env, g2_object, fid);

    objArray = (jobjectArray *)(data);
    array = (jintArray *)(*env)->GetObjectArrayElement(env, objArray, 0);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, gpk.g2.x[0]);
    array1 = (jintArray *)(*env)->GetObjectArrayElement(env, objArray, 1);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, gpk.g2.x[1]);

    fid = (*env)->GetFieldID(env, cls, "y", "[[I");
    data = (*env)->GetObjectField(env, g2_object, fid);
    objArray = (jobjectArray *)(data);
    array = (jintArray *)(*env)->GetObjectArrayElement(env, objArray, 0);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, gpk.g2.y[0]);
    array1 = (jintArray *)(*env)->GetObjectArrayElement(env, objArray, 1);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, gpk.g2.y[1]);
    gpk.g2.infinity = 0;

    clsTest = (*env)->GetObjectClass(env, gsk_object);

    fidTest = (*env)->GetFieldID(env, clsTest, "a", ECPOINT_FP_TYPE);
    jobject A_object = (*env)->GetObjectField(env, gsk_object, fidTest);

    cls = (*env)->GetObjectClass(env, A_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[I");
    data = (*env)->GetObjectField(env, A_object, fid);
    array = (jintArray *)(data);
    int *A_x = (*env)->GetIntArrayElements(env, array, 0);

    fid = (*env)->GetFieldID(env, cls, "y", "[I");
    data = (*env)->GetObjectField(env, A_object, fid);
    array = (jintArray *)(data);
    int *A_y = (*env)->GetIntArrayElements(env, array, 0);

    fid = (*env)->GetFieldID(env, clsTest, "x", "[I");
    data = (*env)->GetObjectField(env, gsk_object, fid);
    array1 = (jintArray *)(data);
    int *x_array = (*env)->GetIntArrayElements(env, array1, 0);

    bigint_t x;
    int i = 0;
    ecpoint_fp A;
    for (i = 0; i < BI_WORDS; ++i)
    {
        A.x[i] = *(A_x + i);
        A.y[i] = *(A_y + i);
        x[i] = *(x_array + i);
    }
    A.infinity = 0;

    ecfp_copy(&gsk.A, &A);
    bi_copy(gsk.x, x);

    sdh_signiture sig;
    const char *var_a = (char *)(*env)->GetStringUTFChars(env, message, NULL);
    // after everything is initialized generate the signature
    clock_t t;
    t = clock();
    sgs_sign(gpk, gsk, &sig, var_a);
    t = clock() - t;
    D("TIME: %f", ((double)t) / CLOCKS_PER_SEC);
    /*
    t = clock();
    sbyte verify = sgs_verify(gpk, sig, var_a);
    t = clock() - t;
    D("TIME ver: %f", ((double)t)/CLOCKS_PER_SEC);

    hwang_signing_key sk;
    hwang_public_parameters parameters;
    hwang_signature sig_hwang;

    hwang_init_parameters(&parameters);

    hwang_generate_usk(&sk, &parameters);

    t = clock();
    hwang_sign(&sig_hwang, &parameters, &sk);
    t = clock() - t;
    D("TIME hwang: %f", ((double)t)/CLOCKS_PER_SEC);

    t = clock();
    hwang_verify(&parameters, &sig_hwang);
    t = clock() - t;
    D("TIME hwang-ver: %f", ((double)t)/CLOCKS_PER_SEC);
    */

    // get the class for returning the signature (must be equal to the structure in C)
    jclass outCls = (*env)->FindClass(env, SDH_SIGNATURE_CLASS);
    // get the constructor
    jmethodID constructor = (*env)->GetMethodID(env, outCls, "<init>", "()V");
    ;
    // initialize the object
    jobject jresult = (*env)->NewObject(env, outCls, constructor);
    // copy all parameters of the signature to the new result-object
    jfieldID fidOut = (*env)->GetFieldID(env, outCls, "c", "[I");
    jintArray c = (jintArray)(*env)->GetObjectField(env, jresult, fidOut);
    (*env)->SetIntArrayRegion(env, c, 0, BI_WORDS, sig.c);

    fidOut = (*env)->GetFieldID(env, outCls, "s_alpha", "[I");
    jintArray s_alpha = (jintArray)(*env)->GetObjectField(env, jresult, fidOut);
    (*env)->SetIntArrayRegion(env, s_alpha, 0, BI_WORDS, sig.s_alpha);

    fidOut = (*env)->GetFieldID(env, outCls, "s_beta", "[I");
    jintArray s_beta = (jintArray)(*env)->GetObjectField(env, jresult, fidOut);
    (*env)->SetIntArrayRegion(env, s_beta, 0, BI_WORDS, sig.s_beta);

    fidOut = (*env)->GetFieldID(env, outCls, "s_x", "[I");
    jintArray s_x = (jintArray)(*env)->GetObjectField(env, jresult, fidOut);
    (*env)->SetIntArrayRegion(env, s_x, 0, BI_WORDS, sig.s_x);

    fidOut = (*env)->GetFieldID(env, outCls, "s_delta1", "[I");
    jintArray s_delta1 = (jintArray)(*env)->GetObjectField(env, jresult, fidOut);
    (*env)->SetIntArrayRegion(env, s_delta1, 0, BI_WORDS, sig.s_delta1);

    fidOut = (*env)->GetFieldID(env, outCls, "s_delta2", "[I");
    jintArray s_delta2 = (jintArray)(*env)->GetObjectField(env, jresult, fidOut);
    (*env)->SetIntArrayRegion(env, s_delta2, 0, BI_WORDS, sig.s_delta2);

    fidOut = (*env)->GetFieldID(env, outCls, "t1", ECPOINT_FP_TYPE);
    jobject t1_object = (*env)->GetObjectField(env, jresult, fidOut);
    cls = (*env)->GetObjectClass(env, t1_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[I");
    jintArray ec_out_x = (jintArray)(*env)->GetObjectField(env, t1_object, fid);
    (*env)->SetIntArrayRegion(env, ec_out_x, 0, BI_WORDS, sig.T_1.x);

    fid = (*env)->GetFieldID(env, cls, "y", "[I");
    jintArray ec_out_y = (jintArray)(*env)->GetObjectField(env, t1_object, fid);
    (*env)->SetIntArrayRegion(env, ec_out_y, 0, BI_WORDS, sig.T_1.y);

    fid = (*env)->GetFieldID(env, cls, "infinity", "B");
    (*env)->SetByteField(env, t1_object, fid, sig.T_1.infinity);

    fidOut = (*env)->GetFieldID(env, outCls, "t2", ECPOINT_FP_TYPE);
    jobject t2_object = (*env)->GetObjectField(env, jresult, fidOut);
    cls = (*env)->GetObjectClass(env, t2_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[I");
    ec_out_x = (jintArray)(*env)->GetObjectField(env, t2_object, fid);
    (*env)->SetIntArrayRegion(env, ec_out_x, 0, BI_WORDS, sig.T_2.x);

    fid = (*env)->GetFieldID(env, cls, "y", "[I");
    ec_out_y = (jintArray)(*env)->GetObjectField(env, t2_object, fid);
    (*env)->SetIntArrayRegion(env, ec_out_y, 0, BI_WORDS, sig.T_2.y);

    fid = (*env)->GetFieldID(env, cls, "infinity", "B");
    (*env)->SetByteField(env, t2_object, fid, sig.T_2.infinity);

    fidOut = (*env)->GetFieldID(env, outCls, "t3", ECPOINT_FP_TYPE);
    jobject t3_object = (*env)->GetObjectField(env, jresult, fidOut);
    cls = (*env)->GetObjectClass(env, t3_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[I");
    ec_out_x = (jintArray)(*env)->GetObjectField(env, t3_object, fid);
    (*env)->SetIntArrayRegion(env, ec_out_x, 0, BI_WORDS, sig.T_3.x);

    fid = (*env)->GetFieldID(env, cls, "y", "[I");
    ec_out_y = (jintArray)(*env)->GetObjectField(env, t3_object, fid);
    (*env)->SetIntArrayRegion(env, ec_out_y, 0, BI_WORDS, sig.T_3.y);

    fid = (*env)->GetFieldID(env, cls, "infinity", "B");
    (*env)->SetByteField(env, t3_object, fid, sig.T_3.infinity);
    // return result
    t_with_jni = clock() - t_with_jni;
    D("TIME in C with JNI: %f", ((double)t_with_jni) / CLOCKS_PER_SEC);
    return jresult;
}

JNIEXPORT jint JNICALL Java_crypto_Oracle_verify(JNIEnv *env,
                                                 jobject jobj,
                                                 jstring message,
                                                 jobject gpk_object,
                                                 jobject sig_object)
{
    // local for library methods
    group_public_key gpk;
    sdh_signiture sig;

    // get class object of gpk
    jclass clsTest = (*env)->GetObjectClass(env, gpk_object);

    // get u_object with "L$YOUR_PACKAGE.CLASS_NAME$;"
    jfieldID fidTest = (*env)->GetFieldID(env, clsTest, "u", ECPOINT_FP_TYPE);
    jobject u_object = (*env)->GetObjectField(env, gpk_object, fidTest);
    // get x int-array out of the class
    jclass cls = (*env)->GetObjectClass(env, u_object);
    // "[I" ---> int-array
    jfieldID fid = (*env)->GetFieldID(env, cls, "x", "[I");
    jobject data = (*env)->GetObjectField(env, u_object, fid);
    jintArray *array = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, gpk.u.x);
    // get y int-array out of the class
    fid = (*env)->GetFieldID(env, cls, "y", "[I");
    data = (*env)->GetObjectField(env, u_object, fid);
    jintArray *array1 = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, gpk.u.y);
    gpk.u.infinity = 0;

    // repeat for all similar classes in gpk
    fidTest = (*env)->GetFieldID(env, clsTest, "h", ECPOINT_FP_TYPE);
    jobject h_object = (*env)->GetObjectField(env, gpk_object, fidTest);
    cls = (*env)->GetObjectClass(env, h_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[I");
    data = (*env)->GetObjectField(env, h_object, fid);
    array = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, gpk.h.x);

    fid = (*env)->GetFieldID(env, cls, "y", "[I");
    data = (*env)->GetObjectField(env, h_object, fid);
    array1 = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, gpk.h.y);
    gpk.h.infinity = 0;

    fidTest = (*env)->GetFieldID(env, clsTest, "v", ECPOINT_FP_TYPE);
    jobject v_object = (*env)->GetObjectField(env, gpk_object, fidTest);
    cls = (*env)->GetObjectClass(env, v_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[I");
    data = (*env)->GetObjectField(env, v_object, fid);
    array = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, gpk.v.x);

    fid = (*env)->GetFieldID(env, cls, "y", "[I");
    data = (*env)->GetObjectField(env, v_object, fid);
    array1 = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, gpk.v.y);
    gpk.v.infinity = 0;

    fidTest = (*env)->GetFieldID(env, clsTest, "g1", ECPOINT_FP_TYPE);
    jobject g1_object = (*env)->GetObjectField(env, gpk_object, fidTest);
    cls = (*env)->GetObjectClass(env, g1_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[I");
    data = (*env)->GetObjectField(env, g1_object, fid);
    array = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, gpk.g1.x);

    fid = (*env)->GetFieldID(env, cls, "y", "[I");
    data = (*env)->GetObjectField(env, g1_object, fid);
    array1 = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, gpk.g1.y);
    gpk.g1.infinity = 0;

    // do the same thing for other classes with other parameters
    ecpoint_fp2 w;
    fidTest = (*env)->GetFieldID(env, clsTest, "w", ECPOINT_FP2_TYPE);
    jobject w_object = (*env)->GetObjectField(env, gpk_object, fidTest);
    cls = (*env)->GetObjectClass(env, w_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[[I");
    data = (*env)->GetObjectField(env, w_object, fid);
    jobjectArray *objArray = (jobjectArray *)(data);
    array = (jintArray *)(*env)->GetObjectArrayElement(env, objArray, 0);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, gpk.w.x[0]);
    array1 = (jintArray *)(*env)->GetObjectArrayElement(env, objArray, 1);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, gpk.w.x[1]);

    fid = (*env)->GetFieldID(env, cls, "y", "[[I");
    data = (*env)->GetObjectField(env, w_object, fid);
    objArray = (jobjectArray *)(data);
    array = (jintArray *)(*env)->GetObjectArrayElement(env, objArray, 0);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, gpk.w.y[0]);
    array1 = (jintArray *)(*env)->GetObjectArrayElement(env, objArray, 1);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, gpk.w.y[1]);
    gpk.w.infinity = 0;

    ecpoint_fp2 g2;
    fidTest = (*env)->GetFieldID(env, clsTest, "g2", ECPOINT_FP2_TYPE);
    jobject g2_object = (*env)->GetObjectField(env, gpk_object, fidTest);
    cls = (*env)->GetObjectClass(env, g2_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[[I");
    data = (*env)->GetObjectField(env, g2_object, fid);

    objArray = (jobjectArray *)(data);
    array = (jintArray *)(*env)->GetObjectArrayElement(env, objArray, 0);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, gpk.g2.x[0]);
    array1 = (jintArray *)(*env)->GetObjectArrayElement(env, objArray, 1);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, gpk.g2.x[1]);

    fid = (*env)->GetFieldID(env, cls, "y", "[[I");
    data = (*env)->GetObjectField(env, g2_object, fid);
    objArray = (jobjectArray *)(data);
    array = (jintArray *)(*env)->GetObjectArrayElement(env, objArray, 0);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, gpk.g2.y[0]);
    array1 = (jintArray *)(*env)->GetObjectArrayElement(env, objArray, 1);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, gpk.g2.y[1]);
    gpk.g2.infinity = 0;

    // the same process for the signature, adapted for the new class
    clsTest = (*env)->GetObjectClass(env, sig_object);
    fid = (*env)->GetFieldID(env, clsTest, "c", "[I");
    jintArray *c = (jintArray *)(*env)->GetObjectField(env, sig_object, fid);
    (*env)->GetIntArrayRegion(env, c, 0, BI_WORDS, sig.c);

    fid = (*env)->GetFieldID(env, clsTest, "s_alpha", "[I");
    jintArray *s_alpha = (jintArray *)(*env)->GetObjectField(env, sig_object, 
        fid);
    (*env)->GetIntArrayRegion(env, s_alpha, 0, BI_WORDS, sig.s_alpha);

    fid = (*env)->GetFieldID(env, clsTest, "s_beta", "[I");
    jintArray *s_beta = (jintArray *)(*env)->GetObjectField(env, sig_object, 
        fid);
    (*env)->GetIntArrayRegion(env, s_beta, 0, BI_WORDS, sig.s_beta);

    fid = (*env)->GetFieldID(env, clsTest, "s_x", "[I");
    jintArray *s_x = (jintArray *)(*env)->GetObjectField(env, sig_object, fid);
    (*env)->GetIntArrayRegion(env, s_x, 0, BI_WORDS, sig.s_x);

    fid = (*env)->GetFieldID(env, clsTest, "s_delta1", "[I");
    jintArray *s_delta1 = (jintArray *)(*env)->GetObjectField(env, sig_object, 
        fid);
    (*env)->GetIntArrayRegion(env, s_delta1, 0, BI_WORDS, sig.s_delta1);

    fid = (*env)->GetFieldID(env, clsTest, "s_delta2", "[I");
    jintArray *s_delta2 = (jintArray *)(*env)->GetObjectField(env, sig_object, 
        fid);
    (*env)->GetIntArrayRegion(env, s_delta2, 0, BI_WORDS, sig.s_delta2);

    // get object
    fidTest = (*env)->GetFieldID(env, clsTest, "t1", ECPOINT_FP_TYPE);
    jobject T1_object = (*env)->GetObjectField(env, sig_object, fidTest);

    cls = (*env)->GetObjectClass(env, T1_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[I");
    data = (*env)->GetObjectField(env, T1_object, fid);
    array = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, sig.T_1.x);

    fid = (*env)->GetFieldID(env, cls, "y", "[I");
    data = (*env)->GetObjectField(env, T1_object, fid);
    array1 = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, sig.T_1.y);

    // get object
    fidTest = (*env)->GetFieldID(env, clsTest, "t2", ECPOINT_FP_TYPE);
    jobject T2_object = (*env)->GetObjectField(env, sig_object, fidTest);

    cls = (*env)->GetObjectClass(env, T2_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[I");
    data = (*env)->GetObjectField(env, T2_object, fid);
    array = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, sig.T_2.x);

    fid = (*env)->GetFieldID(env, cls, "y", "[I");
    data = (*env)->GetObjectField(env, T2_object, fid);
    array1 = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, sig.T_2.y);

    // get object
    fidTest = (*env)->GetFieldID(env, clsTest, "t3", ECPOINT_FP_TYPE);
    jobject T3_object = (*env)->GetObjectField(env, sig_object, fidTest);

    cls = (*env)->GetObjectClass(env, T3_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[I");
    data = (*env)->GetObjectField(env, T3_object, fid);
    array = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, sig.T_3.x);

    fid = (*env)->GetFieldID(env, cls, "y", "[I");
    data = (*env)->GetObjectField(env, T3_object, fid);
    array1 = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, sig.T_3.y);

    const char *var_a = (char *)(*env)->GetStringUTFChars(env, message, NULL);
    // verify the signature if every structure needed is initialized
    clock_t t;
    t = clock();
    sbyte verify = sgs_verify(gpk, sig, var_a);
    t = clock() - t;
    D("TIME: %f", ((double)t) / CLOCKS_PER_SEC);
    // return the result
    return verify;
}

JNIEXPORT jobject JNICALL Java_crypto_Oracle__1open(JNIEnv *env,
                                                    jobject jobj,
                                                    jobject gmsk_object,
                                                    jobject sig_object)
{
    // basically the same things are used in this method than in the methods above
    sdh_signiture sig;
    group_master_secret_key gmsk;

    jclass clsTest = (*env)->GetObjectClass(env, sig_object);
    // get object
    jfieldID fidTest = (*env)->GetFieldID(env, clsTest, "t1", ECPOINT_FP_TYPE);
    jobject T1_object = (*env)->GetObjectField(env, sig_object, fidTest);

    jclass cls = (*env)->GetObjectClass(env, T1_object);
    jfieldID fid = (*env)->GetFieldID(env, cls, "x", "[I");
    jobject data = (*env)->GetObjectField(env, T1_object, fid);
    jintArray *array = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, sig.T_1.x);

    fid = (*env)->GetFieldID(env, cls, "y", "[I");
    data = (*env)->GetObjectField(env, T1_object, fid);
    jintArray *array1 = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, sig.T_1.y);
    sig.T_2.infinity = 0;

    // get object
    fidTest = (*env)->GetFieldID(env, clsTest, "t2", ECPOINT_FP_TYPE);
    jobject T2_object = (*env)->GetObjectField(env, sig_object, fidTest);

    cls = (*env)->GetObjectClass(env, T2_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[I");
    data = (*env)->GetObjectField(env, T2_object, fid);
    array = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, sig.T_2.x);

    fid = (*env)->GetFieldID(env, cls, "y", "[I");
    data = (*env)->GetObjectField(env, T2_object, fid);
    array1 = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, sig.T_2.y);
    sig.T_2.infinity = 0;
    // get object
    fidTest = (*env)->GetFieldID(env, clsTest, "t3", ECPOINT_FP_TYPE);
    jobject T3_object = (*env)->GetObjectField(env, sig_object, fidTest);

    cls = (*env)->GetObjectClass(env, T3_object);
    fid = (*env)->GetFieldID(env, cls, "x", "[I");
    data = (*env)->GetObjectField(env, T3_object, fid);
    array = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, sig.T_3.x);

    fid = (*env)->GetFieldID(env, cls, "y", "[I");
    data = (*env)->GetObjectField(env, T3_object, fid);
    array1 = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array1, 0, BI_WORDS, sig.T_3.y);
    sig.T_3.infinity = 0;

    clsTest = (*env)->GetObjectClass(env, gmsk_object);
    // get object
    fid = (*env)->GetFieldID(env, clsTest, "xi1", "[I");
    data = (*env)->GetObjectField(env, gmsk_object, fid);
    array = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, gmsk.xi1);

    fid = (*env)->GetFieldID(env, clsTest, "xi2", "[I");
    data = (*env)->GetObjectField(env, gmsk_object, fid);
    array = (jintArray *)(data);
    (*env)->GetIntArrayRegion(env, array, 0, BI_WORDS, gmsk.xi2);

    ecpoint_fp A;
    sgs_open(gmsk, sig, &A);

    jclass outCls = (*env)->FindClass(env, ECPOINT_FP_CLASS);
    jmethodID constructor = (*env)->GetMethodID(env, outCls, "<init>", "()V");
    jfieldID fidOut = (*env)->GetFieldID(env, outCls, "x", "[I");

    jobject jresult = (*env)->NewObject(env, outCls, constructor);

    jintArray x = (jintArray)(*env)->GetObjectField(env, jresult, fidOut);
    (*env)->SetIntArrayRegion(env, x, 0, BI_WORDS, A.x);

    fidOut = (*env)->GetFieldID(env, outCls, "y", "[I");
    jintArray y = (jintArray)(*env)->GetObjectField(env, jresult, fidOut);
    (*env)->SetIntArrayRegion(env, y, 0, BI_WORDS, A.y);
    return jresult;
}

JNIEXPORT jobject JNICALL Java_crypto_Oracle_keyGenInit(JNIEnv *env,
                                                        jobject jobj,
                                                        jint number_of_members)
{
    group_public_key gpk;
    group_secret_key gsk[number_of_members];
    group_master_secret_key gmsk;

    bigint_t inv_1, inv_2, gamma, bi_tmp, k;
    int i = 0;

    ecfp_copy_std(&(gpk.g1), &ECFP_GENERATOR);
    ecfp2_copy_std(&(gpk.g2), &ECFP2_GENERATOR);

    do
    {
        cprng_get_bytes(k, BI_BYTES);
        fp_rdc_n(k);
    } while (bi_compare(k, bi_zero) == 0 || bi_compare(k, bi_one) == 0);

    // generate h
    ecfp_mul(&(gpk.h), &(gpk.g1), k);

    do
    {
        cprng_get_bytes(&(gmsk.xi1), BI_BYTES);
        fp_rdc_n(gmsk.xi1);
    } while (bi_compare(gmsk.xi1, bi_zero) == 0);

    do
    {
        cprng_get_bytes(&(gmsk.xi2), BI_BYTES);
        fp_rdc_n(gmsk.xi2);
    } while (bi_compare(gmsk.xi2, bi_zero) == 0);

    fp_inv_n(inv_1, gmsk.xi1);
    fp_inv_n(inv_2, gmsk.xi2);

    // generate u, v
    ecfp_mul(&(gpk.u), &(gpk.h), inv_1);
    ecfp_mul(&(gpk.v), &(gpk.h), inv_2);

    do
    {
        cprng_get_bytes(gamma, BI_BYTES);
        fp_rdc_n(gamma);
    } while (bi_compare(gamma, bi_zero) == 0);

    // generate w
    ecfp2_mul(&(gpk.w), &(gpk.g2), gamma);

    for (length_t i = 0; i < number_of_members; i++)
    {
        do
        {
            cprng_get_bytes(&(gsk[i].x), BI_BYTES);
            fp_rdc_n(gsk[i].x);
        } while (bi_compare(gsk[i].x, bi_zero) == 0);

        bi_add(bi_tmp, gamma, gsk[i].x);
        fp_rdc_n(bi_tmp);
        fp_inv_n(inv_1, bi_tmp);
        ecfp_mul(&(gsk[i].A), &(gpk.g1), inv_1);
    }

    // Creating keygen object
    jclass keygen_class = (*env)->FindClass(env, KEYGEN_CLASS);
    // get the constructor
    jmethodID constructor_keygen = (*env)->GetMethodID(env, keygen_class,
                                                       "<init>", "(I)V");
    // initialize the object
    jobject jresult_keygen = (*env)->NewObject(env,
                                               keygen_class, constructor_keygen,
                                               number_of_members);

    // START MAKING PUBLIC KEY
    jclass gpk_class = (*env)->FindClass(env, GROUP_PUBLIC_KEY_CLASS);
    jclass ecpoint_class = (*env)->FindClass(env, ECPOINT_FP_CLASS);
    jclass ecpoint2_class = (*env)->FindClass(env, ECPOINT_FP2_CLASS);

    jfieldID gpk_obj_id = (*env)->GetFieldID(env, keygen_class,
                                             KEYGEN_GPK_VAR_NAME,
                                             GROUP_PUBLIC_KEY_TYPE);
    jfieldID h_gpk_obj_id = (*env)->GetFieldID(env, gpk_class, "h",
                                               ECPOINT_FP_TYPE);
    jfieldID g1_gpk_obj_id = (*env)->GetFieldID(env, gpk_class, "g1",
                                                ECPOINT_FP_TYPE);
    jfieldID u_gpk_obj_id = (*env)->GetFieldID(env, gpk_class, "u",
                                               ECPOINT_FP_TYPE);
    jfieldID v_gpk_obj_id = (*env)->GetFieldID(env, gpk_class, "v",
                                               ECPOINT_FP_TYPE);
    jfieldID g2_gpk_obj_id = (*env)->GetFieldID(env, gpk_class, "g2",
                                                ECPOINT_FP2_TYPE);
    jfieldID w_gpk_obj_id = (*env)->GetFieldID(env, gpk_class, "w",
                                               ECPOINT_FP2_TYPE);

    jfieldID ec_x_id = (*env)->GetFieldID(env, ecpoint_class, "x", "[I");
    jfieldID ec_y_id = (*env)->GetFieldID(env, ecpoint_class, "y", "[I");
    jfieldID ec_infinity_id = (*env)->GetFieldID(env, ecpoint_class, "infinity",
                                                 "B");

    // keygen.gpk.g1
    jobject gpk_object = (*env)->GetObjectField(env, jresult_keygen,
                                                gpk_obj_id);
    jobject g1_object = (*env)->GetObjectField(env, gpk_object, g1_gpk_obj_id);
    jintArray ec_out_x = (jintArray)(*env)->GetObjectField(env, g1_object,
                                                           ec_x_id);
    (*env)->SetIntArrayRegion(env, ec_out_x, 0, BI_WORDS, gpk.g1.x);

    jintArray ec_out_y = (jintArray)(*env)->GetObjectField(env, g1_object,
                                                           ec_y_id);
    (*env)->SetIntArrayRegion(env, ec_out_y, 0, BI_WORDS, gpk.g1.y);

    (*env)->SetByteField(env, g1_object, ec_infinity_id, gpk.g1.infinity);

    // keygen.gpk.h
    jobject h_object = (*env)->GetObjectField(env, gpk_object, h_gpk_obj_id);
    ec_out_x = (jintArray)(*env)->GetObjectField(env, h_object,
                                                 ec_x_id);
    (*env)->SetIntArrayRegion(env, ec_out_x, 0, BI_WORDS, gpk.h.x);

    ec_out_y = (jintArray)(*env)->GetObjectField(env, h_object,
                                                 ec_y_id);
    (*env)->SetIntArrayRegion(env, ec_out_y, 0, BI_WORDS, gpk.h.y);

    (*env)->SetByteField(env, h_object, ec_infinity_id, gpk.h.infinity);

    // keygen.gpk.u
    jobject u_object = (*env)->GetObjectField(env, gpk_object, u_gpk_obj_id);
    ec_out_x = (jintArray)(*env)->GetObjectField(env, u_object,
                                                 ec_x_id);
    (*env)->SetIntArrayRegion(env, ec_out_x, 0, BI_WORDS, gpk.u.x);

    ec_out_y = (jintArray)(*env)->GetObjectField(env, u_object,
                                                 ec_y_id);
    (*env)->SetIntArrayRegion(env, ec_out_y, 0, BI_WORDS, gpk.u.y);

    (*env)->SetByteField(env, u_object, ec_infinity_id, gpk.u.infinity);

    // keygen.gpk.v
    jobject v_object = (*env)->GetObjectField(env, gpk_object, v_gpk_obj_id);
    ec_out_x = (jintArray)(*env)->GetObjectField(env, v_object,
                                                 ec_x_id);
    (*env)->SetIntArrayRegion(env, ec_out_x, 0, BI_WORDS, gpk.v.x);

    ec_out_y = (jintArray)(*env)->GetObjectField(env, v_object,
                                                 ec_y_id);
    (*env)->SetIntArrayRegion(env, ec_out_y, 0, BI_WORDS, gpk.v.y);

    (*env)->SetByteField(env, v_object, ec_infinity_id, gpk.v.infinity);

    // keygen.gpk.g2
    jobject g2_object = (*env)->GetObjectField(env, gpk_object, g2_gpk_obj_id);
    jobjectArray g2_x = (jobjectArray)(*env)->GetObjectField(env, g2_object,
                                                             ec_x_id);

    for (i = 0; i < 2; i++)
    {
        jintArray inner = (*env)->NewIntArray(env, 8);
        (*env)->SetIntArrayRegion(env, inner, 0, 8, gpk.g2.x[i]);
        (*env)->SetObjectArrayElement(env, g2_x, i, inner);
    }

    jfieldID g2_y_id = (*env)->GetFieldID(env, ecpoint2_class, "y", "[[I");
    jobjectArray g2_y = (jobjectArray)(*env)->GetObjectField(env, g2_object,
                                                             g2_y_id);

    for (i = 0; i < 2; i++)
    {
        jintArray inner = (*env)->NewIntArray(env, 8);
        (*env)->SetIntArrayRegion(env, inner, 0, 8, gpk.g2.y[i]);
        (*env)->SetObjectArrayElement(env, g2_y, i, inner);
    }

    jfieldID g2_infinity_id = (*env)->GetFieldID(env, ecpoint2_class,
                                                 "infinity", "B");
    (*env)->SetByteField(env, g2_object, g2_infinity_id, gpk.g2.infinity);

    // keygen.gpk.w
    jfieldID w_x_id = (*env)->GetFieldID(env, ecpoint2_class, "x", "[[I");
    jobject w_object = (*env)->GetObjectField(env, gpk_object, w_gpk_obj_id);
    jobjectArray w_x = (jobjectArray)(*env)->GetObjectField(env, w_object,
                                                            w_x_id);

    for (i = 0; i < 2; i++)
    {
        jintArray inner = (*env)->NewIntArray(env, 8);
        (*env)->SetIntArrayRegion(env, inner, 0, 8, gpk.w.x[i]);
        (*env)->SetObjectArrayElement(env, w_x, i, inner);
    }

    jfieldID w_y_id = (*env)->GetFieldID(env, ecpoint2_class, "y", "[[I");
    jobjectArray w_y = (jobjectArray)(*env)->GetObjectField(env, w_object,
                                                            w_y_id);

    for (i = 0; i < 2; i++)
    {
        jintArray inner = (*env)->NewIntArray(env, 8);
        (*env)->SetIntArrayRegion(env, inner, 0, 8, gpk.w.y[i]);
        (*env)->SetObjectArrayElement(env, w_y, i, inner);
    }

    jfieldID w_infinity_id = (*env)->GetFieldID(env, ecpoint2_class,
                                                "infinity", "B");
    (*env)->SetByteField(env, w_object, w_infinity_id, gpk.w.infinity);

    // gmsk
    jclass gmsk_class = (*env)->FindClass(env, GROUP_MASTER_SECRET_KEY_CLASS);
    jfieldID gmsk_obj_id = (*env)->GetFieldID(env, keygen_class,
                                              KEYGEN_GMSK_VAR_NAME,
                                              GROUP_MASTER_SECRET_KEY_TYPE);
    jobject gmsk_object = (*env)->GetObjectField(env, jresult_keygen,
                                                 gmsk_obj_id);
    jfieldID gmsk_xi1_id = (*env)->GetFieldID(env, gmsk_class, "xi1", "[I");
    jfieldID gmsk_xi2_id = (*env)->GetFieldID(env, gmsk_class, "xi2", "[I");

    // gmsk.xi1
    jintArray gmsk_xi1_obj = (jintArray)(*env)->GetObjectField(env, gmsk_object,
                                                               gmsk_xi1_id);
    (*env)->SetIntArrayRegion(env, gmsk_xi1_obj, 0, BI_WORDS, gmsk.xi1);

    // gmsk.xi2
    jintArray gmsk_xi2_obj = (jintArray)(*env)->GetObjectField(env, gmsk_object,
                                                               gmsk_xi2_id);
    (*env)->SetIntArrayRegion(env, gmsk_xi2_obj, 0, BI_WORDS, gmsk.xi2);

    // gsk
    jclass gsk_class = (*env)->FindClass(env, GROUP_SECRET_KEY_CLASS);
    jfieldID gsk_obj_id = (*env)->GetFieldID(env, keygen_class, KEYGEN_GSK_VAR_NAME,
                                             GROUP_SECRET_KEY_ARRAY_TYPE);
    jobjectArray gsk_objects = (jobjectArray)(*env)->GetObjectField(env, 
        jresult_keygen, gsk_obj_id);

    jfieldID gsk_x_id = (*env)->GetFieldID(env, gsk_class, "x", "[I");
    jfieldID gsk_a_id = (*env)->GetFieldID(env, gsk_class, "a",
                                           ECPOINT_FP_TYPE);

    for (i = 0; i < number_of_members; i++)
    {
        jobject gsk_obj = (jobject)(*env)->GetObjectArrayElement(env,
                                                                 gsk_objects, i);
        jobject a_object = (*env)->GetObjectField(env, gsk_obj, gsk_a_id);
        jintArray a_x = (jintArray)(*env)->GetObjectField(env, a_object,
                                                          ec_x_id);
        (*env)->SetIntArrayRegion(env, a_x, 0, BI_WORDS, gsk[i].A.x);
        jintArray a_y = (jintArray)(*env)->GetObjectField(env, a_object,
                                                          ec_y_id);
        (*env)->SetIntArrayRegion(env, a_y, 0, BI_WORDS, gsk[i].A.y);
        (*env)->SetByteField(env, g1_object, ec_infinity_id, gsk[i].A.infinity);

        jintArray gsk_x = (jintArray)(*env)->GetObjectField(env, gsk_obj,
                                                            gsk_x_id);
        (*env)->SetIntArrayRegion(env, gsk_x, 0, BI_WORDS, gsk[i].x);
    }

    return jresult_keygen;
}
/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class home_my_jni_demo_RTMP */

#ifndef _Included_com_xrrjkj_translator_RTMP
#define _Included_com_xrrjkj_translator_RTMP
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     home_my_jni_demo_RTMP
 * Method:    initialize
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_xrrjkj_translator_cloud_RTMP_initialize
  (JNIEnv *, jobject, jstring);

/*
 * Class:     home_my_jni_demo_RTMP
 * Method:    send
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_com_xrrjkj_translator_cloud_RTMP_send
  (JNIEnv *, jobject, jbyteArray, jint);

/*
 * Class:     home_my_jni_demo_RTMP
 * Method:    stop
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_xrrjkj_translator_cloud_RTMP_stop
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif

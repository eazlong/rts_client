//
// Created by Administrator on 2016/7/18.
//
#include "com_xrrjkj_translator_cloud_RTMP.h"
#include "librtmp/rtmp.h"
#include "librtmp/log.h"
#include <string.h>
#include <stdlib.h>
#include <android/log.h>

#define LOG_TAG "NativeRTMP"
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))

RTMP *rtmp=NULL;
RTMPPacket *packet=NULL;
#define FRAME_SIZE 160


extern "C" JNIEXPORT jint JNICALL
Java_com_xrrjkj_translator_cloud_RTMP_initialize(JNIEnv * env, jobject instance, jstring url )
{
//    const char* rtmpUrl = env->GetStringUTFChars(url,0);
//    char* pubRtmpUrl = (char*)malloc(strlen(rtmpUrl) + 1);
//    memset(pubRtmpUrl,0,strlen(rtmpUrl) + 1);
//    strcpy(pubRtmpUrl,rtmpUrl);
//
//    rtmp = RTMP_Alloc();
//    RTMP_Init(rtmp);
//
//    LOGI("RTMP_Init %s", pubRtmpUrl);
//    if (!RTMP_SetupURL(rtmp, pubRtmpUrl)) {
//        LOGE("RTMP_SetupURL error");
//        return -1;
//    }
//
//    if (!RTMP_Connect(rtmp, NULL) ) {
//        LOGE("RTMP_Connect error");
//        return -2;
//    }
//
//    LOGI("RTMP INIT OK");
//    return 0;


    const char *url_str = env->GetStringUTFChars( url, 0 );

//packet attributes
    uint32_t type=0;
    uint32_t datalength=0;
    uint32_t timestamp=0;
    uint32_t streamid=0;

    RTMP_LogLevel loglvl=RTMP_LOGALL;
    RTMP_LogSetLevel(loglvl);

    RTMP_debuglevel = loglvl;
    rtmp=RTMP_Alloc();
    RTMP_Init(rtmp);

//set connection timeout,default 30s
    rtmp->Link.timeout=5;
    if( !RTMP_SetupURL( rtmp, (char*)url_str ) )
    {
        RTMP_Log(RTMP_LOGERROR,"SetupURL Err\n");
        RTMP_Free(rtmp);
        return (jint)-1;
    }

//if unable,the AMF command would be 'play' instead of 'publish'
    RTMP_EnableWrite(rtmp);

    if (!RTMP_Connect(rtmp,NULL))
    {
        RTMP_Log(RTMP_LOGERROR,"Connect Err\n");
        RTMP_Free(rtmp);
        return (jint)-1;
    }

    if (!RTMP_ConnectStream(rtmp,0))
    {
        RTMP_Log(RTMP_LOGERROR,"ConnectStream Err\n");
        RTMP_Close(rtmp);
        RTMP_Free(rtmp);
        return (jint)-1;
    }

    packet=(RTMPPacket*)malloc(sizeof(RTMPPacket));
    RTMPPacket_Alloc( packet, FRAME_SIZE );
    RTMPPacket_Reset( packet );

    packet->m_hasAbsTimestamp = 0;
    packet->m_nChannel = 0x04;
    packet->m_nInfoField2 = rtmp->m_stream_id;

    return 0;
}


extern "C"
JNIEXPORT jint JNICALL Java_com_xrrjkj_translator_cloud_RTMP_send(JNIEnv *evn, jobject instance, jbyteArray buf, jint size)
{
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_nTimeStamp = 20;
    packet->m_packetType = 0x08;
    packet->m_nBodySize  = size;

    memset(packet->m_body, 0, FRAME_SIZE);

    evn->GetByteArrayRegion( buf, 0, size,  (signed char*)packet->m_body );

    if (!RTMP_IsConnected(rtmp))
    {
        RTMP_Log(RTMP_LOGERROR,"rtmp is not connect\n");
        return -1;
    }

    if (!RTMP_SendPacket(rtmp,packet,0))
    {
        RTMP_Log(RTMP_LOGERROR,"Send Error\n");
        return -2;
    }

    LOGI("SEND completed!");
    return 0;
}


extern "C"
JNIEXPORT jint JNICALL Java_com_xrrjkj_translator_cloud_RTMP_stop(JNIEnv *env, jobject instance)
{
    if (rtmp!=NULL){
        RTMP_Close(rtmp);
        RTMP_Free(rtmp);
        rtmp=NULL;
    }
    if (packet!=NULL){
        RTMPPacket_Free(packet);
        free(packet);
        packet=NULL;
    }

    return 0;
}


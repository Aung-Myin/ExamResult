#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_mgngoelay_examresult_Constants_getBanner(
        JNIEnv* env,
        jobject /* this */) {
    std::string key = "ca-app-pub-2780984156359274/1017836267";
    return env->NewStringUTF(key.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_mgngoelay_examresult_Constants_getInterstitial(
        JNIEnv* env,
        jobject /* this */) {
    std::string key = "ca-app-pub-2780984156359274/8681059082";
    return env->NewStringUTF(key.c_str());
}
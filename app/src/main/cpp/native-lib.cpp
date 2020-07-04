#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_mgngoelay_examresult_Constants_getBanner(
        JNIEnv* env,
        jobject /* this */) {
    std::string key = "890171551475022_890172791474898";
    return env->NewStringUTF(key.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_mgngoelay_examresult_Constants_getInterstitial(
        JNIEnv* env,
        jobject /* this */) {
    std::string key = "890171551475022_890174918141352";
    return env->NewStringUTF(key.c_str());
}
package com.mgngoelay.examresult;

public class Constants {
    static {
        System.loadLibrary("native-lib");
    }

    public static native String getBanner();

    public static native String getInterstitial();

}

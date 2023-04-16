package com.sd.shuadan.utils;

public class JniUtils {
    static {
        System.loadLibrary("qddext1");
    }

    public static native byte[] getIvs();

    public static native byte[] getKeys();

    public static native String getToken();
}
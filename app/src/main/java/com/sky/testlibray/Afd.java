package com.sky.testlibray;

import androidx.annotation.Keep;

public class Afd {
    static {
        System.loadLibrary("qdd");
    }

    public static native String goj(String str);

    public static void loadSo() {
        System.loadLibrary("qdd");
    }

    @Keep
    public static String rep(String str, long j) {
        return str + "," + j;
    }
}
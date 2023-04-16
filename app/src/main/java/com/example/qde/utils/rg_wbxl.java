package com.example.qde.utils;

public final class rg_wbxl {
    public static int rg_n21099(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception unused) {
            return 0;
        }
    }

    public static double rg_n21103(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception unused) {
            return 0.0d;
        }
    }

    public static boolean rg_n21109(String str) {
        return str == null || str.isEmpty();
    }

    public static String rg_n21111(byte[] bArr) {
        try {
            return new String(bArr);
        } catch (Exception unused) {
            return "";
        }
    }

    public static String rg_n21117(byte[] bArr, String str) {
        try {
            return new String(bArr, str);
        } catch (Exception unused) {
            return "";
        }
    }

    public static boolean rg_n21144(String str, String str2, boolean z) {
        return z ? str.equalsIgnoreCase(str2) : str.equals(str2);
    }

    public static byte[] rg_n21169(String str, String str2) {
        try {
            return str.getBytes(str2);
        } catch (Exception unused) {
            return null;
        }
    }

    public static String rg_n21218(String str, String str2, String str3, boolean z) {
        return z ? str.replaceFirst(str2, str3) : str.replaceAll(str2, str3);
    }

    public static String rg_n21227(String str, int i, int i2) {
        return i2 < 0 ? str.substring(i) : str.substring(i, i2 + i);
    }

    public static String rg_n21238(String str, int i) {
        int length = str.length();
        return str.substring(length - i, length);
    }
}
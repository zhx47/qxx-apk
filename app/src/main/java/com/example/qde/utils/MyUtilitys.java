package com.example.qde.utils;

import java.io.PrintStream;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyUtilitys {
    public static String rg_n27233(String str, String str2, String str3) {
        int indexOf;
        int length = (str2 == null || str2.isEmpty() || (indexOf = str.indexOf(str2)) <= -1) ? 0 : str2.length() + indexOf;
        int indexOf2 = str.indexOf(str3, length);
        if (indexOf2 < 0 || str3 == null || str3.isEmpty()) {
            indexOf2 = str.length();
        }
        return str.substring(length, indexOf2);
    }

    public static String rg_n27284(String str, String str2, int i) {
        try {
            Matcher matcher = Pattern.compile(str2, 10).matcher(str);
            return !matcher.find() ? "" : matcher.group(i);
        } catch (Exception unused) {
            PrintStream printStream = System.out;
            printStream.println("�����쳣��" + str2);
            return "";
        }
    }

    public static long GetTimestamp(boolean z) {
        return z ? new Date().getTime() : new Date().getTime() / 1000;
    }

    public static String rg_n27279(int i, String str) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i2 = 0; i2 < i; i2++) {
            stringBuffer.append(str.charAt((int) (Math.random() * str.length())));
        }
        return stringBuffer.toString();
    }
}
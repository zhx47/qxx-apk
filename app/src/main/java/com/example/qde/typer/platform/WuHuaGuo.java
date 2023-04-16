package com.example.qde.typer.platform;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class WuHuaGuo {
    private static final String a = "AES/CBC/PKCS5Padding";

    public static String encrypt(String str) {
        byte[] bArr;
        try {
            bArr = str.getBytes(StandardCharsets.UTF_8);
        } catch (Exception unused) {
            bArr = null;
        }
        return a(a(Base64.encode(bArr, 0), "1s2u3sd9s327cc06", "yk2e7d7a0df0gk5l"));
    }

    private static byte[] a(byte[] bArr, String str, String str2) {
        try {
            SecretKeySpec a2 = a(str);
            Cipher cipher = Cipher.getInstance(a);
            cipher.init(1, a2, b(str2));
            return cipher.doFinal(bArr);
        } catch (Exception unused) {
            return null;
        }
    }

    private static String a(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer(bArr.length * 2);
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(hexString);
        }
        return stringBuffer.toString();
    }

    private static SecretKeySpec a(String str) {
        byte[] bArr;
        if (str == null) {
            str = "";
        }
        StringBuffer stringBuffer = new StringBuffer(16);
        stringBuffer.append(str);
        while (stringBuffer.length() < 16) {
            stringBuffer.append("0");
        }
        if (stringBuffer.length() > 16) {
            stringBuffer.setLength(16);
        }
        bArr = stringBuffer.toString().getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(bArr, "AES");
    }

    private static IvParameterSpec b(String str) {
        byte[] bArr;
        if (str == null) {
            str = "";
        }
        StringBuffer stringBuffer = new StringBuffer(16);
        stringBuffer.append(str);
        while (stringBuffer.length() < 16) {
            stringBuffer.append("0");
        }
        if (stringBuffer.length() > 16) {
            stringBuffer.setLength(16);
        }
        bArr = stringBuffer.toString().getBytes(StandardCharsets.UTF_8);
        return new IvParameterSpec(bArr);
    }
}
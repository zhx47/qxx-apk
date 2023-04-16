package com.example.qde.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Task_KuaiDan {
    public static byte[] rg_n28364(String str, String str2, String str3, String str4, String str5) {
        String str6;
        if (!rg_n21109(str) && str2 != null && !rg_n21109(str2)) {
            if (str4 == null || rg_n21109(str4)) {
                str6 = "AES/ECB/NoPadding";
            } else {
                str6 = "AES/" + str4;
            }
            try {
                SecretKeySpec secretKeySpec = new SecretKeySpec(str2.getBytes(), "AES");
                IvParameterSpec ivParameterSpec = new IvParameterSpec(str3.getBytes());
                Cipher cipher = Cipher.getInstance(str6);
                cipher.init(1, secretKeySpec, ivParameterSpec);
                return cipher.doFinal(str.getBytes());
            } catch (Exception unused) {
            }
        }
        return null;
    }

    public static boolean rg_n21109(String str) {
        return str == null || str.isEmpty();
    }
}
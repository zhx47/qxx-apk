package com.example.qde.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Date;
import java.util.regex.Pattern;

public class KuaiDuan {
    public static String getParamBody(String str, String str2) {
        String valueOf = String.valueOf(MyUtilitys.GetTimestamp(true));
        return replaceBlank(rg_n21111(Base64.encode("{\"data\":\"[data]\",\"key_id\":7}".replace("[data]", rg_n28371(rg_n28364("{\"1\":\"1\",\"3\":\"2ceede331ca6116c7e0c28f1a687f9c4dd51d256\",\"4\":\"32\",\"5\":\"1080x1920\",\"6\":\"1080x1920\",\"7\":\",\",\"8\":\"\",\"9\":\"\",\"11\":\"1\",\"12\":\"1\",\"13\":\"true\",\"14\":\"-480\",\"15\":\"zh-CN\",\"16\":\"\",\"17\":\"1,0,1,1,1,1\",\"18\":\"3\",\"19\":\"4\",\"20\":\"0\",\"21\":\"\",\"22\":\"Gecko,20030107,Google Inc.,,Mozilla,Netscape,Linux armv7l,33,\",\"23\":\"5,1,1\",\"24\":\"1\",\"25\":\"Qualcomm,Adreno (TM) 330\",\"26\":null,\"27\":\"Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Mobile Safari/537.36 Html5Plus/1.0\",\"28\":\"false,false\",\"29\":\"true,true,true\",\"32\":\"1097\",\"34\":\"Linux armv7l\",\"35\":\"false,true\",\"60\":false,\"61\":false,\"62\":false,\"63\":false,\"64\":false,\"65\":false,\"101\":\"{sign}\",\"103\":\"{time}\",\"104\":\"\",\"106\":2008,\"107\":\"2.3.0\",\"108\":\"{URL}\",\"109\":\"\",\"110\":\"\",\"112\":\"\",\"113\":\"\",\"114\":\"3cf_shitong_subid\",\"115\":\"\",\"200\":\"1\"}".replace("{sign}", rg_n23908(String.format("anti-bot-df%s%sMozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Mobile Safari/537.36 Html5Plus/1.0%s", String.format("0.%s", MyUtilitys.rg_n27279(16, "0123456789")), "https://m.kd78.cn/Mobile/Login.aspx", valueOf).getBytes())).replace("{time}", valueOf).replace("{URL}", String.format("%s", str2)).replace("{referrer}", String.format("%s/Mobile/TaskList.aspx", str)), "0fc0d47746054969", "636014d173e04409", "CBC/PKCS5Padding", "UTF-8"))).getBytes(), 0)));
    }

    public static String replaceBlank(String str) {
        return str != null ? Pattern.compile("\\s*|\t|\r|\n").matcher(str).replaceAll("") : "";
    }

    protected static String rg_n28371(byte[] bArr) {
        char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        if (bArr == null || bArr.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            sb.append(cArr[(b >> 4) & 15]);
            sb.append(cArr[b & 15]);
        }
        return sb.toString();
    }

    public static String rg_n21111(byte[] bArr) {
        try {
            return new String(bArr);
        } catch (Exception unused) {
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

    public static String rg_n23908(byte[] bArr) {
        byte[] digest;
        if (bArr == null || bArr.length == 0 || (digest = rg_n23959("SHA-1").digest(bArr)) == null) {
            return null;
        }
        return rg_n23897(digest);
    }

    public static String rg_n23897(byte[] bArr) {
        StringBuilder sb;
        String str = "";
        if (bArr == null || bArr.length == 0) {
            return "";
        }
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() == 1) {
                sb = new StringBuilder();
                sb.append(str);
                str = "0";
            } else {
                sb = new StringBuilder();
            }
            sb.append(str);
            sb.append(hexString);
            str = sb.toString();
        }
        return str.toUpperCase();
    }

    public static MessageDigest rg_n23959(String str) {
        try {
            return MessageDigest.getInstance(str);
        } catch (Exception unused) {
            return null;
        }
    }

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
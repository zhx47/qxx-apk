package com.example.qde.utils;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Bbb {
    private static final String a = "AES/CBC/PKCS5Padding";
    public static byte[] f6000a = {9, 18, 37, 52, 33, 50, 101, 67};
    public static byte[] f6001b = {25, 3, 21, 10, 49, 23, 34, 52};

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

    public static byte[] a(byte[] bArr, String str, String str2) {
        try {
            SecretKeySpec a2 = a(str);
            Cipher cipher = Cipher.getInstance(a);
            cipher.init(1, a2, b(str2));
            return cipher.doFinal(bArr);
        } catch (Exception unused) {
            return null;
        }
    }

    public static String a(String str, String str2, String str3) {
        byte[] bArr;
        try {
            bArr = str.getBytes(StandardCharsets.UTF_8);
        } catch (Exception unused) {
            bArr = null;
        }
        return a(a(Base64.encode(bArr, 0), str2, str3));
    }

    public static byte[] b(byte[] bArr, String str, String str2) {
        try {
            SecretKeySpec a2 = a(str);
            Cipher cipher = Cipher.getInstance(a);
            cipher.init(2, a2, b(str2));
            return cipher.doFinal(bArr);
        } catch (Exception unused) {
            return null;
        }
    }

    public static String b(String str, String str2, String str3) {
        byte[] bArr;
        try {
            bArr = c(str);
        } catch (Exception unused) {
            bArr = null;
        }
        byte[] b2 = b(bArr, str2, str3);
        if (b2 == null) {
            return null;
        }
        return new String(b2, StandardCharsets.UTF_8);
    }

    public static String a(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer(bArr.length * 2);
        for (byte b2 : bArr) {
            String hexString = Integer.toHexString(b2 & 255);
            if (hexString.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(hexString);
        }
        return stringBuffer.toString();
    }

    private static byte[] c(String str) {
        if (str == null || str.length() < 2) {
            return new byte[0];
        }
        String lowerCase = str.toLowerCase();
        int length = lowerCase.length() / 2;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) (Integer.parseInt(lowerCase.substring(i2, i2 + 2), 16) & 255);
        }
        return bArr;
    }

    public static byte[] cc(byte[] bArr, byte[] bArr2) {
        try {
            DESKeySpec dESKeySpec = new DESKeySpec(bArr2);
            SecretKey generateSecret = SecretKeyFactory.getInstance("DES").generateSecret(dESKeySpec);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(1, generateSecret, new IvParameterSpec(dESKeySpec.getKey()));
            return cipher.doFinal(bArr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String cc(byte[] bArr) {
        StringBuilder sb = new StringBuilder(bArr.length);
        for (byte b2 : bArr) {
            String hexString = Integer.toHexString(b2 & 255);
            if (hexString.length() < 2) {
                sb.append(0);
            }
            sb.append(hexString.toUpperCase());
        }
        return sb.toString();
    }

    public static String f() {
        return a(System.currentTimeMillis());
    }

    public static String a(long j) {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(j));
    }

    private static PublicKey getPublicKeyFromX509(String str, String str2) throws Exception {
        return KeyFactory.getInstance(str).generatePublic(new X509EncodedKeySpec(Base64.decode(str2, 0)));
    }

    private static PrivateKey getPrivateKeyFromX509(String str, String str2) throws Exception {
        return KeyFactory.getInstance(str).generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(str2, 0)));
    }

    public static String encryptByPublic(String plainText, String publicKey) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (byte[] bArr : splitBytes(plainText.getBytes(), 117)) {
            try {
                PublicKey publicKeyFromX509 = getPublicKeyFromX509("RSA", publicKey);
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, publicKeyFromX509);
                byteArrayOutputStream.write(cipher.doFinal(bArr));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new String(Base64.encode(byteArrayOutputStream.toByteArray(), 2));
    }

    public static byte[][] splitBytes(byte[] bArr, int i) {
        int length;
        byte[] bArr2;
        if (bArr.length % i != 0) {
            length = (bArr.length / i) + 1;
        } else {
            length = bArr.length / i;
        }
        byte[][] bArr3 = new byte[length][];
        for (int i2 = 0; i2 < length; i2++) {
            if (i2 != length - 1 || bArr.length % i == 0) {
                bArr2 = new byte[i];
                System.arraycopy(bArr, i2 * i, bArr2, 0, i);
            } else {
                bArr2 = new byte[bArr.length % i];
                System.arraycopy(bArr, i2 * i, bArr2, 0, bArr.length % i);
            }
            bArr3[i2] = bArr2;
        }
        return bArr3;
    }

    public static String decryptByPublic(String encryptedText, String publicKeyString) {
        try {
            // 将公钥字符串转换为公钥对象
            byte[] keyBytes = Base64.decode(publicKeyString, Base64.DEFAULT);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            // 对密文进行解密
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);

            byte[] encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e("sss", e.toString());
        }
        return null;
    }

    public static void main(String[] strArr) {
        System.out.println(f());
    }

    public static String getEeeSign() {
        StringBuffer stringBuffer = new StringBuffer(String.valueOf(System.currentTimeMillis()));
        stringBuffer.insert(4, 9);
        stringBuffer.insert(7, 6);
        stringBuffer.insert(10, 0);
        stringBuffer.insert(14, 8);
        return d(stringBuffer.toString());
    }

    public static String d(String str) {
        try {
            SecretKey generateSecret = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(f6000a));
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(1, generateSecret, new IvParameterSpec(f6001b));
            return Base64.encodeToString(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String d(String str, byte[] bArr, byte[] bArr2) {
        try {
            SecretKey generateSecret = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(bArr));
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(1, generateSecret, new IvParameterSpec(bArr2));
            return Base64.encodeToString(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
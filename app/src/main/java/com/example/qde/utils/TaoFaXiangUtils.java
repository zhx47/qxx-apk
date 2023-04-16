package com.example.qde.utils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class TaoFaXiangUtils {
    public static String encryptByPublic(String str) {
        try {
            PublicKey publicKeyFromX509 = getPublicKeyFromX509("RSA", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzlGR97726ZYaHo59mhsh8u6r63+gu4i2PRH/Wak+9FU2uUVnWqthlYhPoV/9BSKaq/A48cCqilnAdA1E18YHXeEv50ZA6Wq7EU4FHMg+CrePamBQHAXZk11P4k5rPXA/WjmIPCQ6B2cvI3kGfO40+CwY4xajWbXLgRk4Cw5Q8TQIDAQAB");
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(1, publicKeyFromX509);
            byte[] bytes = str.getBytes();
            int length = bytes.length;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int i = 0;
            int i2 = 0;
            while (true) {
                int i3 = length - i;
                if (i3 > 0) {
                    byte[] doFinal = i3 > 117 ? cipher.doFinal(bytes, i, 117) : cipher.doFinal(bytes, i, i3);
                    byteArrayOutputStream.write(doFinal, 0, doFinal.length);
                    i2++;
                    i = i2 * 117;
                } else {
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.close();
                    return Base64Utils.encode(byteArray);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static PublicKey getPublicKeyFromX509(String str, String str2) throws Exception {
        return KeyFactory.getInstance(str).generatePublic(new X509EncodedKeySpec(Base64Utils.decode(str2)));
    }
}
package com.example.qde.utils;

import org.apache.commons.codec.binary.Base64;

/**
 * Base64加解密
 */
public class Base64Utils {

    private Base64Utils() {
    }

    /**
     * Base64编码
     *
     * @param binaryData 待编码的二进制数据
     * @return 编码后的Base64字符串
     */
    public static String encode(byte[] binaryData) {
        return Base64.encodeBase64String(binaryData);
    }

    /**
     * Base64解码
     *
     * @param base64String Base64字符串
     * @return 解码后的二进制数据
     */
    public static byte[] decode(String base64String) {
        return Base64.decodeBase64(base64String);
    }
}
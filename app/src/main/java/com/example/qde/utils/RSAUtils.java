package com.example.qde.utils;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;

public final class RSAUtils {
    private static final String RSA = "RSA";

    public static KeyPair generateRSAKeyPair() {
        return generateRSAKeyPair(1024);
    }

    public static KeyPair generateRSAKeyPair(int i) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
            keyPairGenerator.initialize(i);
            return keyPairGenerator.genKeyPair();
        } catch (NoSuchAlgorithmException unused) {
            return null;
        }
    }

    public static byte[] encryptData(byte[] bArr, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(1, publicKey);
            return cipher.doFinal(bArr);
        } catch (Exception unused) {
            return null;
        }
    }

    public static byte[] decryptData(byte[] bArr, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(2, privateKey);
            return cipher.doFinal(bArr);
        } catch (Exception unused) {
            return null;
        }
    }

    public static PublicKey getPublicKey(byte[] bArr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(RSA).generatePublic(new X509EncodedKeySpec(bArr));
    }

    public static PrivateKey getPrivateKey(byte[] bArr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(RSA).generatePrivate(new PKCS8EncodedKeySpec(bArr));
    }

    public static PublicKey getPublicKey(String str, String str2) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(RSA).generatePublic(new RSAPublicKeySpec(new BigInteger(str), new BigInteger(str2)));
    }

    public static PrivateKey getPrivateKey(String str, String str2) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(RSA).generatePrivate(new RSAPrivateKeySpec(new BigInteger(str), new BigInteger(str2)));
    }

    public static PublicKey loadPublicKey(String str) throws Exception {
        try {
            return KeyFactory.getInstance(RSA).generatePublic(new X509EncodedKeySpec(Base64Utils.decode(str)));
        } catch (NullPointerException unused) {
            throw new Exception("公钥数据为空");
        } catch (NoSuchAlgorithmException unused2) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException unused3) {
            throw new Exception("公钥非法");
        }
    }

    public static PrivateKey loadPrivateKey(String str) throws Exception {
        try {
            return KeyFactory.getInstance(RSA).generatePrivate(new PKCS8EncodedKeySpec(Base64Utils.decode(str)));
        } catch (NullPointerException unused) {
            throw new Exception("私钥数据为空");
        } catch (NoSuchAlgorithmException unused2) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException unused3) {
            throw new Exception("私钥非法");
        }
    }

    public static PublicKey loadPublicKey(InputStream inputStream) throws Exception {
        try {
            return loadPublicKey(readKey(inputStream));
        } catch (IOException unused) {
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException unused2) {
            throw new Exception("公钥输入流为空");
        }
    }

    public static PrivateKey loadPrivateKey(InputStream inputStream) throws Exception {
        try {
            return loadPrivateKey(readKey(inputStream));
        } catch (IOException unused) {
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException unused2) {
            throw new Exception("私钥输入流为空");
        }
    }

    private static String readKey(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                return sb.toString();
            }
            if (readLine.charAt(0) != '-') {
                sb.append(readLine);
                sb.append(13);
            }
        }
    }

    public static void printPublicKeyInfo(PublicKey publicKey) {
        RSAPublicKey rSAPublicKey = (RSAPublicKey) publicKey;
        System.out.println("----------RSAPublicKey----------");
        PrintStream printStream = System.out;
        printStream.println("Modulus.length=" + rSAPublicKey.getModulus().bitLength());
        PrintStream printStream2 = System.out;
        printStream2.println("Modulus=" + rSAPublicKey.getModulus().toString());
        PrintStream printStream3 = System.out;
        printStream3.println("PublicExponent.length=" + rSAPublicKey.getPublicExponent().bitLength());
        PrintStream printStream4 = System.out;
        printStream4.println("PublicExponent=" + rSAPublicKey.getPublicExponent().toString());
    }

    public static void printPrivateKeyInfo(PrivateKey privateKey) {
        RSAPrivateKey rSAPrivateKey = (RSAPrivateKey) privateKey;
        System.out.println("----------RSAPrivateKey ----------");
        PrintStream printStream = System.out;
        printStream.println("Modulus.length=" + rSAPrivateKey.getModulus().bitLength());
        PrintStream printStream2 = System.out;
        printStream2.println("Modulus=" + rSAPrivateKey.getModulus().toString());
        PrintStream printStream3 = System.out;
        printStream3.println("PrivateExponent.length=" + rSAPrivateKey.getPrivateExponent().bitLength());
        PrintStream printStream4 = System.out;
        printStream4.println("PrivatecExponent=" + rSAPrivateKey.getPrivateExponent().toString());
    }
}
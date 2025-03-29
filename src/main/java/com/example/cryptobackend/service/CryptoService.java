package com.example.cryptobackend.service;

import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoService {

    public String aesEncrypt(String text, String key) throws Exception {
        // Create SHA-256 hash of the key for consistent length
        byte[] keyBytes = sha256(key);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        
        // Generate random IV
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        // Initialize cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        
        // Encrypt
        byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        
        // Convert IV and encrypted bytes to hex string
        return bytesToHex(iv) + ":" + bytesToHex(encrypted);
    }

    public String aesDecrypt(String text, String key) throws Exception {
        // Split input into IV and encrypted parts
        String[] parts = text.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid encrypted text format");
        }
        
        byte[] iv = hexToBytes(parts[0]);
        byte[] encrypted = hexToBytes(parts[1]);

        // Create SHA-256 hash of the key
        byte[] keyBytes = sha256(key);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        // Initialize cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        
        // Decrypt
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public String hashSHA256(String text) throws NoSuchAlgorithmException {
        byte[] hash = sha256(text);
        return bytesToHex(hash);
    }

    public String hashMD5(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(text.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    public String encodeBase64(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    public String decodeBase64(String text) {
        byte[] decodedBytes = Base64.getDecoder().decode(text);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    // Helper method to compute SHA-256 hash
    private byte[] sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    // Helper method to convert bytes to hex string
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    // Helper method to convert hex string to bytes
    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
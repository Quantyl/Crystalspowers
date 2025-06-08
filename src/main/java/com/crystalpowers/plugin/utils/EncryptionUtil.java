package com.crystalpowers.plugin.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.logging.Logger;

/**
 * Utility class for encrypting and decrypting sensitive data
 * Uses AES-256 encryption with CBC mode and PKCS5 padding
 */
public class EncryptionUtil {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int KEY_LENGTH = 256;
    private static final int IV_LENGTH = 16;
    
    private static final Logger logger = Logger.getLogger(EncryptionUtil.class.getName());
    private static SecretKey secretKey;
    private static final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * Initialize encryption with a master password
     * @param masterPassword The master password for encryption
     */
    public static void initialize(String masterPassword) {
        try {
            // Generate key from password using SHA-256
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] keyBytes = digest.digest(masterPassword.getBytes(StandardCharsets.UTF_8));
            
            // Use first 32 bytes for AES-256
            byte[] aesKey = new byte[32];
            System.arraycopy(keyBytes, 0, aesKey, 0, Math.min(keyBytes.length, 32));
            
            secretKey = new SecretKeySpec(aesKey, ALGORITHM);
            logger.info("Encryption initialized successfully");
            
        } catch (Exception e) {
            logger.severe("Failed to initialize encryption: " + e.getMessage());
            throw new RuntimeException("Encryption initialization failed", e);
        }
    }
    
    /**
     * Generate a random encryption key
     * @return Base64 encoded encryption key
     */
    public static String generateRandomKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_LENGTH);
            SecretKey key = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (Exception e) {
            logger.severe("Failed to generate random key: " + e.getMessage());
            throw new RuntimeException("Key generation failed", e);
        }
    }
    
    /**
     * Encrypt a string using AES encryption
     * @param plaintext The text to encrypt
     * @return Base64 encoded encrypted text with IV prepended
     */
    public static String encrypt(String plaintext) {
        if (secretKey == null) {
            throw new IllegalStateException("Encryption not initialized. Call initialize() first.");
        }
        
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            
            // Generate random IV
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encryptedData = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            
            // Combine IV and encrypted data
            byte[] combined = new byte[IV_LENGTH + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(encryptedData, 0, combined, IV_LENGTH, encryptedData.length);
            
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (Exception e) {
            logger.severe("Encryption failed: " + e.getMessage());
            throw new RuntimeException("Encryption failed", e);
        }
    }
    
    /**
     * Decrypt a string using AES decryption
     * @param encryptedText Base64 encoded encrypted text with IV prepended
     * @return Decrypted plaintext
     */
    public static String decrypt(String encryptedText) {
        if (secretKey == null) {
            throw new IllegalStateException("Encryption not initialized. Call initialize() first.");
        }
        
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedText);
            
            if (combined.length < IV_LENGTH) {
                throw new IllegalArgumentException("Invalid encrypted data format");
            }
            
            // Extract IV and encrypted data
            byte[] iv = new byte[IV_LENGTH];
            byte[] encryptedData = new byte[combined.length - IV_LENGTH];
            
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            System.arraycopy(combined, IV_LENGTH, encryptedData, 0, encryptedData.length);
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            
            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            logger.severe("Decryption failed: " + e.getMessage());
            throw new RuntimeException("Decryption failed", e);
        }
    }
    
    /**
     * Create a secure hash of a string (for passwords, tokens, etc.)
     * @param input The string to hash
     * @return Base64 encoded hash
     */
    public static String createHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            logger.severe("Hashing failed: " + e.getMessage());
            throw new RuntimeException("Hashing failed", e);
        }
    }
    
    /**
     * Verify if a string matches a hash
     * @param input The string to verify
     * @param hash The hash to compare against
     * @return true if the input matches the hash
     */
    public static boolean verifyHash(String input, String hash) {
        try {
            String inputHash = createHash(input);
            return MessageDigest.isEqual(
                inputHash.getBytes(StandardCharsets.UTF_8),
                hash.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            logger.severe("Hash verification failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Generate a cryptographically secure random string
     * @param length The length of the random string
     * @return Base64 encoded random string
     */
    public static String generateSecureRandom(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }
    
    /**
     * Check if encryption is properly initialized
     * @return true if encryption is ready to use
     */
    public static boolean isInitialized() {
        return secretKey != null;
    }
}

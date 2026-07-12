package com.appsecco.dvja.security;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public final class AESUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final String ENV_KEY_NAME = "DVJA_AES_KEY";

    // Para GCM se recomienda un IV de 12 bytes.
    private static final int IV_LENGTH_BYTES = 12;

    // Etiqueta de autenticación de 128 bits.
    private static final int TAG_LENGTH_BITS = 128;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private AESUtil() {
        // Evita crear objetos de esta clase de utilidad.
    }

    private static SecretKeySpec getSecretKey() {
        String encodedKey = System.getenv(ENV_KEY_NAME);

        if (encodedKey == null || encodedKey.trim().isEmpty()) {
            throw new IllegalStateException(
                    "No se configuró la variable de entorno " + ENV_KEY_NAME
            );
        }

        byte[] keyBytes;

        try {
            keyBytes = Base64.decodeBase64(encodedKey);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "La clave AES no tiene un formato Base64 válido",
                    exception
            );
        }

        if (keyBytes.length != 16
                && keyBytes.length != 24
                && keyBytes.length != 32) {

            throw new IllegalStateException(
                    "La clave AES debe tener 16, 24 o 32 bytes"
            );
        }

        return new SecretKeySpec(keyBytes, "AES");
    }

    public static String encrypt(String plainText) {
        if (plainText == null || plainText.trim().isEmpty()) {
            return plainText;
        }

        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            SECURE_RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);

            GCMParameterSpec parameterSpec =
                    new GCMParameterSpec(TAG_LENGTH_BITS, iv);

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    getSecretKey(),
                    parameterSpec
            );

            byte[] encryptedBytes = cipher.doFinal(
                    plainText.getBytes(StandardCharsets.UTF_8)
            );

            // Guardamos el IV junto al texto cifrado.
            byte[] result = new byte[iv.length + encryptedBytes.length];

            System.arraycopy(
                    iv,
                    0,
                    result,
                    0,
                    iv.length
            );

            System.arraycopy(
                    encryptedBytes,
                    0,
                    result,
                    iv.length,
                    encryptedBytes.length
            );

            return Base64.encodeBase64String(result);

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "No fue posible cifrar el dato",
                    exception
            );
        }
    }

    public static String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.trim().isEmpty()) {
            return encryptedText;
        }

        try {
            byte[] result = Base64.decodeBase64(encryptedText);

            if (result.length <= IV_LENGTH_BYTES) {
                throw new IllegalArgumentException(
                        "El dato cifrado no tiene un formato válido"
                );
            }

            byte[] iv = new byte[IV_LENGTH_BYTES];

            byte[] encryptedBytes =
                    new byte[result.length - IV_LENGTH_BYTES];

            System.arraycopy(
                    result,
                    0,
                    iv,
                    0,
                    IV_LENGTH_BYTES
            );

            System.arraycopy(
                    result,
                    IV_LENGTH_BYTES,
                    encryptedBytes,
                    0,
                    encryptedBytes.length
            );

            Cipher cipher = Cipher.getInstance(ALGORITHM);

            GCMParameterSpec parameterSpec =
                    new GCMParameterSpec(TAG_LENGTH_BITS, iv);

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    getSecretKey(),
                    parameterSpec
            );

            byte[] decryptedBytes =
                    cipher.doFinal(encryptedBytes);

            return new String(
                    decryptedBytes,
                    StandardCharsets.UTF_8
            );

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "No fue posible descifrar el dato",
                    exception
            );
        }
    }
}
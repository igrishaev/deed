package deed;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

@SuppressWarnings("unused")
public class Crypto {

    public static final String AES = "AES";
    public static final String DES = "DES";
    public static final String DESede = "DESede";

    public static final String AES_CBC_NoPadding = "AES/CBC/NoPadding";
    public static final String AES_CBC_PKCS5Padding = "AES/CBC/PKCS5Padding";
    public static final String AES_ECB_NoPadding = "AES/ECB/NoPadding";
    public static final String AES_ECB_PKCS5Padding = "AES/ECB/PKCS5Padding";
    public static final String AES_GCM_NoPadding = "AES/GCM/NoPadding";

    public static final String DES_CBC_NoPadding = "DES/CBC/NoPadding";
    public static final String DES_CBC_PKCS5Padding = "DES/CBC/PKCS5Padding";
    public static final String DES_ECB_NoPadding = "DES/ECB/NoPadding";
    public static final String DES_ECB_PKCS5Padding = "DES/ECB/PKCS5Padding";

    public static final String DESede_CBC_NoPadding = "DESede/CBC/NoPadding";
    public static final String DESede_CBC_PKCS5Padding = "DESede/CBC/PKCS5Padding";
    public static final String DESede_ECB_NoPadding = "DESede/ECB/NoPadding";
    public static final String DESede_ECB_PKCS5Padding = "DESede/ECB/PKCS5Padding";

    //
    // Common
    //
    public static Cipher cipher(final String transformation) {
        try {
            return Cipher.getInstance(transformation);
        } catch (NoSuchAlgorithmException e) {
            throw Err.error(e, "no such transformation: %s", transformation);
        } catch (NoSuchPaddingException e) {
            throw Err.error(e, "no such padding: %s", transformation);
        }
    }

    //
    // InputStream
    //
    public static CipherInputStream cipherInputStream(
            final InputStream in,
            final byte[] keySecret,
            final String cipherAlgorithm,
            final String keyAlgorithm
    ) {
        final Key key = new SecretKeySpec(keySecret, keyAlgorithm);
        final Cipher cipher = cipher(cipherAlgorithm);
        initDecrypt(cipher, key);
        return new CipherInputStream(in, cipher);
    }

    public static void initDecrypt(final Cipher cipher, final Key key) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            throw Err.error(e, "cannot init decrypt mode, cipher: %s, key: %s", cipher, key);
        }
    }

    public static CipherInputStream AESInputStream(final InputStream in, final byte[] keySecret, final String cipherAlgorithm) {
        return cipherInputStream(in, keySecret, cipherAlgorithm, AES);
    }

    public static CipherInputStream AESInputStream(final InputStream in, final byte[] keySecret) {
        return AESInputStream(in, keySecret, AES_CBC_NoPadding);
    }

    public static CipherInputStream DESInputStream(final InputStream in, final byte[] keySecret, final String cipherAlgorithm) {
        return cipherInputStream(in, keySecret, cipherAlgorithm, DES);
    }

    public static CipherInputStream DESInputStream(final InputStream in, final byte[] keySecret) {
        return DESInputStream(in, keySecret, DES_CBC_NoPadding);
    }

    public static CipherInputStream DESedeInputStream(final InputStream in, final byte[] keySecret, final String cipherAlgorithm) {
        return cipherInputStream(in, keySecret, cipherAlgorithm, DESede);
    }

    public static CipherInputStream DESedeInputStream(final InputStream in, final byte[] keySecret) {
        return DESedeInputStream(in, keySecret, DESede_CBC_NoPadding);
    }

    //
    // OutputStream
    //
    public static CipherOutputStream cipherOutputStream(
            final OutputStream out,
            final byte[] keySecret,
            final String cipherAlgorithm,
            final String keyAlgorithm
    ) {
        final Key key = new SecretKeySpec(keySecret, keyAlgorithm);
        final Cipher cipher = cipher(cipherAlgorithm);
        initEncrypt(cipher, key);
        return new CipherOutputStream(out, cipher);
    }

    public static void initEncrypt(final Cipher cipher, final Key key) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            throw Err.error(e, "cannot init encrypt mode, cipher: %s, key: %s", cipher, key);
        }
    }

    public static CipherOutputStream AESOutputStream(final OutputStream out, final byte[] keySecret, final String cipherAlgorithm) {
        return cipherOutputStream(out, keySecret, cipherAlgorithm, AES);
    }

    public static CipherOutputStream AESOutputStream(final OutputStream out, final byte[] keySecret) {
        return AESOutputStream(out, keySecret, AES_CBC_NoPadding);
    }

    public static CipherOutputStream DESOutputStream(final OutputStream out, final byte[] keySecret, final String cipherAlgorithm) {
        return cipherOutputStream(out, keySecret, cipherAlgorithm, DES);
    }

    public static CipherOutputStream DESOutputStream(final OutputStream out, final byte[] keySecret) {
        return DESOutputStream(out, keySecret, DES_CBC_NoPadding);
    }

    public static CipherOutputStream DESedeOutputStream(final OutputStream out, final byte[] keySecret, final String cipherAlgorithm) {
        return cipherOutputStream(out, keySecret, cipherAlgorithm, DESede);
    }

    public static CipherOutputStream DESedeOutputStream(final OutputStream out, final byte[] keySecret) {
        return DESedeOutputStream(out, keySecret, DESede_CBC_NoPadding);
    }
}

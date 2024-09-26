package deed;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class Crypto {

    public static final String AES_CBC_NoPadding = "AES/CBC/NoPadding";
    public static final String AES_CBC_PKCS5Padding = "AES/CBC/PKCS5Padding";
    public static final String AES_ECB_NoPadding = "AES/ECB/NoPadding";
    public static final String AES_ECB_PKCS5Padding = "AES/ECB/PKCS5Padding";
    public static final String AES_GCM_NoPadding = "AES/GCM/NoPadding";

//    AES/CBC/NoPadding (128)
//    AES/CBC/PKCS5Padding (128)
//    AES/ECB/NoPadding (128)
//    AES/ECB/PKCS5Padding (128)
//    AES/GCM/NoPadding (128)
//    DES/CBC/NoPadding (56)
//    DES/CBC/PKCS5Padding (56)
//    DES/ECB/NoPadding (56)
//    DES/ECB/PKCS5Padding (56)
//    DESede/CBC/NoPadding (168)
//    DESede/CBC/PKCS5Padding (168)
//    DESede/ECB/NoPadding (168)
//    DESede/ECB/PKCS5Padding (168)
//    RSA/ECB/PKCS1Padding (1024, 2048)
//    RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048)
//    RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)

    public static CipherInputStream AESInputStream(final InputStream in, final byte[] secret) {
        return AESInputStream(in, secret, AES_CBC_NoPadding);
    }

    public static CipherInputStream AESInputStream(final InputStream in, final byte[] secret, final String transformation) {
        final Key key = new SecretKeySpec(secret, "AES");
        final Cipher cipher = cipher(transformation);
        initDecrypt(cipher, key);
        return new CipherInputStream(in, cipher);
    }

    public static Cipher cipher(final String transformation) {
        try {
            return Cipher.getInstance(transformation);
        } catch (NoSuchAlgorithmException e) {
            throw Err.error(e, "no such transformation: %s", transformation);
        } catch (NoSuchPaddingException e) {
            throw Err.error(e, "no such padding: %s", transformation);
        }
    }

    public static void initEncrypt(final Cipher cipher, final Key key) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            throw Err.error(e, "cannot init encrypt mode, cipher: %s, key: %s", cipher, key);
        }
    }

    public static void initDecrypt(final Cipher cipher, final Key key) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            throw Err.error(e, "cannot init decrypt mode, cipher: %s, key: %s", cipher, key);
        }
    }


}

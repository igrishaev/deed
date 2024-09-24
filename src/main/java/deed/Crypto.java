package deed;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class Crypto {

    public static SecretKey AESKey(final String secret) {
        return AESKey(secret.getBytes(StandardCharsets.UTF_8));
    }

    public static SecretKey AESKey(final byte[] secret) {
        return new SecretKeySpec(secret, "AES");
    }

    public static Cipher AESCipher () {
        final String transformation = "AES/ECB/PKCS5Padding";
        try {
            return Cipher.getInstance(transformation);
        } catch (NoSuchAlgorithmException e) {
            throw Err.error(e, "not such algorithm: %s", transformation);
        } catch (NoSuchPaddingException e) {
            throw Err.error(e, "not such padding: %s", transformation);
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

    public static CipherOutputStream wrapCipher(final OutputStream out, final Cipher cipher) {
        if (out instanceof CipherOutputStream cos) {
            return cos;
        } else {
            return new CipherOutputStream(out, cipher);
        }
    }

    public static CipherInputStream wrapCipher(final InputStream in, final Cipher cipher) {
        if (in instanceof CipherInputStream cis) {
            return cis;
        } else {
            return new CipherInputStream(in, cipher);
        }
    }

}

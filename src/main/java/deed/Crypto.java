package deed;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class Crypto {

    public static SecretKey secretKey(final byte[] secret, final String transformation) {
        final String[] parts = transformation.split("/");
        if (parts.length == 0) {
            throw Err.error("empty transformation: %s", transformation);
        }
        final String algorithm = parts[0];
        return new SecretKeySpec(secret, algorithm);
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

    public static CipherOutputStream wrapCipher(
            final OutputStream out,
            final String algorithm,
            final byte[] secret
    ) {
        final Cipher cipher = cipher(algorithm);
        final Key secretKey = secretKey(secret, algorithm);
        initEncrypt(cipher, secretKey);
        return new CipherOutputStream(out, cipher);
    }

    public static CipherInputStream wrapCipher(
            final InputStream in,
            final String algorithm,
            final byte[] secret
    ) {
        final Cipher cipher = cipher(algorithm);
        final Key secretKey = secretKey(secret, algorithm);
        initDecrypt(cipher, secretKey);
        return new CipherInputStream(in, cipher);
    }
}

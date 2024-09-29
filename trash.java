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



    @SuppressWarnings("unused")
    public static InputStream toInputStream(final Object x) {
        if (x instanceof InputStream in) {
            return in;
        } else if (x instanceof File f) {
            return fileInputStream(f);
        } else if (x instanceof Socket s) {
            try {
                return s.getInputStream();
            } catch (IOException e) {
                throw Err.error(e, "cannot get input stream from socket %s", s);
            }
        } else if (x instanceof URL u) {
            if (u.getProtocol().equals("file")) {
                final File f = new File(u.getFile());
                return fileInputStream(f);
            } else {
                try {
                    return u.openStream();
                } catch (IOException e) {
                    throw Err.error(e, "cannot open stream from URL %s", u);
                }
            }
        } else if (x instanceof URI u) {
            try {
                return toInputStream(u.toURL());
            } catch (MalformedURLException e) {
                throw Err.error("the URL is malformed: %s", u);
            }
        } else {
            throw Err.error("cannot coerce to input stream: %s", x);
        }
    }

    public static OutputStream toOutputStream(final Object x) {
        if (x instanceof OutputStream)
    }


package deed;

import mikera.vectorz.*;

public class VectorZ {

    public void encodeIVector(final Encoder encoder, final short oid, final IVector iv) {
        encoder.writeOID(oid);
        final int len = iv.length();
        encoder.writeInt(len);
        for (int i = 0; i < len; i++) {
            encoder.writeDouble(iv.get(i));
        }
    }

    public void encodeVector(final Encoder encoder, final mikera.vectorz.Vector v) {
        encodeIVector(encoder, OID.MIKERA_VECTOR, v);
    }

    public void encodeVector1(final Encoder encoder, final mikera.vectorz.Vector1 v1) {
        encodeIVector(encoder, OID.MIKERA_VECTOR1, v1);
    }

    public void encodeVector2(final Encoder encoder, final mikera.vectorz.Vector2 v2) {
        encodeIVector(encoder, OID.MIKERA_VECTOR2, v2);
    }

    public void encodeVector3(final Encoder encoder, final mikera.vectorz.Vector3 v3) {
        encodeIVector(encoder, OID.MIKERA_VECTOR3, v3);
    }

    public void encodeVector4(final Encoder encoder, final mikera.vectorz.Vector4 v4) {
        encodeIVector(encoder, OID.MIKERA_VECTOR4, v4);
    }

    public IVector decodeVector(final Decoder decoder) {
        final int len = decoder.readInteger();
        final IVector vector = Vectorz.newVector(len);
        double x;
        for (int i = 0; i < len; i++) {
            x = decoder.readDouble();
            vector.set(i, x);
        }
        return vector;
    }

}

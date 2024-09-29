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

package deed;

import mikera.vectorz.*;

@SuppressWarnings("unused")
public class VectorZ {

    public static void encodeAVector(final Encoder encoder, final AVector av) {
        encoder.writeOID(OID.VECTORZ_AVECTOR);
        final int len = av.length();
        encoder.writeInt(len);
        for (int i = 0; i < len; i++) {
            encoder.writeDouble(av.get(i));
        }
    }

    public static IVector decodeAVector(final Decoder decoder) {
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

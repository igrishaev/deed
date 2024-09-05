package pinny;

import java.io.*;
import java.util.List;

public class Main {

    public static void main(String... args) throws IOException {

        try(final FileOutputStream out = new FileOutputStream("test.pinny");
            final Encoder encoder = new Encoder(out)) {

            encoder.encode(999);
            encoder.encode(true);
            encoder.encode("test");
            encoder.encodeMulti(List.of(1,2,3,4,5,6,7));
            encoder.encode("test");
        }

        try(final FileInputStream in = new FileInputStream("test.pinny");
            final Decoder decoder = new Decoder(in)) {

            for (Object x: decoder) {
                System.out.println(x);
            }
        }
    }

}

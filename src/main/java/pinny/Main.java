package pinny;

import clojure.lang.IFn;
import clojure.lang.PersistentVector;

import java.io.*;
import pinny.Decoder;

import clojure.java.api.Clojure;

public class Main {

    public static void main(String... args) throws IOException {

        IFn inc = Clojure.var("clojure.core", "inc");
        IFn map = Clojure.var("clojure.core", "map");

        try(final FileOutputStream out = new FileOutputStream("test.pinny");
            final Encoder encoder = new Encoder(null, out)) {

            System.out.println(map.invoke(inc, PersistentVector.create(1, 2, 3)));

            encoder.encode(map.invoke(inc, PersistentVector.create(1, 2, 3, 4)));

            // encoder.encode(999);
//            encoder.encode(true);
//            encoder.encode("test");
//            encoder.encodeMulti(List.of(1,2,3,4,5,6,7));
//            encoder.encode("test");
        }

//        try(final FileInputStream in = new FileInputStream("test.pinny");
//            final Decoder decoder = new Decoder(in)) {
//
//            for (Object x: decoder) {
//                System.out.println(x);
//            }
//        }
    }

}

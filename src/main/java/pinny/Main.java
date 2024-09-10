package pinny;

import clojure.lang.IFn;

import java.io.*;
import java.util.concurrent.atomic.AtomicLong;

import clojure.java.api.Clojure;

public class Main {

    public static void main(String... args) throws IOException, ClassNotFoundException {

        IFn inc = Clojure.var("clojure.core", "inc");
        IFn map = Clojure.var("clojure.core", "map");

        File file = new File("aaa.test");

        try(final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("aaa.test"))) {
            out.writeInt(1);
            out.writeInt(2);
            out.writeInt(3);
            out.writeObject(new Exception("test"));
            out.writeInt(1);
            out.writeInt(2);
            out.writeInt(3);
        }

        AtomicLong toSkip = new AtomicLong();

        try(final ObjectInputStream in = new ObjectInputStream(new FileInputStream("aaa.test"))) {
            in.setObjectInputFilter((ObjectInputFilter.FilterInfo info) -> {
                toSkip.set(info.streamBytes());
                return ObjectInputFilter.Status.REJECTED;
            });
            System.out.println(in.readInt());
            System.out.println(in.readInt());
            System.out.println(in.readInt());
            try {
                System.out.println(in.readObject());
            } catch (InvalidClassException e) {
                in.skip(toSkip.get());
                System.out.println("aaa");
            }
            System.out.println(in.readInt());
//            System.out.println(in.readInt());
//            System.out.println(in.readInt());
        }



//        try(final FileOutputStream out = new FileOutputStream("test.pinny");
//            final Encoder encoder = new Encoder(null, out)) {
//
//            System.out.println(map.invoke(inc, PersistentVector.create(1, 2, 3)));
//
//            encoder.encode(map.invoke(inc, PersistentVector.create(1, 2, 3, 4)));
//
//            // encoder.encode(999);
////            encoder.encode(true);
////            encoder.encode("test");
////            encoder.encodeMulti(List.of(1,2,3,4,5,6,7));
////            encoder.encode("test");
//        }

//        try(final FileInputStream in = new FileInputStream("test.pinny");
//            final Decoder decoder = new Decoder(in)) {
//
//            for (Object x: decoder) {
//                System.out.println(x);
//            }
//        }
    }

}

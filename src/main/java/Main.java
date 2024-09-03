import clojure.lang.Atom;
import clojure.lang.IFn;

import java.io.*;

public class Main {

    public static void main(String... args) throws IOException {

        FileOutputStream out = new FileOutputStream(new File("test.pinny"));

        final Encoder encoder = new Encoder(out);
        System.out.println(encoder.encode(999));
        System.out.println(encoder.encode(true));
        System.out.println(encoder.encode("test"));
        System.out.println(encoder.encode(new Atom(12345)));

        encoder.flush();
        out.close();


//        ObjectOutputStream o = new ObjectOutputStream(out);
//        o.writeInt(333);
//        o.writeBoolean(true);
//        o.writeBytes("hello");
//        o.close();

        FileInputStream fi = new FileInputStream(new File("test.pinny"));
        final Decoder decoder = new Decoder(fi);
        System.out.println(decoder.decode());
        System.out.println(decoder.decode());
        System.out.println(decoder.decode());
        System.out.println(decoder.decode());
        System.out.println(decoder.decode());
        fi.close();


//        ObjectInputStream in = new ObjectInputStream(fi);

//        System.out.println(in.readInt());
//        System.out.println(in.readInt());
//        in.close();




    }

}

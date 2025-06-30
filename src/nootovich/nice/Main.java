package nootovich.nice;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import nootovich.nglib.NGUtils;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) NGUtils.error("Operation mode unspecified.\n" + usage());
        if (args[0].equals("-e")) {
            if (args.length < 2) NGUtils.error("No input file provided.\n" + usage());
            if (args.length < 3) NGUtils.error("No output path provided.\n" + usage());
            Encoder.encode(ImageIO.read(new File(args[1])), args[2]);
        } else if (args[0].equals("-d")) {
            if (args.length < 2) NGUtils.error("No input file provided.\n" + usage());
            new Decoder().main(args[1]);
        } else if (args[0].equals("-ed")) {
            if (args.length < 2) NGUtils.error("No input file provided.\n" + usage());
            Encoder.encode(ImageIO.read(new File(args[1])), args[1] + ".nicetemp");
            new Decoder().main(args[1] + ".nicetemp");
        } else NGUtils.error("Unsupported mode.\n" + usage());
    }

    public static String usage() {
        return "Usage:\n\tEncoder\t\t[-e] [input_file] [output_path]\n\tDecoder\t\t[-d] [input_file(.nice)]";
    }
}

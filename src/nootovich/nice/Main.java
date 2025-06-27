package nootovich.nice;

import nootovich.nglib.NGUtils;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) NGUtils.error("Operation mode unspecified.\n" + usage());
        if (args[0].equals("-e")) {
            if (args.length < 2) NGUtils.error("No input file provided.\n" + usage());
            if (args.length < 3) NGUtils.error("No output path provided.\n" + usage());
            Encoder.encode(args[1], args[2]);
        } else if (args[0].equals("-d")) {
            if (args.length < 2) NGUtils.error("No input file provided.\n" + usage());
            new Decoder().main(args[1]);
        } else NGUtils.error("Unsupported mode.\n" + usage());
    }

    public static String usage() {
        return "Usage:\n\tEncoder\t\t[-e] [input_file] [output_path]\n\tDecoder\t\t[-d] [input_file(.nice)]";
    }
}

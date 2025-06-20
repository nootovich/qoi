package nootovich.qoi;

import java.io.IOException;
import nootovich.nglib.NGUtils;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) NGUtils.error("Operation mode unspecified.\nAvailable modes:\n\tEncoder\t\t[-e] [input_file] [output_path]");
        if (!args[0].equals("-e")) NGUtils.error("Unsupported mode.\nAvailable modes:\n\tEncoder\t\t[-e] [input_file] [output_path]");
        if (args.length < 2) NGUtils.error("No input file provided.\nUsage:\n\tEncoder\t\t[-e] [input_file] [output_path]");
        if (args.length < 3) NGUtils.error("No output path provided.\nUsage:\n\tEncoder\t\t[-e] [input_file] [output_path]");
        try {
            Encoder.encode(args[1], args[2]);
        } catch (IOException e) {
            NGUtils.error(e.getMessage());
        }
    }
}

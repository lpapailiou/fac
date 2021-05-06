package main;

import scanner.JScanner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RunScanner {

    private static final Logger LOG = Logger.getLogger(String.class.getName());

    public static void main(String[] args) {
        if (args.length > 1) {
            LOG.log(Level.INFO, "This class will process the first argument only.");
        }

        String encodingName = "UTF-8";
        String baseFile = "src/main/resources/misc_test_2.txt";

        if (args.length > 0) {
            try {
                baseFile = args[0];
            } catch (Exception e) {
                LOG.log(Level.INFO, "File path could not be found.");
            }
        }
        String file = Paths.get(baseFile).toAbsolutePath().toString();
        try (FileInputStream stream = new FileInputStream(file); InputStreamReader reader = new InputStreamReader(stream, encodingName)) {
            JScanner scanner = new JScanner(reader);
            while (!scanner.yyatEOF()) {
                scanner.next_token();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

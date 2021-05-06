package main;

import java_cup.runtime.Symbol;
import parser.JParser;
import parser.interpreter.Interpreter;
import parser.parsetree.Program;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RunParser {

    private static final Logger LOG = Logger.getLogger(String.class.getName());

    public static void main(String[] args) {
        if (args.length > 1) {
            LOG.log(Level.INFO, "This class will process the first argument only.");
        }

        String encodingName = "UTF-8";
        String baseFile = "src/main/resources/hello_world.txt";

        if (args.length > 0) {
            try {
                baseFile = args[0];
            } catch (Exception e) {
                LOG.log(Level.INFO, "File path could not be found.");
            }
        }
        String file = Paths.get(baseFile).toAbsolutePath().toString();
        try (FileInputStream stream = new FileInputStream(file); InputStreamReader reader = new InputStreamReader(stream, encodingName)) {
            JParser parser = new JParser(reader);

            Symbol reducedResult = null;

            while (!parser.yyatEOF()) {
                reducedResult = parser.parse();
            }

            System.out.println("***** PARSER RESULT *****\n\n" + reducedResult.value + "\n");

            Interpreter interpreter = new Interpreter();
            interpreter.visit((Program) reducedResult.value);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package main;

import interpreter.Executor;
import java_cup.runtime.Symbol;
import parser.JParser;
import parser.parsetree.Program;

import java.io.*;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RunConsoleExecutor {

    private static final Logger LOG = Logger.getLogger(String.class.getName());
    private static final String CONSOLE_MARKER = ">  ";


    public static void main(String[] args) {
        if (args.length > 1) {
            LOG.log(Level.INFO, "This class will process no arguments at all. Please continue on console.");
        }
        Scanner scan = new Scanner(System.in);
        String code = "";
        String cache = "";

        System.out.println("**** WELCOME TO JLANG **** ");
        System.out.println(CONSOLE_MARKER + "... initialized & ready to code!");

        while (cache != null) {
            System.out.print(CONSOLE_MARKER);
            cache = scan.nextLine();
            System.out.println(CONSOLE_MARKER);

            try {
                read(code + cache);
                code += cache;
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Entered code not valid (" + e.getLocalizedMessage() + ")!");
            }

        }
    }

    private static void read(String code) throws Exception {
        String encodingName = "UTF-8";
        try (InputStream stream = new ByteArrayInputStream(code.getBytes()); InputStreamReader reader = new InputStreamReader(stream, encodingName)) {
            JParser parser = new JParser(reader, false);

            Symbol reducedResult = null;

            while (!parser.yyatEOF()) {
                reducedResult = parser.parse();
            }

            Executor executor = new Executor();
            executor.visit((Program) reducedResult.value);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

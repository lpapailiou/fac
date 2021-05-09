package main;

import execution.Executor;
import java_cup.runtime.Symbol;
import parser.JParser;
import parser.interpreter.Interpreter;
import parser.parsetree.Program;
import scanner.JScanner;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Compiler {

    private static final String ENCODING = "UTF-8";
    private static final Logger LOG = Logger.getLogger(String.class.getName());
    private static final Scanner SCANNER = new Scanner(System.in);
    private static Option option = Option.CONSOLE;
    private static String defaultFilePath = "src/main/resources/hello_world.txt";
    private static String path;
    private static String cache;


    public static void main(String[] args) {
        if (args != null) {
            cache = Arrays.toString(args).replaceAll("\\[", "").replaceAll("]", "").replaceAll(",", "");
        }
        while (true) {
            evaluateArguments(cache.split(" "));
            if (cache.startsWith("-h") || cache.startsWith("-q")) {
                continue;
            }
            switch (option) {
                case SCAN:
                    try {
                        if (path != null) {
                            scanFile(path);
                        }
                    } catch (IOException e) {
                        LOG.log(Level.WARNING, "File " + path + " does not seem to be readable. The sample file " + defaultFilePath + " will be processed instead.");
                        try {
                            scanFile(defaultFilePath);
                        } catch (IOException e1) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case PARSE:
                    try {
                        if (path != null) {
                            parseFile(path);
                        }
                    } catch (IOException e) {
                        LOG.log(Level.WARNING, "File " + path + " does not seem to be readable. The sample file " + defaultFilePath + " will be processed instead.");
                        try {
                            parseFile(defaultFilePath);
                        } catch (IOException e1) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case INTERPRET:
                    try {
                        if (path != null) {
                            interpretFile(path);
                        }
                    } catch (IOException e) {
                        LOG.log(Level.WARNING, "File " + path + " does not seem to be readable. The sample file " + defaultFilePath + " will be processed instead.");
                        try {
                            interpretFile(defaultFilePath);
                        } catch (IOException e1) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case EXECUTE:
                    try {
                        if (path != null) {
                            LOG.log(Level.WARNING, "File " + path + " does not seem to be readable. The sample file " + defaultFilePath + " will be processed instead.");
                            executeFile(path);
                        }
                    } catch (IOException e) {
                        try {
                            executeFile(defaultFilePath);
                        } catch (IOException e1) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case CONSOLE:
                    startExecutor();
                    break;
                default:
                    LOG.log(Level.INFO, "Compiler was started with invalid arguments. Type -h for help.");
            }
            if (!cache.startsWith("-h")) {
                LOG.log(Level.INFO, "What are you going to do next? Type -h for help, -q to quit or press any key to start console.");
                cache = SCANNER.nextLine();
            }
        }

    }

    private static void evaluateArguments(String[] args) {
        option = Option.CONSOLE;
        path = null;
        if (args == null || args.length == 0) {
            return;
        } else if (args[0].equals("-q")) {
            LOG.log(Level.INFO, "... bye bye.");
            System.exit(0);
        } else if (args[0].equals("-h")) {
            LOG.log(Level.INFO, "Following options are available:\n\t-o scan\n\t-o parse\n\t-o interpret\n\t-o execute\n\nOptionally you may enter a file path after the option.");
            cache = SCANNER.nextLine();
            if (!cache.startsWith("-h")) {
                cache = "";
            }
        } else if (args[0].equals("-o")) {
            switch (args[1]) {
                case "scan":
                    option = Option.SCAN;
                    break;
                case "parse":
                    option = Option.PARSE;
                    break;
                case "interpret":
                    option = Option.INTERPRET;
                    break;
                case "execute":
                    option = Option.EXECUTE;
                    break;
                case "console":
                    option = Option.CONSOLE;
                    break;
                default:
                    LOG.log(Level.WARNING, "Entered option is not valid, console opens.");
                    option = Option.CONSOLE;
            }
            if (option == Option.CONSOLE) {
                path = null;
            } else if (args.length > 2) {
                path = args[2];
            } else {
                path = defaultFilePath;
            }
        }
    }

    private static void startExecutor() {
        String consoleMarker = ">  ";
        String code = "";

        System.out.println("(press -h for help or -q to quit)");
        System.out.println("**************************************************************");
        System.out.println("*                      WELCOME TO JLANG                      *");
        System.out.println("**************************************************************");
        System.out.println(consoleMarker + "... initialized & ready to code!");

        while (true) {
            System.out.print(consoleMarker);
            cache = SCANNER.nextLine();
            if (cache.startsWith("-h") || cache.startsWith("-q")) {
                break;
            }

            try {
                if (!cache.startsWith("//") && !cache.replaceAll("\\[ \\s \t\n\r]", "").equals("")) {
                    executeConsoleContent(code + cache);
                    code += cache;
                }
            } catch (Exception | Error e) {
                LOG.log(Level.WARNING, "Entered code not valid (" + e.getLocalizedMessage() + ")!");
            }
        }
    }

    private static void executeConsoleContent(String code) throws Exception {
        try (InputStream stream = new ByteArrayInputStream(code.getBytes()); InputStreamReader reader = new InputStreamReader(stream, ENCODING)) {
            JParser parser = new JParser(reader, false);

            Symbol reducedResult = null;

            while (!parser.yyatEOF()) {
                reducedResult = parser.parse();
            }

            Executor executor = new Executor();
            executor.setScriptMode(true);
            executor.visit((Program) reducedResult.value);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void executeFile(String data) throws IOException {
        try (FileInputStream stream = new FileInputStream(Paths.get(data).toAbsolutePath().toString()); InputStreamReader reader = new InputStreamReader(stream, ENCODING)) {
            JParser parser = new JParser(reader, true);

            Symbol reducedResult = null;

            try {
                while (!parser.yyatEOF()) {
                    reducedResult = parser.parse();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("***** PARSER RESULT *****\n\n" + reducedResult.value + "\n");

            System.out.println("***** EXECUTION RESULT *****\n");

            Executor executor = new Executor();
            executor.visit((Program) reducedResult.value);

        } catch (IOException e) {
            throw e;
        }
    }

    private static void interpretFile(String data) throws IOException {
        try (FileInputStream stream = new FileInputStream(Paths.get(data).toAbsolutePath().toString()); InputStreamReader reader = new InputStreamReader(stream, ENCODING)) {
            JParser parser = new JParser(reader);

            Symbol reducedResult = null;

            try {
                while (!parser.yyatEOF()) {
                    reducedResult = parser.parse();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("***** PARSER RESULT *****\n\n" + reducedResult.value + "\n");

            Interpreter interpreter = new Interpreter();
            interpreter.visit((Program) reducedResult.value);

        } catch (IOException e) {
            throw e;
        }
    }

    private static void parseFile(String data) throws IOException {
        try (FileInputStream stream = new FileInputStream(Paths.get(data).toAbsolutePath().toString()); InputStreamReader reader = new InputStreamReader(stream, ENCODING)) {
            JParser parser = new JParser(reader);

            Symbol reducedResult = null;

            try {
                while (!parser.yyatEOF()) {
                    reducedResult = parser.parse();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("***** PARSER RESULT *****\n\n" + reducedResult.value + "\n");

        } catch (IOException e) {
            throw e;
        }
    }

    private static void scanFile(String data) throws IOException {
        try (FileInputStream stream = new FileInputStream(Paths.get(data).toAbsolutePath().toString()); InputStreamReader reader = new InputStreamReader(stream, ENCODING)) {

            JScanner scanner = new JScanner(reader);

            while (!scanner.yyatEOF()) {
                scanner.next_token();
            }

        } catch (IOException e) {
            throw e;
        }
    }

}

package main;

import exceptions.GrammarException;
import exceptions.ScanException;
import execution.Executor;
import interpreter.Interpreter;
import java_cup.runtime.Symbol;
import parser.JParser;
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


    public static void main(String... args) {
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
                case PARSE:
                case INTERPRET:
                case EXECUTE:
                    try {
                        if (path != null) {
                            processFile(path);
                        }
                    } catch (IOException e) {
                        LOG.log(Level.WARNING, "File " + path + " does not seem to be readable. The sample file " + defaultFilePath + " will be processed instead.");
                        try {
                            processFile(defaultFilePath);
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
                System.out.println();
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
        String tmpCache = "";

        System.out.println("(press -h for help or -q to quit)");
        System.out.println("**************************************************************");
        System.out.println("*                      WELCOME TO JLANG                      *");
        System.out.println("**************************************************************");
        System.out.println(consoleMarker + "... initialized & ready to code!");

        while (true) {
            System.out.print(consoleMarker);
            tmpCache = SCANNER.nextLine();
            if (tmpCache.startsWith("-h") || tmpCache.startsWith("-q")) {
                cache = tmpCache;
                break;
            } else if (!tmpCache.replaceAll("\\[ \\s \t\n\r]", "").equals("")) {
                if (!tmpCache.startsWith("//")) {
                    cache += tmpCache;
                }
                continue;
            }

            if (cache.replaceAll("\\[ \\s \t\n\r]", "").equals("")) {
                continue;
            }

            try {
                executeConsoleContent(code + cache);
                code += cache;
            } catch (Exception e) {
                if (e instanceof GrammarException) {
                    LOG.log(Level.WARNING, "Parsed code semantically not valid (" + e.getLocalizedMessage() + ")!");
                } else {
                    LOG.log(Level.WARNING, "Parsed code syntax not valid!");
                }
            } catch (Error e) {
                Throwable t = new ScanException(e.getMessage(), e);
                LOG.log(Level.WARNING, "Scanned code not valid (" + t.getLocalizedMessage() + ")!");
            } finally {
                cache = "";
            }
        }
    }

    private static void executeConsoleContent(String code) throws Exception {
        try (InputStream stream = new ByteArrayInputStream(code.getBytes()); InputStreamReader reader = new InputStreamReader(stream, ENCODING)) {
            JParser parser = new JParser(reader, false);
            Program program = getProgram(parser);

            Executor executor = new Executor();
            executor.setScriptMode(true);
            program.accept(executor);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processFile(String data) throws IOException {
        try (FileInputStream stream = new FileInputStream(Paths.get(data).toAbsolutePath().toString()); InputStreamReader reader = new InputStreamReader(stream, ENCODING)) {
            Program program = null;

            try {
                if (option == Option.SCAN) {
                    JScanner scanner = new JScanner(reader);
                    program = getProgram(scanner);
                } else {
                    JParser parser = new JParser(reader, true);
                    program = getProgram(parser);
                }
            } catch (Exception e) {
                if (e instanceof GrammarException) {
                    LOG.log(Level.WARNING, "Parsed code semantically not valid (" + e.getLocalizedMessage() + ")!");
                } else {
                    LOG.log(Level.WARNING, "Parsed code syntax not valid!");
                    e.printStackTrace();
                }
            } catch (Error e) {
                Throwable t = new ScanException(e.getMessage(), e);
                LOG.log(Level.WARNING, "Scanned code not valid (" + t.getLocalizedMessage() + ")!");
            }

            if (option != Option.SCAN) {
                System.out.println("***** PARSER RESULT *****\n\n" + program + "\n");
            }
            if (option == Option.INTERPRET) {
                Interpreter interpreter = new Interpreter();
                program.accept(interpreter);

                System.out.println("\n***** SEMANTIC CHECK SUCCEEDED *****\n");
            } else if (option == Option.EXECUTE) {
                System.out.println("***** EXECUTION RESULT *****\n");

                Executor executor = new Executor();
                program.accept(executor);
            }

        } catch (IOException e) {
            throw e;
        }
    }

    private static Program getProgram(JScanner scanner) throws Exception {
        Symbol root = null;
        while (!scanner.yyatEOF()) {
            root = scanner.next_token();
        }
        return (Program) root.value;
    }

    private static Program getProgram(JParser parser) throws Exception {
        Symbol root = null;
        while (!parser.yyatEOF()) {
            root = parser.parse();
        }
        return (Program) root.value;
    }

}

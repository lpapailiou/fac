package main;

import exceptions.GrammarException;
import exceptions.ScanException;
import execution.Interpreter;
import java_cup.runtime.Symbol;
import parser.JParser;
import parser.parsetree.Program;
import scanner.JScanner;
import validator.Validator;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible for the execution of this program in the terminal.
 * It will stay in a loop and provide multiple options to process code until the user decides to quit the program.
 */
class Console {

    private static final String ENCODING = "UTF-8";
    private static final Logger LOG = Logger.getLogger(String.class.getName());
    private static final Scanner SCANNER = new Scanner(System.in);
    private static Option option = Option.CONSOLE;
    private static String defaultFilePath = "samples/hello_world.txt";
    private static String path;
    private static String cache;

    /**
     * The console can be started in different modes. The modes can be passed as arguments.
     * Options:
     * <code>-o scan</code>: scan a code to for lexical validation.
     * <code>-o parse</code>: parse a code for syntactical validation.
     * <code>-o validate</code>: validate a code for semantic validation.
     * <code>-o execute</code>: execute a code.
     * <code>-o console</code>: start console mode and process code typed as console input.
     * If a file path is given as third argument, the according file will be processed. Otherwise, a short demo file
     * will be processed.
     * If no arguments are given at all, console mode will be started immediately.
     *
     * @param args the arguments passed to start the console mode.
     */
    static void startTerminal(String... args) {
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
                case VALIDATE:
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
                    startConsole();
                    break;
                default:
                    LOG.log(Level.INFO, "Main was started with invalid arguments. Type -h for help.");
            }
            if (!cache.startsWith("-h")) {
                System.out.println();
                LOG.log(Level.INFO, "What are you going to do next? Type -h for help, -q to quit or press any key to start console.");
                cache = SCANNER.nextLine();
            }
        }

    }

    /**
     * This method is used to evaluate passed arguments from the main method. Its purpose is to set the correct mode
     * and optionally point to the file chosen to process.
     *
     * @param args the arguments to evaluate the mode to take.
     */
    private static void evaluateArguments(String... args) {
        option = Option.CONSOLE;
        path = null;
        if (args == null || args.length == 0) {
            return;
        } else if (args[0].equals("-q")) {
            LOG.log(Level.INFO, "... bye bye.");
            System.exit(0);
        } else if (args[0].equals("-h")) {
            LOG.log(Level.INFO, "Following options are available:\n\t-o scan\n\t-o parse\n\t-o validate\n\t-o execute\n\nOptionally you may enter a file path after the option.");
            cache = SCANNER.nextLine();
            evaluateArguments(cache.split(" "));
        } else if (args[0].equals("-o")) {
            try {
                option = Option.valueOf(args[1].toUpperCase());
            } catch (Exception e) {
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

    /**
     * This method will start the interactive console mode. After printing a header, the user may then
     * enter script code to process.
     */
    private static void startConsole() {
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

    /**
     * This method executes console input after the input is confirmed by the ENTER key.
     * It will process the code accordingly. If valid, the next line may be entered. If not, the last
     * entry is rejected, and the user may retry with a new input.
     *
     * @param code the code to process.
     * @throws Exception the exception to throw in case the code is rejected.
     */
    private static void executeConsoleContent(String code) throws Exception {
        try (InputStream stream = new ByteArrayInputStream(code.getBytes()); InputStreamReader reader = new InputStreamReader(stream, ENCODING)) {
            JParser parser = new JParser(reader, false);
            Program program = getProgram(parser);

            Interpreter interpreter = new Interpreter();
            interpreter.setScriptMode(true);
            program.accept(interpreter);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will process code from a given file. The processing will depend on the chosen mode.
     *
     * @param data the file path of the code.
     * @throws IOException the exception thrown in case the file path is not valid.
     */
    private static void processFile(String data) throws IOException {
        try (InputStreamReader reader = data.equals(defaultFilePath) ?
                new InputStreamReader(Console.class.getClassLoader().getResourceAsStream(defaultFilePath), ENCODING) :                     // make work from jar
                new InputStreamReader(new FileInputStream(Paths.get(data).toAbsolutePath().toString()), ENCODING)) {                    // custom file

            Program program = null;
            JParser parser = null;

            try {
                if (option == Option.SCAN) {
                    JScanner scanner = new JScanner(reader);
                    program = getProgram(scanner);
                } else {
                    parser = new JParser(reader, true);
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
                System.out.println("***** PARSE TREE *****\n\n" + program + "\n");

                for (String str : parser.getScannerOutput()) {
                    System.out.println(str);
                }
                System.out.println("\n");
                System.out.println("***** PARSER RESULT *****\n\n" + program + "\n");
            }
            if (option == Option.VALIDATE) {
                Validator validator = new Validator();
                program.accept(validator);

                System.out.println("\n***** SEMANTIC CHECK SUCCEEDED *****\n");
            } else if (option == Option.EXECUTE) {
                System.out.println("***** EXECUTION RESULT *****\n");

                Interpreter interpreter = new Interpreter();
                program.accept(interpreter);
            }

        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * This method will perform a scanning process for the complete code.
     *
     * @param scanner the scanner to use (including reader and input).
     * @return the last token which was scanned.
     * @throws Exception the exception thrown in case of invalid code.
     */
    private static Program getProgram(JScanner scanner) throws Exception {
        Symbol root = null;
        while (!scanner.yyatEOF()) {
            root = scanner.next_token();
        }
        return (Program) root.value;
    }

    /**
     * This method will perform a parsing process for the complete code.
     *
     * @param parser the parser to use (including scanner, reader and input).
     * @return the root of the parse tree as Program instance.
     * @throws Exception the exception thrown in case of invalid code.
     */
    private static Program getProgram(JParser parser) throws Exception {
        Symbol root = null;
        while (!parser.yyatEOF()) {
            root = parser.parse();
        }
        return (Program) root.value;
    }

}

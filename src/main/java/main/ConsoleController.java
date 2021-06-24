package main;

import execution.Mode;
import execution.Processor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible for the execution of this program in the terminal.
 * It will stay in a loop and provide multiple options to process code until the user decides to quit the program.
 */
class ConsoleController {

    private static final String ENCODING = "UTF-8";
    private static final Logger LOG = Logger.getLogger(String.class.getName());
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final String defaultFilePath = "samples/hello_world.txt";
    private static Mode mode = Mode.CONSOLE;
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
     * <code>-o gui</code>: starts up the graphic user interface while leaving the console open.
     * If a file path is given as third argument, the according file will be processed. Otherwise, a short demo file
     * will be processed.
     * If no arguments are given at all, console mode will be started immediately.
     *
     * @param args the arguments passed to start the console mode.
     */
    void startTerminal(String... args) {
        if (args != null) {
            cache = Arrays.toString(args).replaceAll("\\[", "").replaceAll("]", "").replaceAll(",", "");
        }
        RUNNER:
        while (true) {
            evaluateArguments(cache.split(" "));
            if (cache.startsWith("-h") || cache.startsWith("-q")) {
                continue;
            }
            switch (mode) {
                case SCAN:
                case PARSE:
                case VALIDATE:
                case EXECUTE:
                    try {
                        if (path != null) {
                            processFile(path);
                        }
                    } catch (IOException e) {
                        System.out.println();
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
                case GUI:
                    System.out.println("\nSee you on the other side ...\n");
                    Main.initializeGui();
                    break RUNNER;
                default:
                    System.out.println();
                    LOG.log(Level.INFO, "Main was started with invalid arguments. Type -h for help.");
            }
            if (!cache.startsWith("-h")) {
                System.out.println("\n\n");
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
    private void evaluateArguments(String... args) {
        mode = Mode.CONSOLE;
        path = null;
        if (args == null || args.length == 0) {
            return;
        }

        switch (args[0]) {
            case "-q":
                System.out.println();
                LOG.log(Level.INFO, "... bye bye.");
                System.exit(0);
            case "-h":
                System.out.println();
                LOG.log(Level.INFO, "Following options are available:\n\t-o scan\n\t-o parse\n\t-o validate\n\t-o execute\n\t-o gui\n\nOptionally you may enter a file path after the option.");
                cache = SCANNER.nextLine();
                evaluateArguments(cache.split(" "));
                break;
            case "-o":
                try {
                    mode = Mode.valueOf(args[1].toUpperCase());
                } catch (Exception e) {
                    System.out.println();
                    LOG.log(Level.WARNING, "Entered option is not valid, console opens.");
                    mode = Mode.CONSOLE;
                }
                if (mode == Mode.CONSOLE) {
                    path = null;
                } else if (args.length > 2) {
                    path = args[2];
                } else {
                    path = defaultFilePath;
                }
                break;
            default:
                break;
        }
    }

    /**
     * This method will start the interactive console mode. After printing a header, the user may then
     * enter script code to process.
     */
    private void startConsole() {
        String consoleMarker = ">  ";
        StringBuilder code = new StringBuilder();
        String tmpCache;

        System.out.println("(press -h for help or -q to quit)");
        System.out.println("**************************************************************");
        System.out.println("*                      WELCOME TO JLANG                      *");
        System.out.println("**************************************************************");
        System.out.println(consoleMarker + "... initialized & ready to code!");

        LOOP:
        while (true) {
            System.out.print(consoleMarker);
            tmpCache = SCANNER.nextLine();
            if (tmpCache.startsWith("-h") || tmpCache.startsWith("-q")) {
                cache = tmpCache;
                break LOOP;
            } else if (!tmpCache.replaceAll("\\[ \\s \t\n\r]", "").equals("")) {
                if (!tmpCache.startsWith("//")) {
                    cache += tmpCache;
                }
                continue;
            }

            if (cache.replaceAll("\\[ \\s \t\n\r]", "").equals("")) {
                continue;
            }

            new Processor(mode, code + cache);
            code.append(cache);
            cache = "";
        }
    }

    /**
     * This method will process code from a given file. The processing will depend on the chosen mode.
     *
     * @param data the file path of the code.
     * @throws IOException the exception thrown in case the file path is not valid.
     */
    private void processFile(String data) throws IOException {
        try (InputStreamReader reader = data.equals(defaultFilePath) ?
                new InputStreamReader(Objects.requireNonNull(ConsoleController.class.getClassLoader().getResourceAsStream(defaultFilePath)), ENCODING) :  // make work from jar
                new InputStreamReader(new FileInputStream(Paths.get(data).toAbsolutePath().toString()), ENCODING);                              // custom file
             BufferedReader br = new BufferedReader(reader)) {

            StringBuilder text = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append("\n");
            }
            Processor processor = new Processor(mode, text.toString());

            System.out.println();
            if (mode != Mode.SCAN) {
                System.out.println("***** PARSE TREE *****\n\n" + processor.getParseTree() + "\n");
                System.out.println("***** PARSER RESULT *****\n\n" + processor.getParseCode() + "\n");
            }
            if (mode == Mode.VALIDATE && !processor.isExceptionThrown()) {
                System.out.println("\n***** SEMANTIC CHECK SUCCEEDED *****\n");
            } else if (mode == Mode.EXECUTE) {
                System.out.println("***** EXECUTION RESULT *****\n\n" + processor.getExecutionResult());
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }


}

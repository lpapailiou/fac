package execution;

import exceptions.GrammarException;
import exceptions.ScanException;
import java_cup.runtime.Symbol;
import parser.JParser;
import parser.parsetree.Program;
import scanner.JScanner;
import validator.Validator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles the code processing. Depending on the given mode, it will
 * scan, parse, validate and / or execute the passed code.
 * During code processing, all possible output is cached. Additionally, possible exceptions and errors are catched and kept track of.
 * The according result is available by getters.
 */
public class Processor {

    private static final Logger LOG = Logger.getLogger(String.class.getName());
    private StringWriter stackTraceWriter;

    private boolean exceptionThrown = false;
    private boolean errorThrown = false;

    private boolean lexCheck = true;
    private boolean parseCheck = true;
    private boolean validationCheck = true;
    private boolean runtimeCheck = true;

    private String scannerOutput;
    private String parseTree;
    private String parseCode;
    private String executionResult;
    private String errorMessage = "";

    private Symbol currentToken;
    private int[] location = {0, 0};

    /**
     * The constructor of this class will take a mode and code as string and start processing immediately.
     * This means, that an instance of this class cannot be reused in any way.
     *
     * @param mode the processing mode.
     * @param code the code to process.
     */
    public Processor(Mode mode, String code) {
        try (InputStream stream = new ByteArrayInputStream(code.getBytes()); InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JScanner scanner = null;
            JParser parser = null;
            Interpreter interpreter = null;
            try {

                Symbol root;
                Program program;
                if (mode == Mode.SCAN) {
                    scanner = new JScanner(reader, true);
                    while (!scanner.yyatEOF()) {
                        scanner.next_token();
                    }
                    scannerOutput = String.join("\n", scanner.getOutput());
                } else {
                    parser = new JParser(reader, mode != Mode.CONSOLE && mode != Mode.GUI);
                    root = parser.parse();
                    scannerOutput = String.join("\n", parser.getScannerOutput());
                    program = (Program) root.value;
                    parseTree = program.getParseTree();
                    parseCode = program.toString();
                    if (mode == Mode.VALIDATE) {
                        Validator validator = new Validator();
                        program.accept(validator);
                    } else if (mode == Mode.EXECUTE || mode == Mode.CONSOLE || mode == Mode.GUI) {
                        interpreter = new Interpreter();
                        interpreter.setScriptMode(mode == Mode.CONSOLE);
                        program.accept(interpreter);
                        executionResult = String.join("\n", interpreter.getOutput());
                    }
                }

            } catch (Exception e) {

                exceptionThrown = true;
                String message = e.getMessage();
                if (e instanceof GrammarException) {
                    errorMessage = "Parsed code semantically not valid!\n" + message;
                    validationCheck = false;
                    runtimeCheck = false;
                    setLocation(message);
                } else if (e instanceof ArithmeticException) {
                    errorMessage = "During runtime, an arithmetic operation resulted in an invalid numeric value!";
                    runtimeCheck = false;
                    setLocation(message);
                } else {
                    errorMessage = "Parsed code syntax not valid, parse tree could not be constructed!";
                    validationCheck = false;
                    parseCheck = false;
                    runtimeCheck = false;
                }
                stackTraceWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTraceWriter));
                e.printStackTrace();

            } catch (Error e) {                                                         // if an Error occurs, it's source is from the scanner or from overflow

                errorThrown = true;
                Throwable t = new ScanException(e.getMessage(), e);
                String message = t.getMessage();
                if (message == null || e instanceof StackOverflowError) {
                    errorMessage = "StackOverflowError during execution occurred!";
                    runtimeCheck = false;
                    if (message != null) {
                        setLocation(message);
                    }
                    if (interpreter != null) {
                        executionResult = String.join("\n", interpreter.getOutput());
                    }
                } else {
                    errorMessage = "Scanned code not valid!\n" + message;
                    lexCheck = false;
                    validationCheck = false;
                    parseCheck = false;
                    runtimeCheck = false;
                }
                stackTraceWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTraceWriter));
                e.printStackTrace();

            } finally {

                if (!errorMessage.equals("")) {
                    LOG.log(Level.WARNING, errorMessage);
                } else {
                    errorMessage = "everything is fine :)";
                }
                if (stackTraceWriter != null) {
                    errorMessage += "\n\n\nstackTrace:\n" + stackTraceWriter.toString();
                }
                if (parser != null && scannerOutput == null) {
                    currentToken = parser.getCurrentToken();
                    scannerOutput = String.join("\n", parser.getScannerOutput());
                } else if (scanner != null && scannerOutput == null) {
                    scannerOutput = String.join("\n", scanner.getOutput());
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns true if an exception was thrown.
     *
     * @return true if an exception was thrown.
     */
    public boolean isExceptionThrown() {
        return exceptionThrown;
    }

    /**
     * Returns true if an error was thrown.
     *
     * @return true if an error was thrown.
     */
    public boolean isErrorThrown() {
        return errorThrown;
    }

    /**
     * Returns true if the lexical validation was successful.
     *
     * @return true if the lexical validation was successful.
     */
    public boolean isLexCheckSuccessful() {
        return lexCheck;
    }

    /**
     * Returns true if the syntax validation was successful.
     *
     * @return true if the syntax validation was successful.
     */
    public boolean isParseCheckSuccessful() {
        return parseCheck;
    }

    /**
     * Returns true if the semantic validation was successful.
     *
     * @return true if the semantic validation was successful.
     */
    public boolean isValidationCheckSuccessful() {
        return validationCheck;
    }

    /**
     * Returns true if no runtime exception occurred.
     *
     * @return true if no runtime exception occurred.
     */
    public boolean isRuntimeCheckSuccessful() {
        return runtimeCheck;
    }

    /**
     * Returns a list of the scanned tokens and their location.
     *
     * @return the scanner output.
     */
    public String getScannerOutput() {
        return scannerOutput == null ? "" : scannerOutput;
    }

    /**
     * Returns a text representation of the parse tree.
     *
     * @return the parse tree as text.
     */
    public String getParseTree() {
        return parseTree == null ? "" : parseTree;
    }

    /**
     * Returns the pretty-printed code as read by the parser.
     *
     * @return the pretty-printed code which was parsed.
     */
    public String getParseCode() {
        return parseCode == null ? "" : parseCode;
    }

    /**
     * Returns the execution result (i.e. everything which was printed).
     *
     * @return the execution result.
     */
    public String getExecutionResult() {
        return executionResult == null ? "" : executionResult;
    }

    /**
     * Returns the error message if any occurred.
     *
     * @return the error message.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Returns the last token which was processed, if available.
     *
     * @return the current token.
     */
    public Symbol getCurrentToken() {
        return currentToken;
    }

    /**
     * Returns the location of an exception, if any occurred.
     * The location will be used to mark the according part within the input area.
     *
     * @return the error location.
     */
    public int[] getLocation() {
        return location;
    }

    /**
     * This method extracts the location of the exception if known. The location is the character index
     * of the error causing element.
     *
     * @param message the error message to extract the error location from.
     */
    private void setLocation(String message) {
        if (message.contains("location")) {
            try {
                String[] loc = message.substring(message.indexOf("location")).split(",");
                int left = Integer.parseInt(loc[0].replaceAll("[^0-9]", ""));
                int right = Integer.parseInt(loc[1].replaceAll("[^0-9]", ""));
                location = new int[]{left, right};
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            location = new int[]{0, 0};
        }
    }

}

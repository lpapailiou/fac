package main;

import exceptions.GrammarException;
import exceptions.ScanException;
import execution.Interpreter;
import java_cup.runtime.Symbol;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import parser.JParser;
import parser.parsetree.Program;
import scanner.JScanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This is the main class of this application. Its purpose is to start code processing.
 * The Main can be run in different modes.
 * After processing, the program will remain running. A small menu allows to change mode and continue processing.
 */
public class AppController implements Initializable {

    @FXML
    TextArea input;

    @FXML
    TextArea scanOut;

    @FXML
    TextArea parseTreeOut;

    @FXML
    TextArea codeOut;

    @FXML
    TextArea executeOut;

    @FXML
    Button start;

    private static final String ENCODING = "UTF-8";
    private static final Logger LOG = Logger.getLogger(String.class.getName());
    private static final Scanner SCANNER = new Scanner(System.in);
    private static Option option = Option.CONSOLE;
    private static String defaultFilePath = "samples/hello_world.txt";
    private static String path;
    private static String cache;


    public void init() {
        start.setOnAction(e -> process());
        input.setText("number one = 1; print(one);" +
                "if(true) { one++; } else { one = 1 + 2; } print(one);");
    }


    private void process() {
        try (InputStream stream = new ByteArrayInputStream(input.getText().getBytes()); InputStreamReader reader = new InputStreamReader(stream, ENCODING)) {

            Program program = null;

            try {
                JParser parser = new JParser(reader, true);
                program = getProgram(parser);
                scanOut.setText(parser.getScannerOutput().stream().collect(Collectors.joining("\n")));
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

            Interpreter interpreter = new Interpreter();
            program.accept(interpreter);
            parseTreeOut.setText(program.getParseTree());
            codeOut.setText(program.toString());
            executeOut.setText(interpreter.getOutput().stream().collect(Collectors.joining("\n")));

        } catch (IOException e) {
            e.printStackTrace();
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
    }
}

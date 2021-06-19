package main;

import exceptions.GrammarException;
import exceptions.ScanException;
import execution.Interpreter;
import java_cup.runtime.Symbol;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import parser.JParser;
import parser.parsetree.Program;
import scanner.JScanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
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
    TabPane tabPane;

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
    TextArea validationOut;

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
        input.setText("def number fun(number x, number xx) { number a = 3; return x + 1; }" +
                "print(fun(7,7));");
    }


    private void process() {
        try (InputStream stream = new ByteArrayInputStream(input.getText().getBytes()); InputStreamReader reader = new InputStreamReader(stream, ENCODING)) {

            Program program = null;
            Interpreter interpreter = null;
            String err = "";

            try {
                JParser parser = new JParser(reader, true);
                program = getProgram(parser);
                scanOut.setText(parser.getScannerOutput().stream().collect(Collectors.joining("\n")));

                interpreter = new Interpreter();
                if (program != null) {
                    program.accept(interpreter);
                }
            } catch (Exception e) {
                if (e instanceof GrammarException) {
                    err = "Parsed code semantically not valid (" + e.getLocalizedMessage() + ")!";
                    LOG.log(Level.WARNING, err);
                } else {
                    err = "Parsed code syntax not valid!\n" + Arrays.toString(e.getStackTrace());
                    LOG.log(Level.WARNING, "Parsed code syntax not valid!");
                    e.printStackTrace();
                }
            } catch (Error e) {
                Throwable t = new ScanException(e.getMessage(), e);
                err = "Scanned code not valid (" + t.getLocalizedMessage() + ")!";
                LOG.log(Level.WARNING, "Scanned code not valid (" + t.getLocalizedMessage() + ")!");
            }

            if (!err.equals("")) {
                scanOut.setText("");
                parseTreeOut.setText("");
                codeOut.setText("");
                executeOut.setText("");
                validationOut.setText(err);
                tabPane.getSelectionModel().select(4);
            } else {
                parseTreeOut.setText(program.getParseTree());
                codeOut.setText(program.toString());
                executeOut.setText(interpreter.getOutput().stream().collect(Collectors.joining("\n")));
                validationOut.setText("- lexical validation is fine\n- syntax validation is fine\n- semantic validation is fine\n- no runtime errors");
                tabPane.getSelectionModel().select(3);
            }

        } catch (IOException e) {
            e.printStackTrace();
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
        tabPane.getSelectionModel().select(3);
        input.requestFocus();
    }
}

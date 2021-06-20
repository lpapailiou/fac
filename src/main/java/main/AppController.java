package main;

import exceptions.GrammarException;
import exceptions.ScanException;
import execution.Interpreter;
import java_cup.runtime.Symbol;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import parser.JParser;
import parser.parsetree.Program;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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

    @FXML
    CheckBox lexCheck;

    @FXML
    CheckBox parseCheck;

    @FXML
    CheckBox validationCheck;

    @FXML
    Button upload;

    @FXML
    ToggleButton theme;

    private static final String ENCODING = "UTF-8";
    private static final Logger LOG = Logger.getLogger(String.class.getName());
    private FileChooser fileChooser = new FileChooser();
    private static final Scanner SCANNER = new Scanner(System.in);
    private static Option option = Option.CONSOLE;
    private static String defaultFilePath = "samples/hello_world.txt";
    private static String path;
    private static String cache;


    public void init() {
        start.setOnAction(e -> {
            process(true);
            //tabPane.getSelectionModel().select(3);
        });
        tabPane.getSelectionModel().select(3);
        scanOut.setEditable(false);
        parseTreeOut.setEditable(false);
        codeOut.setEditable(false);
        executeOut.setEditable(false);
        validationOut.setEditable(false);

        input.textProperty().addListener((o, nv, ov) -> {
            if (input.getText().substring(input.getText().length() - 2).equals("\n\n")) {
                process(false);
            }
            Platform.runLater(() -> input.requestFocus());
        });

        lexCheck.setDisable(true);
        parseCheck.setDisable(true);
        validationCheck.setDisable(true);

    }

    public void setUpFileChooser(Stage stage) {
        if (stage == null) {
            return;
        }
        upload.setOnAction(ev -> {
            File file = fileChooser.showOpenDialog(stage);
            uploadFile(file);
        });
    }

    private void uploadFile(File file) {
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

                StringBuilder text = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    text.append(line);
                    text.append("\n");
                }

                input.setText(text.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void process(boolean opneExec) {
        try (InputStream stream = new ByteArrayInputStream(input.getText().getBytes()); InputStreamReader reader = new InputStreamReader(stream, ENCODING)) {

            lexCheck.setSelected(true);
            parseCheck.setSelected(true);
            validationCheck.setSelected(true);


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
                    validationCheck.setSelected(false);
                } else {

                    StringWriter stackTraceWriter = new StringWriter();
                    e.printStackTrace(new PrintWriter(stackTraceWriter));
                    err = "Parsed code syntax not valid!\n" + stackTraceWriter.toString();
                    LOG.log(Level.WARNING, "Parsed code syntax not valid!");
                    validationCheck.setSelected(false);
                    parseCheck.setSelected(false);
                }
            } catch (Error e) {
                Throwable t = new ScanException(e.getMessage(), e);
                err = "Scanned code not valid (" + t.getLocalizedMessage() + ")!";
                LOG.log(Level.WARNING, "Scanned code not valid (" + t.getLocalizedMessage() + ")!");
                lexCheck.setSelected(false);
                parseCheck.setSelected(false);
                validationCheck.setSelected(false);
            }

            if (!err.equals("")) {
                scanOut.setText("");
                parseTreeOut.setText("");
                codeOut.setText("");
                executeOut.setText("");
                validationOut.setText(err);
                tabPane.getSelectionModel().select(4);
                Platform.runLater(() -> validationOut.requestFocus());
            } else {
                parseTreeOut.setText(program.getParseTree());
                codeOut.setText(program.toString());
                executeOut.setText(interpreter.getOutput().stream().collect(Collectors.joining("\n")));
                validationOut.setText("- lexical validation is fine\n- syntax validation is fine\n- semantic validation is fine\n- no runtime errors");
                if (opneExec) {
                    tabPane.getSelectionModel().select(3);
                }
                Platform.runLater(() -> executeOut.requestFocus());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Program getProgram(JParser parser) throws Exception {
        Symbol root = null;
        while (!parser.yyatEOF()) {
            root = parser.parse();
        }
        return (Program) root.value;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
        try (InputStream in = AppController.class.getClassLoader().getResourceAsStream("samples/hello_world.txt")) {
            File file = new File("output.txt");
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            uploadFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> input.requestFocus());
    }
}

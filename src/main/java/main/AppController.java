package main;

import exceptions.GrammarException;
import exceptions.ScanException;
import execution.Interpreter;
import java_cup.runtime.Symbol;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import parser.JParser;
import parser.parsetree.Program;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.*;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the class responsible for the graphical user interface.
 * The user interface is designed as IDE for our toy programming language. It will allow
 * to enter code in multiple ways, but also to run and validate it.
 */
public class AppController implements Initializable {

    @FXML
    BorderPane borderPane;

    @FXML
    SplitPane splitPane;

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
    CheckBox runtimeCheck;

    @FXML
    Button upload;

    @FXML
    ComboBox<String> demoFiles;

    @FXML
    Button theme;

    @FXML
    Button split;

    @FXML
    Button help;

    private static final String ENCODING = "UTF-8";
    private static final Logger LOG = Logger.getLogger(String.class.getName());
    private FileChooser fileChooser = new FileChooser();
    private List<String> demoFileList = new ArrayList<>();
    private boolean isDarkTheme = true;
    private String appCss = Objects.requireNonNull(Main.class.getClassLoader().getResource("css/application.css")).toExternalForm();
    private String darkTheme = Objects.requireNonNull(Main.class.getClassLoader().getResource("css/darkTheme.css")).toExternalForm();
    private String lightTheme = Objects.requireNonNull(Main.class.getClassLoader().getResource("css/lightTheme.css")).toExternalForm();

    /**
     * This method will initialize the graphic interface and according controls from an fxml file.
     *
     * @param location  the location.
     * @param resources the resources.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeUI();
        setDemoFile("hello_world");     // initialize with demo code
    }

    /**
     * This awfully large method will be called when a code is validated. It will grab the available code,
     * scan, parse, validate and run it, and then visualize the according results on the ui.
     *
     * @param showExecutionResultTab determines, if the execution result tab is opened and focused or not.
     */
    private void process(boolean showExecutionResultTab) {
        StringWriter stackTraceWriter = null;
        String cleansedCode = cleanse(input.getText(), true);                                 // code cleansing
        try (InputStream stream = new ByteArrayInputStream(cleansedCode.getBytes()); InputStreamReader reader = new InputStreamReader(stream, ENCODING)) {
            setCheckResultHintSelected(true);                                           // selects all validation checkboxes

            Program program = null;
            String parseTree = null;
            Interpreter interpreter = null;
            String err = "";
            Symbol root;
            JParser parser = new JParser(reader, true);

            try {                                                                       // process code
                root = parser.parse();
                program = (Program) root.value;
                parseTree = program.getParseTree();
                Platform.runLater(() -> scanOut.setText(String.join("\n", parser.getScannerOutput())));
                interpreter = new Interpreter();
                if (program != null) {
                    program.accept(interpreter);
                }
            } catch (Exception e) {                                                     // from here, exception handling starts
                String message = e.getMessage();
                markExceptionText(parser, message);
                if (e instanceof GrammarException) {
                    if (message.contains("Infinity") || message.contains("NaN")) {
                        err = "During runtime, an arithmetic operation resulted in an invalid numeric value!";
                        Platform.runLater(() -> runtimeCheck.setSelected(false));
                    } else {
                        err = "Parsed code semantically not valid (" + message + ")!";
                        Platform.runLater(() -> {
                            validationCheck.setSelected(false);
                            runtimeCheck.setSelected(false);
                        });
                    }
                } else {
                    err = "Parsed code syntax not valid, parse tree could not be constructed!";
                    Platform.runLater(() -> {
                        validationCheck.setSelected(false);
                        parseCheck.setSelected(false);
                        runtimeCheck.setSelected(false);
                    });
                }
                stackTraceWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTraceWriter));
                e.printStackTrace();
            } catch (Error e) {                                                         // if an Error occurs, it's source is from the scanner or from overflow
                markErrorText(parser);
                Throwable t = new ScanException(e.getMessage(), e);
                String message = t.getMessage();
                if (message == null) {
                    err = "StackOverflowError during execution occurred!";
                    Platform.runLater(() -> runtimeCheck.setSelected(false));
                } else {
                    err = "Scanned code not valid (" + message + ")!";
                    Platform.runLater(() -> {
                        lexCheck.setSelected(false);
                        parseCheck.setSelected(false);
                        validationCheck.setSelected(false);
                        runtimeCheck.setSelected(false);
                    });
                }
                stackTraceWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTraceWriter));
                e.printStackTrace();
            } finally {
                if (!err.equals("")) {
                    LOG.log(Level.WARNING, err);
                }
                if (stackTraceWriter != null) {
                    err += "\n\n\nstackTrace:\n" + stackTraceWriter.toString();
                }

                showResultOnGui(err, program, parseTree, interpreter, showExecutionResultTab);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showResultOnGui(String err, Program program, String parseTree, Interpreter interpreter, boolean showExecutionResultTab) {

        if (!err.equals("")) {
            if (program != null) {
                parseTreeOut.setText(parseTree);
                codeOut.setText(program.toString());
            } else {
                parseTreeOut.setText("");
                codeOut.setText("");
            }
            if (interpreter != null) {
                executeOut.setText(String.join("\n", interpreter.getOutput()));
            } else {
                executeOut.setText("");
            }
            validationOut.setText(err);
            tabPane.getSelectionModel().select(4);
            Platform.runLater(() -> validationOut.requestFocus());
        } else {
            if (program != null) {
                parseTreeOut.setText(parseTree);
                codeOut.setText(program.toString());
            }
            if (interpreter != null) {
                executeOut.setText(String.join("\n", interpreter.getOutput()));
            }
            validationOut.setText("everything is fine :)");
            if (showExecutionResultTab) {
                tabPane.getSelectionModel().select(3);
                Platform.runLater(() -> executeOut.requestFocus());
            }

        }
    }

    private void markExceptionText(JParser parser, String message) {
        if (message.contains("location")) {
            String[] loc = message.substring(message.indexOf("location")).split(",");
            int left = Integer.parseInt(loc[0].replaceAll("[^0-9]", ""));
            int right = Integer.parseInt(loc[1].replaceAll("[^0-9]", ""));
            markTextTo(left, right);
        } else {
            try {
                Symbol currentToken = parser.getCurrentToken();
                markTextTo(currentToken.left, currentToken.right);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void markErrorText(JParser parser) {
        try {
            Symbol currentToken = parser.getCurrentToken();
            if (currentToken == null) {
                Platform.runLater(() -> markText(0));
            } else {
                Platform.runLater(() -> markText(currentToken.right + 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will pre-process the code-to-validate by removing comments.
     * This will facilitate further processing.
     *
     * @return the cleansed code ready to process.
     */
    private String cleanse(String code, boolean all) {
        String pattern = "//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/";
        if (all) {
            return code.replaceAll(pattern, "$1 ");
        }
        return code.replaceFirst(pattern, "$1 ");
    }

    private String removeWhitespace(String code, boolean all) {
        String pattern = "[ \t]+(\r\n?|\n)";
        if (all) {
            return code.replaceAll(pattern, "$1 ");
        }
        return code.replaceFirst(pattern, "$1 ");
    }

    // ------------------------------------------ ui handling ------------------------------------------

    /**
     * This method takes care about the initialization of specific gui components.
     */
    private void initializeUI() {
        tabPane.getSelectionModel().select(3);
        scanOut.setEditable(false);
        parseTreeOut.setEditable(false);
        codeOut.setEditable(false);
        executeOut.setEditable(false);
        validationOut.setEditable(false);

        setCheckResultHintSelected(true);

        input.textProperty().addListener((o, nv, ov) -> {
            input.deselect();
            demoFiles.getSelectionModel().selectFirst();
            setCheckResultHintSelected(false);
            if (input.getText().length() > 2 && input.getText().substring(input.getText().length() - 2).equals("\n\n")) {
                process(false);
            }
            Platform.runLater(() -> input.requestFocus());
        });

        start.setOnAction(e -> process(true));

        help.setOnAction(e -> {
            String url = "https://github.com/lpapailiou/fac#run-with-gui";
            try {
                URI uri = new URI(url);
                Desktop.getDesktop().browse(uri);
            } catch (URISyntaxException | IOException ex) {
                launchDialog("Oopsie...", "The URL " + url + " leading to an online help could not be opened in your browser.");
            }
        });

        initializeDemoFileSelector();

        split.setOnAction(e -> {
            if (splitPane.getOrientation() == Orientation.HORIZONTAL) {
                splitPane.setOrientation(Orientation.VERTICAL);
            } else {
                splitPane.setOrientation(Orientation.HORIZONTAL);
            }
            splitPane.setDividerPositions(0.5);
        });

    }

    /**
     * This is a helper method to set all check indicators of the right-hand-side to a
     * selected/not selected easily.
     *
     * @param selected true if the check indicators should be selected.
     */
    private void setCheckResultHintSelected(boolean selected) {
        lexCheck.setSelected(selected);
        parseCheck.setSelected(selected);
        validationCheck.setSelected(selected);
        runtimeCheck.setSelected(selected);
    }

    /**
     * This method will create a simple dialog with a custom title and message.
     *
     * @param title   the custom title.
     * @param message the custom message.
     */
    private void launchDialog(String title, String message) {
        GaussianBlur blurEffect = new GaussianBlur(2);
        borderPane.setEffect(blurEffect);
        Dialog<String> dialog = new Dialog<>();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("icon.png"));
        dialog.getDialogPane().getStylesheets().add(appCss);
        dialog.getDialogPane().getStylesheets().add(isDarkTheme ? darkTheme : lightTheme);
        dialog.setTitle(title);
        ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().add(type);
        dialog.setResultConverter(b -> null);
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(r -> borderPane.setEffect(null));
        if (!result.isPresent()) {
            borderPane.setEffect(null);
        } else {
            borderPane.setEffect(null);
        }
    }

    /**
     * This method will initialize the combobox values and behavior for the demo file selector.
     * It covers the execution by IDE and by jar.
     */
    private void initializeDemoFileSelector() {
        final String demoFileName = "... select demo file";
        final String samplesDirectory = "samples/";
        try (InputStream in = getResourceAsStream(samplesDirectory); BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;
            while ((resource = br.readLine()) != null) {
                demoFileList.add(resource.replaceAll(".txt", "").replaceAll("_", " "));
            }
            if (demoFileList.isEmpty()) {       // case jar file
                URI uri = AppController.class.getProtectionDomain().getCodeSource().getLocation().toURI();
                Enumeration jar = new JarFile(new File(uri)).entries();
                while (jar.hasMoreElements()) {
                    String element = jar.nextElement().toString();
                    if (element.contains(samplesDirectory) && !element.equals(samplesDirectory)) {
                        demoFileList.add(element.replaceAll(".txt", "").replaceAll("_", " ").replaceAll(samplesDirectory, ""));
                    }
                }
            }

            demoFiles.getItems().add(demoFileName);
            demoFiles.getItems().addAll(demoFileList);
            demoFiles.getSelectionModel().selectFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }

        demoFiles.setOnAction(e -> {
            String value = demoFiles.getValue();
            if (!value.equals(demoFileName)) {
                setDemoFile(value);
            }
        });
    }

    /**
     * This is a helper method to upload the content of a demo file to the input textarea.
     * It will create a temp file, which will be deleted after the upload.
     *
     * @param fileName the file name of the demo file to upload.
     */
    private void setDemoFile(String fileName) {
        try (InputStream in = AppController.class.getClassLoader().getResourceAsStream("samples/" + fileName.replaceAll(" ", "_") + ".txt")) {
            File file = File.createTempFile("fac_tmp_code_upload", ".txt");
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            uploadFile(file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> input.requestFocus());
    }

    /**
     * This helper method is responsible to upload a file content to the input textarea.
     *
     * @param file the file to upload.
     */
    private void uploadFile(File file) {
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder text = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    text.append(line);
                    text.append("\n");
                }
                Platform.runLater(() -> input.setText(text.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This is a helper method to access resources, both by IDE and by jar.
     *
     * @param resource the resource to access.
     * @return the input stream to process.
     */
    private InputStream getResourceAsStream(String resource) {
        final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    /**
     * This is a helper method to initialize the file chooser to upload files.
     * As the stage is required, it will be called from outside this controller.
     * @param stage the stage to pass.
     */
    void initializeFileChooser(Stage stage) {
        if (stage == null) {
            return;
        }
        upload.setOnAction(ev -> {
            demoFiles.getSelectionModel().selectFirst();
            File file = fileChooser.showOpenDialog(stage);
            uploadFile(file);
        });
    }

    /**
     * This is a helper method to initialize the theme switcher.
     * As the stage is required, it will be called from outside this controller.
     * @param stage the stage to pass.
     */
    void initializeThemeControl(Stage stage) {
        if (stage == null) {
            return;
        }
        theme.setOnAction(e -> {
            if (isDarkTheme) {
                stage.getScene().getStylesheets().removeIf(s -> s.matches(Objects.requireNonNull(darkTheme)));
                stage.getScene().getStylesheets().add(lightTheme);
                Platform.runLater(() -> theme.setText("dark theme"));
            } else {
                stage.getScene().getStylesheets().removeIf(s -> s.matches(Objects.requireNonNull(lightTheme)));
                stage.getScene().getStylesheets().add(darkTheme);
                Platform.runLater(() -> theme.setText("light theme"));
            }
            isDarkTheme = !isDarkTheme;
        });
    }

    /**
     * This method allows to mark a specific token. It is used to indicate where scanner errors happen.
     *
     * @param from the start index of the error token.
     */
    private void markText(int from) {
        if (input.isFocused()) {
            return;
        }
        Platform.runLater(() -> {
            int fromIndex = Math.max(from, 0);
            String text = input.getText();
            String cleansedText = cleanse(text, true);
            int skipped = 0;
            while (!text.substring(0, fromIndex).equals(cleansedText.substring(0, fromIndex))) {
                int length = text.length();
                text = cleanse(text, false);
                skipped += length - text.length();
            }
            fromIndex = fromIndex + skipped;
            input.selectRange(fromIndex, Math.min(input.getText().indexOf(" ", fromIndex), input.getText().indexOf("\n", fromIndex)));
        });
    }

    /**
     * This method allows to mark a specific token. It is used to indicate where scanner errors happen.
     *
     * @param from the start index of the error token.
     */
    private void markTextTo(int from, int to) {
        if (input.isFocused()) {
            return;
        }
        Platform.runLater(() -> {
            int fromIndex = Math.max(from, 0);
            int toIndex = Math.max(to, 0);
            String text = input.getText();
            String cleansedText = cleanse(text, true);
            int skipped = 0;
            while (!text.substring(0, fromIndex).equals(cleansedText.substring(0, fromIndex))) {
                int length = text.length();
                text = cleanse(text, false);
                skipped += length - text.length();
            }
            fromIndex = fromIndex + skipped;
            while (!text.substring(0, toIndex).equals(cleansedText.substring(0, toIndex))) {
                int length = text.length();
                text = cleanse(text, false);
                skipped += length - text.length();
            }
            toIndex = toIndex + skipped;
            input.selectRange(fromIndex, toIndex);
        });
    }

}

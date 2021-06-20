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
 * This is the main class of this application. Its purpose is to start code processing.
 * The Main can be run in different modes.
 * After processing, the program will remain running. A small menu allows to change mode and continue processing.
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeUI();
        setDemoFile("hello_world");
    }

    private void process(boolean openExec) {
        String cleansedCode = cleanse();
        try (InputStream stream = new ByteArrayInputStream(cleansedCode.getBytes()); InputStreamReader reader = new InputStreamReader(stream, ENCODING)) {
            lexCheck.setSelected(true);
            parseCheck.setSelected(true);
            validationCheck.setSelected(true);
            runtimeCheck.setSelected(true);

            Program program = null;
            Interpreter interpreter = null;
            String err = "";

            try {
                JParser parser = new JParser(reader, true);
                Symbol root = null;
                while (!parser.yyatEOF()) {
                    root = parser.parse();
                }
                program = (Program) root.value;
                scanOut.setText(String.join("\n", parser.getScannerOutput()));

                interpreter = new Interpreter();
                if (program != null) {
                    program.accept(interpreter);
                }
            } catch (Exception e) {
                if (e instanceof GrammarException) {
                    String message = e.getMessage();
                    err = "Parsed code semantically not valid (" + message + ")!";
                    LOG.log(Level.WARNING, err);

                    if (message.contains("Infinity") || message.contains("NaN")) {
                        err = "During runtime, an arithmetic operation resulted in an invalid numeric value!";
                        runtimeCheck.setSelected(false);
                    } else {
                        validationCheck.setSelected(false);
                        runtimeCheck.setSelected(false);
                    }
                } else {
                    StringWriter stackTraceWriter = new StringWriter();
                    e.printStackTrace(new PrintWriter(stackTraceWriter));
                    err = "Parsed code syntax not valid!\n" + stackTraceWriter.toString();
                    LOG.log(Level.WARNING, "Parsed code syntax not valid!");
                    validationCheck.setSelected(false);
                    parseCheck.setSelected(false);
                    runtimeCheck.setSelected(false);
                }
            } catch (Error e) {
                Throwable t = new ScanException(e.getMessage(), e);
                String message = t.getMessage();

                LOG.log(Level.WARNING, "Scanned code not valid (" + message + ")!");
                if (message == null) {
                    LOG.log(Level.WARNING, "Scanned code not valid (" + message + ")!");
                    runtimeCheck.setSelected(false);
                    err = "Overflow during execution occurred!";
                } else {
                    err = "Scanned code not valid (" + message + ")!";
                    lexCheck.setSelected(false);
                    parseCheck.setSelected(false);
                    validationCheck.setSelected(false);
                    runtimeCheck.setSelected(false);
                }
            }

            if (!err.equals("")) {
                if (program != null) {
                    parseTreeOut.setText(program.getParseTree());
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
                assert program != null;
                parseTreeOut.setText(program.getParseTree());
                codeOut.setText(program.toString());
                assert interpreter != null;
                executeOut.setText(String.join("\n", interpreter.getOutput()));
                validationOut.setText("everything is fine :)");
                if (openExec) {
                    tabPane.getSelectionModel().select(3);
                }
                Platform.runLater(() -> executeOut.requestFocus());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String cleanse() {
        String code = input.getText();
        String[] lines = code.split("\n");
        StringBuilder bld = new StringBuilder();
        for (String line : lines) {
            if (line.contains("//")) {
                if (line.startsWith("//")) {
                    continue;
                }
                line = line.substring(0, line.indexOf("//") - 1);
            }
            bld.append(line);
            bld.append("\n");
        }
        return bld.toString();
    }

    // ------------------------------------------ ui handling ------------------------------------------



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

    private InputStream getResourceAsStream(String resource) {
        final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);

        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    private void initializeUI() {
        tabPane.getSelectionModel().select(3);
        scanOut.setEditable(false);
        parseTreeOut.setEditable(false);
        codeOut.setEditable(false);
        executeOut.setEditable(false);
        validationOut.setEditable(false);

        lexCheck.setDisable(true);
        parseCheck.setDisable(true);
        validationCheck.setDisable(true);
        runtimeCheck.setDisable(true);

        input.textProperty().addListener((o, nv, ov) -> {
            demoFiles.getSelectionModel().selectFirst();
            if (input.getText().substring(input.getText().length() - 2).equals("\n\n")) {
                process(false);
            }
            Platform.runLater(() -> input.requestFocus());
        });

        start.setOnAction(e -> process(true));

        help.setOnAction(e -> setUpDialog());

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

    private void initializeDemoFileSelector() {
        final String demoFileName = "... select demo file";
        final String samplesDirectory = "samples/";
        try (InputStream in = getResourceAsStream(samplesDirectory); BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;
            while ((resource = br.readLine()) != null) {
                demoFileList.add(resource.replaceAll(".txt", "").replaceAll("_", " "));
            }
            if (demoFileList.isEmpty()) {
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

    private void setUpDialog() {
        String url = "https://github.com/lpapailiou/fac#readme";        // TODO: set correct pointer
        try {
            URI uri = new URI(url);
            Desktop.getDesktop().browse(uri);
        } catch (URISyntaxException | IOException ex) {
            GaussianBlur blurEffect = new GaussianBlur(2);
            borderPane.setEffect(blurEffect);
            Dialog<String> dialog = new Dialog<>();
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("icon.png"));
            dialog.getDialogPane().getStylesheets().add(appCss);
            dialog.getDialogPane().getStylesheets().add(isDarkTheme ? darkTheme : lightTheme);
            dialog.setTitle("Oopsie...");
            ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            dialog.setContentText("The URL " + url + " leading to an online help could not be opened in your browser.");
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
    }

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

}

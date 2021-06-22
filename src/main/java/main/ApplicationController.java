package main;

import execution.Mode;
import execution.Processor;
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

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This is the class responsible for the graphical user interface.
 * The user interface is designed as IDE for our toy programming language. It will allow
 * to enter code in multiple ways, but also to run and validate it.
 */
public class ApplicationController implements Initializable {

    private final FileChooser fileChooser = new FileChooser();
    private final List<String> demoFileList = new ArrayList<>();
    private final String appCss = Objects.requireNonNull(Main.class.getClassLoader().getResource("css/application.css")).toExternalForm();
    private final String darkTheme = Objects.requireNonNull(Main.class.getClassLoader().getResource("css/darkTheme.css")).toExternalForm();
    private final String lightTheme = Objects.requireNonNull(Main.class.getClassLoader().getResource("css/lightTheme.css")).toExternalForm();
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
    private boolean isDarkTheme = true;

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
     * This method will trigger the processing of the code. After processing, the according results are
     * extracted and visualized on the ui.
     *
     * @param showExecutionResultTab determines, if the execution result tab will be opened and focused or not.
     */
    private void process(boolean showExecutionResultTab) {
        String cleansedCode = cleanse(input.getText(), true);
        Processor processor = new Processor(Mode.GUI, cleansedCode);

        lexCheck.setSelected(processor.isLexCheckSuccessful());
        parseCheck.setSelected(processor.isParseCheckSuccessful());
        validationCheck.setSelected(processor.isValidationCheckSuccessful());
        runtimeCheck.setSelected(processor.isRuntimeCheckSuccessful());

        scanOut.setText(processor.getScannerOutput());
        parseTreeOut.setText(processor.getParseTree());
        codeOut.setText(processor.getParseCode());
        executeOut.setText(processor.getExecutionResult());
        validationOut.setText(processor.getErrorMessage());

        if (processor.isExceptionThrown() || processor.isErrorThrown()) {
            tabPane.getSelectionModel().select(4);
            Platform.runLater(() -> validationOut.requestFocus());
            if (processor.isExceptionThrown()) {
                markExceptionText(processor.getCurrentToken(), processor.getLocation());
            } else if (processor.isErrorThrown()) {
                markErrorText(processor.getCurrentToken());
            }
        } else {
            if (showExecutionResultTab) {
                tabPane.getSelectionModel().select(3);
                Platform.runLater(() -> executeOut.requestFocus());
            }
        }

    }

    /**
     * This method will pre-process the code-to-validate by removing comments.
     * This is useful to reconstruct error locations, as the scanner will skip comments as well (i.e.
     * the error location will not match with the source file without this processing step).
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

    /**
     * This is a helper method to upload the content of a demo file to the input textarea.
     * It will create a temp file, which will be deleted after the upload.
     *
     * @param fileName the file name of the demo file to upload.
     */
    private void setDemoFile(String fileName) {
        try (InputStream in = ApplicationController.class.getClassLoader().getResourceAsStream("samples/" + fileName.replaceAll(" ", "_") + ".txt")) {
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
     * This helper method is responsible to upload a text from a file to the input textarea.
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
     * This is a helper method to access project resources, both by IDE and by jar.
     *
     * @param resource the resource to access.
     * @return the input stream to process.
     */
    private InputStream getResourceAsStream(String resource) {
        final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    // ------------------------------------------ ui handling ------------------------------------------

    /**
     * This method takes care about the initialization of specific gui components of the main stage.
     */
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
            input.deselect();
            demoFiles.getSelectionModel().selectFirst();
            setCheckResultHintSelected(false);
            if (input.getText().length() > 2 && input.getText().endsWith("\n\n")) {
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
        borderPane.setEffect(null);
    }

    /**
     * This method will initialize the combo box values and behavior for the demo file selector.
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
                URI uri = ApplicationController.class.getProtectionDomain().getCodeSource().getLocation().toURI();
                Enumeration<JarEntry> jar = new JarFile(new File(uri)).entries();
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
     * This is a helper method to initialize the file chooser to upload files.
     * As the stage is required, it will be called from outside this controller.
     *
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
     *
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
     * This method will handle the selection of a text section within the input area.
     * It is triggered in case of exceptions (probably by the parser).
     *
     * @param token    the last token which was processed.
     * @param location the location of the exception, if present.
     */
    private void markExceptionText(Symbol token, int[] location) {
        if (!Arrays.equals(location, new int[]{0, 0})) {
            markText(location[0], location[1]);
        } else {
            if (token != null) {
                markText(token.left, token.right);
            }
        }
    }

    /**
     * This method will handle the selection of a text section within the input area.
     * It is triggered in case of an error (probably by the scanner).
     *
     * @param token the last token which was processed.
     */
    private void markErrorText(Symbol token) {
        if (token == null) {
            Platform.runLater(() -> markToken(0));
        } else {
            Platform.runLater(() -> markToken(token.right + 1));
        }
    }

    /**
     * This method allows to mark a specific token. It is used to indicate where scanner errors happen.
     *
     * @param startIndex the start index of the error token.
     */
    private void markToken(int startIndex) {
        if (input.isFocused()) {
            return;
        }
        Platform.runLater(() -> {
            int fromIndex = Math.max(startIndex, 0);
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
     * This method allows to mark a specific text section. It is used to indicate where parser errors happen.
     *
     * @param startIndex the start index of the exception cause.
     * @param endIndex   the end index of the exception cause.
     */
    private void markText(int startIndex, int endIndex) {
        if (input.isFocused()) {
            return;
        }
        Platform.runLater(() -> {
            int fromIndex = Math.max(startIndex, 0);
            int toIndex = Math.max(endIndex, 0);
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

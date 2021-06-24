package main;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Objects;

/**
 * This is the main class of this application. Its purpose is to start code processing.
 * This program can be run on console or with a graphic user interface.
 * To start the console mode, the program must be started with any argument.
 * After processing, the program will remain running. A menu will allow further navigation.
 */
public class Main extends Application {

    private static boolean isStartedByConsole;
    private static Stage stage;
    private static String[] args;

    /**
     * The main method is the entry point for this program. It will take a decision whether the program should be
     * started in a console or if a gui is to be created.
     *
     * @param args the passed arguments for starting
     */
    public static void main(String... args) {
        try {
            if (System.console() != null || (args != null && args.length > 0)) {
                isStartedByConsole = true;
                Main.args = args;
            }
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is responsible for the initialization of the graphic user interface.
     * It will mainly initialize the only fxml controller used to do so.
     */
    static void initializeGui() {
        try {
            ClassLoader classLoader = Main.class.getClassLoader();
            FXMLLoader loader = new FXMLLoader(classLoader.getResource("fxml/application.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1600, 800);
            scene.getStylesheets().add(Objects.requireNonNull(classLoader.getResource("css/application.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(classLoader.getResource("css/darkTheme.css")).toExternalForm());
            stage.setScene(scene);
            stage.setMinHeight(400);
            stage.setMinWidth(800);
            stage.setTitle("JLANG | the toy programming language playground");
            stage.getIcons().add(new Image("icon.png"));
            ApplicationController controller = loader.getController();
            controller.initializeFileChooser(stage);
            controller.initializeThemeControl(stage);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will take care about the start of the graphic user interface.
     *
     * @param stage the stage to pass.
     */
    @Override
    public void start(Stage stage) {
        try {
            Main.stage = stage;
            if (isStartedByConsole) {
                new ConsoleController().startTerminal(args);
            } else {
                initializeGui();
            }
        } catch (Exception e) {
            e.printStackTrace();
            stop();
        }
    }

    /**
     * This method allows the program to close in a clean way.
     */
    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }

}

package main;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * This is the main class of this application. Its purpose is to start code processing.
 * The Main can be run in different modes.
 * After processing, the program will remain running. A small menu allows to change mode and continue processing.
 */
public class Main extends Application {

    public static void main(String... args) {
        try {
            if (System.console() != null) {
                Console.startTerminal(args);
            } else {
                launch(args);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        ClassLoader classLoader = Main.class.getClassLoader();
        FXMLLoader loader = new FXMLLoader(classLoader.getResource("fxml/application.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1600, 800);
        scene.getStylesheets().add(classLoader.getResource("css/application.css").toExternalForm());
        scene.getStylesheets().add(classLoader.getResource("css/darkTheme.css").toExternalForm());
        stage.setScene(scene);
        stage.setMinHeight(400);
        stage.setMinWidth(800);
        stage.setTitle("JLANG | the toy programming language playground");
        stage.getIcons().add(new Image("icon.png"));
        AppController controller = loader.getController();
        controller.initializeFileChooser(stage);
        controller.initializeThemeControl(stage);
        stage.show();
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }

}

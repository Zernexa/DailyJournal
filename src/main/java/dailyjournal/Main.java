package dailyjournal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.awt.*;

public class Main extends Application {

    public static void main (String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception{

        Group root = new Group();
        Scene scene = new Scene(root, Color.BLACK);

        Image icon = new Image("file:C:/Users/Radu/IdeaProjects/DailyJournal/src/icon.png");
        stage.getIcons().add(icon);
        stage.setTitle("DailyJournal");
        stage.setWidth(700);
        stage.setHeight(700);
        stage.setResizable(false);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("Cioaca (c)");
        stage.setFullScreenExitKeyCombination(KeyCombination.valueOf("c"));

        stage.setScene(scene);
        stage.show();
    }
}

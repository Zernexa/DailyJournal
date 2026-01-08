package dailyjournal;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML private Button routineEdit;

    @FXML
    private void onNewEntry(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("entryCreation.fxml"));
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void onRoutineEdit(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("routineManagement.fxml"));
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}

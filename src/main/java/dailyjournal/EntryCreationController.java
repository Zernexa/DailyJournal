package dailyjournal;

import java.time.LocalDateTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.FlowPane;

public class EntryCreationController {

    @FXML private DatePicker entryDatePicker;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;
    @FXML private Button nowBtn;
    @FXML private FlowPane routinesFlow;

    @FXML
    private void initialize() {
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0)); // Setam valoarea spinnerului de ora sa fie intre 0 si 23
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0)); // Setam valoarea spinnerului de minute sa fie intre 0 si 59

        nowBtn.setOnAction(e -> setNow());

        setNow(); // Pentru autocomplete cand deschizi pagina
        loadRoutines();
    }

    private void loadRoutines() {
        List<String> routines = RoutineStorage.loadRoutines();
        routinesFlow.getChildren().clear();
        for (String routine : routines) {
            CheckBox cb = new CheckBox(routine);
            routinesFlow.getChildren().add(cb);
        }
    }

    private void setNow() {
        LocalDateTime now = LocalDateTime.now();
        entryDatePicker.setValue(now.toLocalDate());
        hourSpinner.getValueFactory().setValue(now.getHour());
        minuteSpinner.getValueFactory().setValue(now.getMinute());
    }

    @FXML
    private void onExitEntryCreation(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void onSaveNewEntry(ActionEvent event) throws IOException {
        System.out.println("Am salvat :)");
    }
}
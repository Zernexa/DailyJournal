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

import java.util.UUID;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.util.stream.Collectors;

public class EntryCreationController {

    @FXML private DatePicker entryDatePicker;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;
    @FXML private Button nowBtn;
    @FXML private FlowPane routinesFlow;
    @FXML private TextField titleField;
    @FXML private TextArea notesField;

    private JournalEntry currentEntry;

    @FXML
    private void initialize() {
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        nowBtn.setOnAction(e -> setNow());
    }

    public void setEntry(JournalEntry entry) {
        this.currentEntry = entry;
        if (entry != null) {
            LocalDateTime dateTime;
            try {
                dateTime = LocalDateTime.parse(entry.getDate());
            } catch (Exception e) {
                dateTime = LocalDateTime.now();
            }
            entryDatePicker.setValue(dateTime.toLocalDate());
            hourSpinner.getValueFactory().setValue(dateTime.getHour());
            minuteSpinner.getValueFactory().setValue(dateTime.getMinute());
            titleField.setText(entry.getTitle());
            notesField.setText(entry.getNotes());
            
            routinesFlow.getChildren().clear();
            for (RoutineStatus status : entry.getRoutines()) {
                CheckBox cb = new CheckBox(status.getName());
                cb.setSelected(status.isCompleted());
                routinesFlow.getChildren().add(cb);
            }
        } else {
            setNow();
            loadRoutines();
        }
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
        String title = titleField.getText();
        String notes = notesField.getText();
        LocalDateTime dateTime = LocalDateTime.of(entryDatePicker.getValue(), 
                                                  java.time.LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue()));
        
        List<RoutineStatus> routines = routinesFlow.getChildren().stream()
                .filter(node -> node instanceof CheckBox)
                .map(node -> {
                    CheckBox cb = (CheckBox) node;
                    return new RoutineStatus(cb.getText(), cb.isSelected());
                })
                .collect(Collectors.toList());

        if (currentEntry == null) {
            currentEntry = new JournalEntry(UUID.randomUUID().toString(), title, dateTime.toString(), notes, routines);
        } else {
            currentEntry.setTitle(title);
            currentEntry.setDate(dateTime.toString());
            currentEntry.setNotes(notes);
            currentEntry.setRoutines(routines);
        }

        JournalStorage.saveOrUpdateEntry(currentEntry);
        
        onExitEntryCreation(event);
    }
}
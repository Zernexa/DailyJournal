package dailyjournal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.ArrayList;

public class RoutineEditController {

    @FXML private TextField newRoutineField;
    @FXML private ListView<String> routineListView;

    private ObservableList<String> routineList;

    @FXML
    private void initialize() {
        routineList = FXCollections.observableArrayList(RoutineStorage.loadRoutines());
        routineListView.setItems(routineList);
        
        routineListView.setCellFactory(lv -> {
            TextFieldListCell<String> cell = new TextFieldListCell<>();
            
            cell.setConverter(new StringConverter<String>() {
                @Override public String toString(String object) { return object; }
                @Override public String fromString(String string) { return string; }
            });

            ContextMenu contextMenu = new ContextMenu();
            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(event -> cell.startEdit());
            
            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> {
                routineList.remove(cell.getItem());
                RoutineStorage.saveRoutines(new ArrayList<>(routineList));
            });

            contextMenu.getItems().addAll(editItem, deleteItem);

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });

            return cell;
        });

        routineListView.setOnEditCommit(event -> {
            int index = event.getIndex();
            String newValue = event.getNewValue().trim();
            if (newValue.isEmpty()) {
                routineList.remove(index);
            } else {
                routineList.set(index, newValue);
            }
            RoutineStorage.saveRoutines(new ArrayList<>(routineList));
        });
    }

    @FXML
    private void onAddRoutine() {
        String newRoutine = newRoutineField.getText().trim();
        if (!newRoutine.isEmpty()) {
            routineList.add(newRoutine);
            newRoutineField.clear();
            RoutineStorage.saveRoutines(new ArrayList<>(routineList));
        }
    }

    @FXML
    private void onBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}

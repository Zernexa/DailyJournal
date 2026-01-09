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

import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.scene.control.TextField;

public class MainController {

    @FXML private Button routineEdit;
    @FXML private ListView<JournalEntry> entriesListView;
    @FXML private TextField searchField;

    @FXML
    private void initialize() {
        loadEntries();
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEntries(newValue);
        });
    }

    private void filterEntries(String query) {
        if (query == null || query.isEmpty()) {
            loadEntries();
        } else {
            List<JournalEntry> allEntries = JournalStorage.loadEntries();
            List<JournalEntry> filtered = allEntries.stream()
                    .filter(e -> e.getTitle().toLowerCase().contains(query.toLowerCase()) || 
                                 e.getNotes().toLowerCase().contains(query.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
            entriesListView.setItems(FXCollections.observableArrayList(filtered));
        }
    }

    private void loadEntries() {
        List<JournalEntry> entries = JournalStorage.loadEntries();
        entriesListView.setItems(FXCollections.observableArrayList(entries));
        
        entriesListView.setCellFactory(param -> new ListCell<JournalEntry>() {
            @Override
            protected void updateItem(JournalEntry entry, boolean empty) {
                super.updateItem(entry, empty);
                if (empty || entry == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox container = new VBox(5);
                    
                    HBox topRow = new HBox(10);
                    Label titleLabel = new Label(entry.getTitle());
                    titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 1.1em;");
                    
                    LocalDateTime dt;
                    try {
                        dt = LocalDateTime.parse(entry.getDate());
                    } catch (Exception e) {
                        dt = LocalDateTime.now();
                    }
                    Label timeLabel = new Label("(" + dt.format(DateTimeFormatter.ofPattern("HH:mm")) + ")");
                    timeLabel.setStyle("-fx-text-fill: gray;");
                    
                    Button editBtn = new Button("Edit");
                    editBtn.setOnAction(e -> editEntry(entry));

                    Button deleteBtn = new Button("Delete");
                    deleteBtn.setStyle("-fx-text-fill: red;");
                    deleteBtn.setOnAction(e -> {
                        JournalStorage.deleteEntry(entry.getId());
                        loadEntries();
                    });

                    HBox spacer = new HBox();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    topRow.getChildren().addAll(titleLabel, timeLabel, spacer, editBtn, deleteBtn);
                    
                    Label infoLabel = new Label(dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + 
                                               " | Tasks: " + entry.getCompletedTasksCount() + "/" + entry.getRoutines().size());
                    infoLabel.setStyle("-fx-font-size: 0.8em;");
                    
                    String notes = entry.getNotes() != null ? entry.getNotes() : "";
                    String firstLineNotes = notes.split("\n")[0];
                    if (firstLineNotes.length() > 100) firstLineNotes = firstLineNotes.substring(0, 97) + "...";
                    Label notesLabel = new Label(firstLineNotes);
                    notesLabel.setStyle("-fx-font-size: 0.7em; -fx-text-fill: gray;");
                    
                    container.getChildren().addAll(topRow, infoLabel, notesLabel);
                    setGraphic(container);
                }
            }
        });
    }

    private void editEntry(JournalEntry entry) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("entryCreation.fxml"));
            Parent root = loader.load();
            EntryCreationController controller = loader.getController();
            controller.setEntry(entry);
            
            Stage stage = (Stage) entriesListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onNewEntry(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("entryCreation.fxml"));
        Parent root = loader.load();
        EntryCreationController controller = loader.getController();
        controller.setEntry(null);

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

package dailyjournal;

import java.util.ArrayList;
import java.util.List;

public class JournalEntry {
    private String id;
    private String title;
    private String date;
    private String notes;
    private List<RoutineStatus> routines;

    public JournalEntry() {
        this.routines = new ArrayList<>();
    }

    public JournalEntry(String id, String title, String date, String notes, List<RoutineStatus> routines) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.notes = notes;
        this.routines = routines;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title != null ? title : ""; }
    public void setTitle(String title) { this.title = title; }

    public String getDate() { return date != null ? date : ""; }
    public void setDate(String date) { this.date = date; }

    public String getNotes() { return notes != null ? notes : ""; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<RoutineStatus> getRoutines() { return routines; }
    public void setRoutines(List<RoutineStatus> routines) { this.routines = routines; }

    public int getCompletedTasksCount() {
        int count = 0;
        for (RoutineStatus rs : routines) {
            if (rs.isCompleted()) count++;
        }
        return count;
    }
}

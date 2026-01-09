package dailyjournal;

public class RoutineStatus {
    private String name;
    private boolean completed;

    public RoutineStatus() {}

    public RoutineStatus(String name, boolean completed) {
        this.name = name;
        this.completed = completed;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}

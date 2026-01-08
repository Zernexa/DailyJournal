package dailyjournal;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RoutineStorage {
    private static final String FILE_PATH = "routines.txt";

    public static List<String> loadRoutines() {
        List<String> routines = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return routines;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    routines.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return routines;
    }

    public static void saveRoutines(List<String> routines) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (String routine : routines) {
                writer.println(routine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package dailyjournal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JournalStorage {
    private static final String FILE_PATH = "profile.json";

    public static List<JournalEntry> loadEntries() {
        List<JournalEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String content = sb.toString().trim();
            if (content.isEmpty() || content.equals("[]")) return entries;

            if (content.startsWith("[") && content.endsWith("]")) {
                content = content.substring(1, content.length() - 1).trim();
                if (content.isEmpty()) return entries;
                
                List<String> entryStrings = new ArrayList<>();
                int braceCount = 0;
                boolean inString = false;
                StringBuilder currentEntry = new StringBuilder();
                
                for (int i = 0; i < content.length(); i++) {
                    char c = content.charAt(i);
                    if (c == '\"' && (i == 0 || content.charAt(i - 1) != '\\')) {
                        inString = !inString;
                    }
                    
                    if (!inString) {
                        if (c == '{') braceCount++;
                        if (c == '}') braceCount--;
                    }
                    
                    currentEntry.append(c);
                    
                    if (braceCount == 0 && c == '}' && !inString) {
                        entryStrings.add(currentEntry.toString().trim());
                        currentEntry = new StringBuilder();
                        while (i + 1 < content.length() && (content.charAt(i + 1) == ',' || Character.isWhitespace(content.charAt(i + 1)))) {
                            i++;
                        }
                    }
                }
                
                for (String s : entryStrings) {
                    if (s.startsWith("{")) {
                        entries.add(parseEntry(s));
                    }
                }
            }
        } catch (IOException e) {
        }
        return entries;
    }

    private static JournalEntry parseEntry(String json) {
        JournalEntry entry = new JournalEntry();
        entry.setId(unescape(extractValue(json, "id")));
        entry.setTitle(unescape(extractValue(json, "title")));
        entry.setDate(unescape(extractValue(json, "date")));
        entry.setNotes(unescape(extractValue(json, "notes")));
        
        String routinesPart = extractArray(json, "routines");
        List<RoutineStatus> routines = new ArrayList<>();
        if (!routinesPart.isEmpty()) {
            String[] routineStrings = routinesPart.split("\\},\\s*\\{");
            for (String rs : routineStrings) {
                if (!rs.startsWith("{")) rs = "{" + rs;
                if (!rs.endsWith("}")) rs = rs + "}";
                routines.add(new RoutineStatus(
                    unescape(extractValue(rs, "name")), 
                    "true".equals(extractValue(rs, "completed"))
                ));
            }
        }
        entry.setRoutines(routines);
        return entry;
    }

    private static String unescape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(i + 1);
                switch (next) {
                    case '"': sb.append('"'); i++; break;
                    case '\\': sb.append('\\'); i++; break;
                    case 'n': sb.append('\n'); i++; break;
                    case 'r': sb.append('\r'); i++; break;
                    case 't': sb.append('\t'); i++; break;
                    default: sb.append(c); break;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String extractValue(String json, String key) {
        String keyPattern = "\"" + key + "\"";
        int keyIndex = -1;
        
        int braceCount = 0;
        int bracketCount = 0;
        boolean inString = false;
        for (int i = 0; i < json.length() - keyPattern.length(); i++) {
            char c = json.charAt(i);
            
            if (!inString) {
                if (c == '{') braceCount++;
                if (c == '}') braceCount--;
                if (c == '[') bracketCount++;
                if (c == ']') bracketCount--;
                
                if (braceCount == 1 && bracketCount == 0 && json.startsWith(keyPattern, i)) {
                    keyIndex = i;
                    break;
                }
            }
            if (c == '\"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
        }
        
        if (keyIndex == -1) return "";
        
        int colonIndex = json.indexOf(":", keyIndex + keyPattern.length());
        if (colonIndex == -1) return "";
        
        int valueStart = -1;
        boolean isStringValue = false;
        for (int i = colonIndex + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (Character.isWhitespace(c)) continue;
            if (c == '\"') {
                valueStart = i + 1;
                isStringValue = true;
                break;
            } else {
                valueStart = i;
                isStringValue = false;
                break;
            }
        }
        
        if (valueStart == -1) return "";
        
        if (isStringValue) {
            int end = -1;
            for (int i = valueStart; i < json.length(); i++) {
                char c = json.charAt(i);
                if (c == '\"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                    end = i;
                    break;
                }
            }
            return end != -1 ? json.substring(valueStart, end) : "";
        } else {
            int end = -1;
            int internalBraceCount = 0;
            int internalBracketCount = 0;
            boolean internalInString = false;
            for (int i = valueStart; i < json.length(); i++) {
                char c = json.charAt(i);
                if (c == '\"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                    internalInString = !internalInString;
                }
                if (!internalInString) {
                    if (internalBraceCount == 0 && internalBracketCount == 0 && (c == ',' || c == '}')) {
                        end = i;
                        break;
                    }
                    if (c == '{') internalBraceCount++;
                    if (c == '}') internalBraceCount--;
                    if (c == '[') internalBracketCount++;
                    if (c == ']') internalBracketCount--;
                }
            }
            return end != -1 ? json.substring(valueStart, end).trim() : json.substring(valueStart).trim();
        }
    }

    private static String extractArray(String json, String key) {
        String keyPattern = "\"" + key + "\"";
        int keyIndex = -1;
        
        int braceCount = 0;
        int bracketCount = 0;
        boolean inString = false;
        for (int i = 0; i < json.length() - keyPattern.length(); i++) {
            char c = json.charAt(i);
            
            if (!inString) {
                if (c == '{') braceCount++;
                if (c == '}') braceCount--;
                if (c == '[') bracketCount++;
                if (c == ']') bracketCount--;
                
                if (braceCount == 1 && bracketCount == 0 && json.startsWith(keyPattern, i)) {
                    keyIndex = i;
                    break;
                }
            }
            if (c == '\"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
        }
        
        if (keyIndex == -1) return "";
        
        int colonIndex = json.indexOf(":", keyIndex + keyPattern.length());
        if (colonIndex == -1) return "";
        
        int bracketStart = -1;
        for (int i = colonIndex + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (Character.isWhitespace(c)) continue;
            if (c == '[') {
                bracketStart = i + 1;
                break;
            } else {
                return ""; 
            }
        }
        
        if (bracketStart == -1) return "";
        
        int internalBracketCount = 1;
        inString = false;
        for (int i = bracketStart; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '\"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
            if (!inString) {
                if (c == '[') internalBracketCount++;
                if (c == ']') internalBracketCount--;
                
                if (internalBracketCount == 0) {
                    return json.substring(bracketStart, i);
                }
            }
        }
        return "";
    }

    public static void saveEntries(List<JournalEntry> entries) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("[");
            for (int i = 0; i < entries.size(); i++) {
                writer.write(toJson(entries.get(i)));
                if (i < entries.size() - 1) writer.write(",");
            }
            writer.write("]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String toJson(JournalEntry entry) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":\"").append(entry.getId()).append("\",");
        sb.append("\"title\":\"").append(escape(entry.getTitle())).append("\",");
        sb.append("\"date\":\"").append(entry.getDate()).append("\",");
        sb.append("\"notes\":\"").append(escape(entry.getNotes())).append("\",");
        sb.append("\"routines\":[");
        for (int i = 0; i < entry.getRoutines().size(); i++) {
            RoutineStatus rs = entry.getRoutines().get(i);
            sb.append("{\"name\":\"").append(escape(rs.getName())).append("\",\"completed\":").append(rs.isCompleted()).append("}");
            if (i < entry.getRoutines().size() - 1) sb.append(",");
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    public static void deleteEntry(String id) {
        List<JournalEntry> entries = loadEntries();
        entries.removeIf(e -> e.getId().equals(id));
        saveEntries(entries);
    }

    public static void saveOrUpdateEntry(JournalEntry entry) {
        List<JournalEntry> entries = loadEntries();
        boolean found = false;
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getId().equals(entry.getId())) {
                entries.set(i, entry);
                found = true;
                break;
            }
        }
        if (!found) {
            entries.add(entry);
        }
        saveEntries(entries);
    }
}

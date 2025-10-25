import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.Path;

public final class DatabaseHelper<Model extends DatabaseModel<Model>> {
    /* TODO:
     * [x] constructor
     * [x] read/write methods
     * [x] CRUD stuff
     * [x] first(), all(), where(), count(), clear()
     * [ ] db file name/path
     * [ ] CSV parsing
     */
    private Class<Model> modelClass;
    private String[] columnHeaders;
    private ArrayList<Model> records;

    private final Path dbFile;
    private static final String DB_DIR = "./database";

    public DatabaseHelper(Class<Model> modelClass, String[] nonIdColumns) {
        this.modelClass = modelClass;

        this.columnHeaders = new String[nonIdColumns.length + 1];
        for (int i = 0; i < nonIdColumns.length; i++) {
            if (i == 0) {
                this.columnHeaders[0] = "id";
            } else {
                this.columnHeaders[i] = nonIdColumns[i];
            }
        }
        
        this.records = new ArrayList<>();
        
        try { Files.createDirectories(Paths.get(DB_DIR)); } catch (IOException ignored) {} // create dir if doesnt exist yet
        this.dbFile = Paths.get(DB_DIR, modelClass.getSimpleName() + ".csv");
        try {
            if (!Files.exists(this.dbFile)) {
                Files.createFile(this.dbFile);
                writeToFile();
            } else {
                loadFromFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize database file for " + modelClass.getSimpleName(), e);
        }
    }

    public void add(Model model) {
        records.add(model);
        try { writeToFile(); } catch (IOException e) { throw new RuntimeException(e); }
    }

    public void update(Model model) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).id == model.id) {
                records.set(i, model);
                try { writeToFile(); } catch (IOException e) { throw new RuntimeException(e); }
                return;
            }
        }
    }

    public synchronized void delete(Model model) {
        records.removeIf(r -> r.id == model.id);
        try { writeToFile(); } catch (IOException e) { throw new RuntimeException(e); }
    }

    public Model find(int id) {
        for (Model m : records) {
            if (m.id == id) return m;
        }
        return null;
    }

    public ArrayList<Model> all() {
        return new ArrayList<>(records);
    }

    public ArrayList<Model> where(HashMap<String,String> params) {
        ArrayList<Model> out = new ArrayList<>();
        for (Model m : records) {
            if (m.paramMatch(params)) {
                out.add(m);
            }
        }
        return out;
    }

    public Model first() {
        return find(1);
    }

    public int count() {
        return records.size();
    }

    public void clear() {
        records.clear();
        try { writeToFile(); } catch (IOException e) { throw new RuntimeException(e); }
    }

    private void loadFromFile() {
        records.clear();

        try { 
            Scanner scanner = new Scanner(dbFile);

            if (scanner.hasNextLine()) {
                String headerLine = scanner.nextLine();
                this.columnHeaders = headerLine.split(",");
            }

            while (scanner.hasNextLine()) {
                String[] values = scanner.nextLine().split(",");

                int id = Integer.parseInt(values[0]);
                
                HashMap<String, String> paramMap = new HashMap<>();
                
                for (int i = 1; i < columnHeaders.length; i++) {
                    paramMap.put(columnHeaders[i], values[i]);
                }

                records.add(createFromMap(id, paramMap));
            }
            scanner.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read database file for " + modelClass.getSimpleName(), e);
        }
    }

    private Model createFromMap(int id, HashMap<String, String> paramMap) {
        try {
            Model instance = modelClass.getDeclaredConstructor().newInstance();
            Field idField = modelClass.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(instance, id);

            for (String key : paramMap.keySet()) {
                Field field = modelClass.getDeclaredField(key);
                field.setAccessible(true);
                String value = paramMap.get(key);
                field.set(instance, parseStringToFieldType(value, field.getType()));
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + modelClass.getSimpleName() + " from map", e);
        }
    }

    private static Object parseStringToFieldType(String s, Class<?> type) {
        if (type.equals(String.class)) return s;
        if (type.equals(Integer.class) || type.equals(int.class)) return s.isEmpty() ? null : Integer.parseInt(s);
        if (type.equals(Long.class) || type.equals(long.class)) return s.isEmpty() ? null : Long.parseLong(s);
        if (type.equals(Boolean.class) || type.equals(boolean.class)) return s.isEmpty() ? false : Boolean.parseBoolean(s);
        if (type.equals(Double.class) || type.equals(double.class)) return s.isEmpty() ? null : Double.parseDouble(s);
        return s;
    }

    private void writeToFile() throws IOException {
        Files.write(this.dbFile, String.join(",", this.columnHeaders).getBytes());

        for (Model record : this.records) {
            // build CSV line for each record
            ArrayList<String> values = new ArrayList<>();
            for (String col : this.columnHeaders) {
                try {
                    Field field = modelClass.getDeclaredField(col);
                    field.setAccessible(true);
                    Object value = field.get(record);
                    values.add(value != null ? value.toString() : "");
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    values.add("");
                }
            }
            String line = String.join(",", values) + "\n";

            Files.write(this.dbFile, line.getBytes(), StandardOpenOption.APPEND);
        }
    }
}
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
     * [ ] CRUD stuff
     * [ ] first(), all(), where(), count(), clear()
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

        // prepend "id" to columns
        this.columnHeaders = new String[nonIdColumns.length + 1];
        for (int i = 0; i < nonIdColumns.length; i++) {
            if (i == 0) {
                this.columnHeaders[0] = "id";
            } else {
                this.columnHeaders[i] = nonIdColumns[i];
            }
        }
        
        this.records = new ArrayList<>();
        
        // initialize db files
        try { Files.createDirectories(Paths.get(DB_DIR)); } catch (IOException ignored) {} // create dir if doesnt exist yet
        this.dbFile = Paths.get(DB_DIR, modelClass.getSimpleName() + ".csv");
        try {
            if (!Files.exists(this.dbFile)) {
                Files.createFile(this.dbFile);
                // write header line
                writeToFile();
            } else {
                // load existing records from file
                loadFromFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize database file for " + modelClass.getSimpleName(), e);
        }
    }

    private void loadFromFile() {
        // Clear existing records
        records.clear();

        // Read from file
        try { 
            Scanner scanner = new Scanner(dbFile);

            // Read header line
            if (scanner.hasNextLine()) {
                String headerLine = scanner.nextLine();
                this.columnHeaders = headerLine.split(",");
            }

            // Parse csv
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
        // overwrite file with headers
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

            // write to file
            Files.write(this.dbFile, line.getBytes(), StandardOpenOption.APPEND);
        }
    }
}
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public final class DatabaseHelper<Model extends DatabaseModel<Model>> {
    /* TODO:
     * [x] constructor
     * [ ] read/write methods
     * [ ] CRUD stuff
     * [ ] first(), all(), where(), count(), clear()
     * [ ] db file name/path
     * [ ] CSV parsing
     */
    private final Class<Model> modelClass;
    private final String[] columnHeaders;
    private final ArrayList<Model> records;

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
        // TODO
    }

    private void writeToFile() throws IOException {
        // TODO
    }
}
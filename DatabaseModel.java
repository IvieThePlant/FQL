import java.util.HashMap;

public class DatabaseModel {
    public int id;
    public static int nextId;

    private DatabaseHelper<this.class> dbHelper;
    public boolean paramMatch(HashMap<String, String> params) {
        return true;

    }

    public static String getColumns(){
        return "foo";
    }

    public String toString();
}
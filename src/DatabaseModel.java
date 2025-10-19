package src;
import java.util.ArrayList;
import java.util.HashMap;

// What does a student need to implement in their file? should I also make and interface?

// Vars

// private static int nextId;
// public static int nextId() {
//  return nextId++;
// }

// private static DatabaseHelper<YOUR_CLASS_NAME_HERE> dbHelper;
// public static DatabaseHelper<YOUR_CLASS_NAME_HERE> dbHelper() {
//  return dbHelper;
// }


// public abstract boolean paramMatch(HashMap<String, String> params);
// TODO

// public static String getColumns(); // This should also be static
// TODO

// public abstract String toString();
// TODO

abstract class DatabaseModel<Model extends DatabaseModel<Model>> {
    public int id;
    protected abstract int nextId(); // This gets the next unused id for that Model type
    protected abstract DatabaseHelper<Model> dbHelper(); // This gets the shared dbHelper for all instances of that Model

    public DatabaseModel() {
        this.id = nextId();
    }
    public DatabaseModel(HashMap<String, String> params) {        
        this.id = nextId();
    }


    // find
    public Model find(int id) {
        return dbHelper().find(id);
    }

    // findwhere
    public ArrayList<Model> findWhere(HashMap<String, String> params) {
        return dbHelper().findWhere(params);
    }

    // first
    public Model first() {
        return dbHelper().first();
    }

    // all
    public ArrayList<Model> all() {
        return dbHelper().all();
    }

    // add
    public void add(Model record) {
        record.dbHelper().add(record);
    }

    // delete
    public void delete(Model record) {
        record.dbHelper().delete(record);
    }

    // update
    public void update(Model record) {
        record.dbHelper().update(record);
    }

    // count
    public int count() {
        return dbHelper().count();
    }

    // clear
    public void clear() {
        dbHelper().clear();
    }

    // getFilePath
    public String getFilePath() {
        return dbHelper().getFilePath();
    }

    // getFileName
    public String getFileName() {
        return dbHelper().getFileName();
    }

    public abstract boolean paramMatch(HashMap<String, String> params);

    public abstract String[] getColumns(); // This should also be static

    public abstract String toString();
}
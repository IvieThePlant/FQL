package src;
import java.util.HashMap;

public class User extends DatabaseModel<User> {
    // db stuff
    private static int nextId = 0;
    public int nextId() {
        return nextId++;
    }

    private static DatabaseHelper<User> dbHelper = new DatabaseHelper<User>(User.class);
    public DatabaseHelper<User> dbHelper() {
        return dbHelper;
    }

    // vars
    public String name;
    public String email;

    // Constructor
    public User(String name, String email) {
        super();
        this.name = name;
        this.email = email;
        add(this);
    }
    public User(HashMap<String, String> params) {
        super();
        this.name = params.get("name");
        this.email = params.get("email");
        add(this);
    }

    @Override
    public boolean paramMatch(HashMap<String, String> params) {
        for (String key : params.keySet()) {
            switch (key) {
                case "name":
                    if (!this.name.equals(params.get(key))) { return false; }
                case "email":
                    if (!this.email.equals(params.get(key))) { return false; }
                default:
            }
        }
        return true;
    }

    @Override
    public String[] getColumns() {
        String[] arr = new String[2];
        arr[0] = "name";
        arr[1] = "email";
        return arr;
    }

    @Override
    public String toString() {
        return this.id + ", " + this.name + ", " + this.email;
    } 
}
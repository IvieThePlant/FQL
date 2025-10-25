// import java.util.HashMap;

public class User extends DatabaseModel<User> {
    // fields
    public String name;
    public String email;

    // Expext a public no-arg constructor
    public User() {
    }

    // Expect a public constructor with each field as param
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /*
     * These could be an option, but I want it to work without it too
     * public User(HashMap<String,String> params) {
     * this.name = params.get("name");
     * this.email = params.get("email");
     * }
     * 
     * public String[] getColumns() {
     * return new String[] { "name", "email" };
     * }
     * 
     */
    public String toString() {
        return this.id + ", " + this.name + ", " + this.email;
    }
}
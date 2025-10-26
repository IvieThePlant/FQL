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

    public String toString() {
        return this.id + ", " + this.name + ", " + this.email;
    }
}
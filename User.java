public class User extends DatabaseModel<User> {
    // Example variables
    public String name;
    public String email;

    // Must have a public no-arg constructor
    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String toString() {
        return this.id + ", " + this.name + ", " + this.email;
    }
}
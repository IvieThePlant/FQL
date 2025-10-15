public class User implements DatabaseModel {
    public int id;
    public static int nextId = 1;

    private static DatabaseHelper<User> dbHelper = new DatabaseHelper<>(User.class);

    public String name;
    public String email;

    public User(String name, String email) {
        this.id = nextId++;
        this.name = name;
        this.email = email;
    }

    @Override
    public boolean match(HashMap<String, String> params) {
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (key.equals("id") && Integer.toString(this.id).equals(value)) {
                continue;
            } else if (key.equals("name") && this.name.equals(value)) {
                continue;
            } else if (key.equals("email") && this.email.equals(value)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name=" + name + ", email=" + email + "}";
    }
}
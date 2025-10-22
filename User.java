

import src.DatabaseModel;

public class User extends DatabaseModel<User> {
    private String name;
    private String email;

    public String getName() {
        return name;
    }

}
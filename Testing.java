public class Testing {
    public static void main(String[] args) {
        // Create two users and save them
        User u1 = new User("Piku", "piku@example.com");
        u1.save();
        User u2 = new User("Niku", "niku@example.com");
        u2.save();

        // List all users
        System.out.println("All users:");
        for (User u : User.all(User.class)){
            System.out.println(u);
        }

        // Find id 1
        User f = User.first(User.class);
        System.out.println("first record: " + f);

        // Where
        java.util.Map<String,String> q = new java.util.HashMap<>();
        q.put("email", "piku@example.com");
        System.out.println("Where email=piku@example.com: " + User.where(User.class, q).size());

        // Delete first user
        if (f != null) {
            f.delete();
        }

        System.out.println("Remaining:");
        for (User u : User.all(User.class)) {
            System.out.println(u);
        }
    }
}

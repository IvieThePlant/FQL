import java.util.HashMap;
import java.util.ArrayList;

public class Testing {
    private static int testsPassed = 0;
    private static int totalTests = 0;
    private static ArrayList<String> failedTests = new ArrayList<>();

    public static void main(String[] args) {
        // Clear any existing data
        User.clear(User.class);

        // Run all tests
        testBasicCRUD();
        testNullValues();
        testMultipleInstances();
        testQueries();
        testEdgeCases();
        testFilePersistence();

        // Print test results
        System.out.println("\n=== Test Results ===");
        System.out.println("Tests passed: " + testsPassed + "/" + totalTests);

        if (!failedTests.isEmpty()) {
            System.out.println("\nFailed Tests:");
            for (String failure : failedTests) {
                System.out.println("  - " + failure);
            }
        }
    }

    private static void assert_(boolean condition, String message) {
        totalTests++;
        if (condition) {
            System.out.println("o " + message);
            testsPassed++;
        } else {
            System.out.println("x " + message);
            failedTests.add(message);
        }
    }

    private static void testBasicCRUD() {
        System.out.println("\n=== Testing Basic CRUD Operations ===");

        // Test Create
        User u1 = new User("Test User", "test@example.com");
        u1.save();
        assert_(u1.id > 0, "User should have valid ID after save");
        assert_(User.count(User.class) == 1, "Database should have one user");

        // Test Read
        User found = User.find(User.class, u1.id);
        assert_(found != null, "Should find user by ID");
        assert_(found.name.equals("Test User"), "User name should match");
        assert_(found.email.equals("test@example.com"), "User email should match");

        // Test Update
        found.name = "Updated Name";
        found.save();
        User updated = User.find(User.class, u1.id);
        assert_(updated.name.equals("Updated Name"), "Name should be updated");

        // Test Delete
        found.delete();
        assert_(User.find(User.class, u1.id) == null, "User should be deleted");
        assert_(User.count(User.class) == 0, "Database should be empty after delete");
    }

    private static void testNullValues() {
        System.out.println("\n=== Testing Null Values ===");

        User u = new User(null, null);
        u.save();
        assert_(u.id > 0, "Should save user with null values");

        User found = User.find(User.class, u.id);
        assert_(found != null, "Should find user with null values");
        assert_(found.name == null, "Name should be null");
        assert_(found.email == null, "Email should be null");

        u.delete();
    }

    private static void testMultipleInstances() {
        System.out.println("\n=== Testing Multiple Instances ===");

        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User u = new User("User" + i, "user" + i + "@example.com");
            u.save();
            users.add(u);
        }

        assert_(User.count(User.class) == 5, "Should have 5 users");
        assert_(User.all(User.class).size() == 5, "All() should return 5 users");

        // Clean up
        User.clear(User.class);
    }

    private static void testQueries() {
        System.out.println("\n=== Testing Queries ===");

        // Create test data
        User u1 = new User("John", "john@example.com");
        User u2 = new User("John", "john2@example.com");
        User u3 = new User("Jane", "jane@example.com");
        u1.save();
        u2.save();
        u3.save();

        // Test where queries
        HashMap<String, String> params = new HashMap<>();
        params.put("name", "John");
        ArrayList<User> results = User.where(User.class, params);
        assert_(results.size() == 2, "Should find 2 users named John");

        params.clear();
        params.put("email", "jane@example.com");
        results = User.where(User.class, params);
        assert_(results.size() == 1, "Should find 1 user with Jane's email");

        // Test first
        User first = User.first(User.class);
        assert_(first != null, "First should return a user");

        // Clean up
        User.clear(User.class);
    }

    private static void testEdgeCases() {
        System.out.println("\n=== Testing Edge Cases ===");

        User.clear(User.class); // Clear before starting

        // Test empty string values
        User u = new User("", "");
        u.save();
        User found = User.find(User.class, u.id);
        assert_(found.name.equals(""), "Should handle empty string name");
        assert_(found.email.equals(""), "Should handle empty string email");

        // Test very long values
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 1000; i++)
            longString.append("a");
        u = new User(longString.toString(), "long@example.com");
        u.save();
        found = User.find(User.class, u.id);
        assert_(found.name.length() == 1000, "Should handle long string values");

        // Test special characters in strings
        u = new User("Test,With,Commas", "email@with,commas.com");
        u.save();
        found = User.find(User.class, u.id);
        assert_(found.name.equals("Test,With,Commas"), "Should handle commas in name");
        assert_(found.email.equals("email@with,commas.com"), "Should handle commas in email");

        // Test Unicode characters
        u = new User("测试用户", "test@测试.com");
        u.save();
        found = User.find(User.class, u.id);
        assert_(found.name.equals("测试用户"), "Should handle Unicode in name");
        assert_(found.email.equals("test@测试.com"), "Should handle Unicode in email");

        // Test whitespace handling
        u = new User("  User  With  Spaces  ", "  email@with.spaces  ");
        u.save();
        found = User.find(User.class, u.id);
        assert_(found.name.equals("  User  With  Spaces  "), "Should preserve whitespace in name");
        assert_(found.email.equals("  email@with.spaces  "), "Should preserve whitespace in email");

        // Test newline characters
        u = new User("User\\nWith\\nNewlines", "email\\nwith\\nnewlines@test.com");
        u.save();
        found = User.find(User.class, u.id);
        assert_(found.name.equals("User\\nWith\\nNewlines"), "Should handle newline characters in name");
        assert_(found.email.equals("email\\nwith\\nnewlines@test.com"), "Should handle newline characters in email");

        // Test boundary values for ID
        u = new User("Max ID Test", "max@test.com");
        for (int i = 0; i < 1000; i++) {
            User temp = new User("Temp", "temp@test.com");
            temp.save();
        }
        u.save();
        found = User.find(User.class, u.id);
        assert_(found != null, "Should handle large ID values");

        // Clean up
        User.clear(User.class);
    }

    private static void testFilePersistence() {
        System.out.println("\n=== Testing File Persistence ===");

        // Create test data
        User u = new User("Persistent", "persist@example.com");
        u.save();
        int id = u.id;

        // Create new helper to force reload from file
        User found = User.find(User.class, id);
        assert_(found != null, "Should load user from file");
        assert_(found.name.equals("Persistent"), "Should maintain data integrity");
        assert_(found.email.equals("persist@example.com"), "Should maintain data integrity");

        // Clean up
        User.clear(User.class);
    }
}

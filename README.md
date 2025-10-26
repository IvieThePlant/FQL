# FQL (Fake Query Language)

A .csv based alternative to databases, made for coders who "just aren't ready for that yet".

## Features

- Simple CSV file storage
- Easy to use syntax
- Portable and easy to share

## Installation

1. Clone the repository (or just download the files manually)
2. Place the files in your project folder.
3. That's it! You're ready to use FQL.

## Usage

### Creating a Model

To create a new model, extend `DatabaseModel<YOUR_CLASS_NAME_HERE>` with your class, and fill in your class name.
Example:
```java
public class User extends DatabaseModel<User> {
    // Thse are some example columns! These will be saved to the database
    public String name;
    public String email;

    // !REQUIRED!: Your class must have a constructor with no arguments
    public User() {
    }

    // You can have any other constructors you need as well
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
```

### Database Operations

#### Create & Update
```java
// Create new user
User user = new User("John", "john@example.com");
user.save();  // Assigns ID and saves to CSV

// Update existing user
user.name = "John Smith";
user.save();  // Updates existing record in CSV
```

#### Read
For each of these methods, you MUST pass in `YOUR_CLASS.class` as the first argument.
```java
// Find by ID
User found = User.find(User.class, 1);

// Get first record
User first = User.first(User.class);

// Get all records
ArrayList<User> allUsers = User.all(User.class);

// Count records
int userCount = User.count(User.class);

// Find record with certain conditions
HashMap<String, String> params = new HashMap<>();
params.put("name", "John"); // Params takes the format of (THE_VARIABLE, WHAT_YOU_WANT_THE_VALUE_TO_BE)
ArrayList<User> johns = User.where(User.class, params);
```

#### Delete
```java
// Delete a record
user.delete();

// Clear all records from CSV
User.clear(User.class);
```

### Data Storage

Each saved instance of your class is stored in CSV files within a `database` directory. Each model class gets its own file (e.g., `database/User.csv`).

### Example Implementation

The repository includes a complete `User` class implementation demonstrating all features.

### Comprehensive Testing

The project includes extensive tests (`Testing.java`) covering:

1. Basic CRUD (Create, Read, Update, Destroy) Operations
2. Edge Cases
3. Query Operations
4. Data Integrity

To run the tests:
```bash
javac *.java
java Testing
```

## Known Issues
- **Your String fields cannot contain commas!** Doing so will break the CSV file reader!
- Concurency is currently not supported.

## Troubleshooting

### Common Issues and Solutions

1. **File Permission Errors**
   ```
   java.nio.file.FileSystemException: ./database/Model.csv: Permission denied
   ```
   - Ensure the application has write permissions in the directory
   - Check if another process isn't locking the CSV file
   - Try running with elevated privileges if necessary

2. **ID Assignment Issues**
   ```
   RuntimeException: Failed to create helper for Model
   ```
   - Clear the database directory and try again
   - Ensure your model class has a proper no-args constructor
   - Check if the CSV file isn't corrupted

3. **Null Pointer Exceptions**
   ```
   NullPointerException when calling field.get(this)
   ```
   - Make sure all variables are properly initialized
   - Check if you're trying to access a null object
   - Verify that the variables exists in your model class

4. **CSV Parsing Errors**
   ```
   ArrayIndexOutOfBoundsException during CSV parsing
   ```
   - Check if your CSV files haven't been manually edited
   - Ensure no commas are present in the String variables
   - Verify the number of columns in the CSV matches your model variables

5. **Concurrent Access Issues**
   ```
   FileSystemException: The process cannot access the file because it is being used by another process
   ```
   - Concurrency is currently not supported :(

### Still Having Issues?

If you encounter problems not covered here:
1. Check the test file (`Testing.java`) for proper usage examples
2. Verify your model class follows the example structure
3. Create an issue on GitHub with:
   - Complete stack trace
   - Model class code
   - Steps to reproduce
   - Content of relevant CSV files

## Contributing

Contributions are welcome! Please fork the repository and create a pull request with your changes.

## License

This project is licensed under the GPL-3.0 License. See the LICENSE file for details.

## Contact

For any questions or suggestions, please open an issue on the GitHub repository.

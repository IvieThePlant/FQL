package helpers;

import java.util.ArrayList;
import java.util.HashMap;

// File handling imports
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DatabaseHelper<Model> {
    Class modelClass;

    private File dbFile;
    private String[] columnHeaders;
    private ArrayList<Model> records = new ArrayList<>();

    // constructor
    public DatabaseHelper(Class model) {
        this.modelClass = model.getClass();
        this.dbFile = new File(model.getSimpleName() + "_db.csv");

        // create file if it doesn't exist
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // read file and populate records
        records = readFromFile();
    }

    // find
    public <Model> find(int id) {
        for (<Model> record : records) {
            if (record.id == id) {
                return record;
            }
        }
        return null;
    }


    public ArrayList<Model> findWhere(HashMap<String><String> params) {
        ArrayList<Model> results = new ArrayList<>();

        for (<Model> record : records) {
            if (record.match(params)) {
                results.add(record);
            }
        }

        return results;
    }

    public <Model> first() {
        return find(1);
    }

    // all
    public ArrayList<Model> all() {
            return records;
    }

    // add

    // delete

    // update

    // where

    // private read file
    // reads the db file and makes record for each line
    private ArrayList<Model> readFromFile() {
        try {
            Scanner scanner = new Scanner(dbFile);

            // if it has headers, read them, else create them
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                columnHeaders = line.split(",");
            } else {
                columnHeaders = modelClass.getColumns();
            }

            // read each line and make a record
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] values = line.split(",");

                // build the record hashMap
                if (columnHeaders.count() == values.count()) {
                    HashMap<String, String> recordHash = new HashMap<><>();
                    for (i = 0; i < columnHeaders.count(); i++) {
                        recordHash[columnHeaders[i]] = values[i];
                    }

                    // create a new instance of the model class
                    records.add(new modelClass(recordHash));
                } else {
                    throw Exception("A csv record is missing a column!")
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    // private write to file
    // takes all records, and writes each one on a line of csv
    private void writeToFile() {
        try {
            // overwrite file with column headers
            FileWriter writer = new FileWriter(dbFile, false);
            writer.write(columnHeaders + "\n");
            writer.close();

            writer = new FileWriter(dbFile, true);

            // add each record to file
            for (Model record : records) {
                writer.write(record.toString() + "\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.example.bluejack;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the Users table
        String createUserTableQuery = "CREATE TABLE users (" +
                "userID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "email TEXT," +
                "password TEXT," +
                "phone TEXT," +
                "isVerified INTEGER)";
        db.execSQL(createUserTableQuery);

        // Create the Medicine table
        String createMedicineTableQuery = "CREATE TABLE medicine (" +
                "medicineID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "manufacturer TEXT," +
                "price REAL," +
                "image TEXT," +
                "description TEXT)";
        db.execSQL(createMedicineTableQuery);

        // Create the Transaction table
        String createTransactionTableQuery = "CREATE TABLE transactions (" +
                "transactionID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "medicineID INTEGER," +
                "userID INTEGER," +
                "transactionDate TEXT," +
                "quantity INTEGER," +
                "FOREIGN KEY (medicineID) REFERENCES medicine(medicineID)," +
                "FOREIGN KEY (userID) REFERENCES users(userID))";
        db.execSQL(createTransactionTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrade if needed
    }
}

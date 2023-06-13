package com.example.bluejack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.example.bluejack.model.Datas;
import com.example.bluejack.model.Transactions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class TransactionPage extends AppCompatActivity {
    RecyclerView recyclerView;
    Button btnAboutUs, btnLogout;
    SharedPreferences sp;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    String userString;
    ArrayList<Transactions> transactions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_page);

        btnAboutUs = findViewById(R.id.btntransactionAboutUs);
        btnLogout = findViewById(R.id.btntransactionLogout);
        recyclerView = findViewById(R.id.rvTransaction);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_medicine:
                    startActivity(new Intent(TransactionPage.this, HomePage.class));
                    return true;
                case R.id.navigation_transaction:
                    return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_transaction);
        
        getTransactions();
        setListeners();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TransactionAdapter(getApplicationContext(),transactions,userString));
    }

    @SuppressLint("Range")
    private void getTransactions() {
        sp = getSharedPreferences("loggedIn", MODE_PRIVATE);
        int userID = Integer.parseInt(sp.getString("loggedIn",""));
        userString = String.valueOf(userID);

        String query = "SELECT * FROM transactions WHERE userID = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userID)});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String transactionDate = cursor.getString(cursor.getColumnIndex("transactionDate"));
                @SuppressLint("Range") int medicineID = cursor.getInt(cursor.getColumnIndex("medicineID"));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
                @SuppressLint("Range") int transactionID = cursor.getInt(cursor.getColumnIndex("transactionID"));

                String query2 = "SELECT * FROM medicine WHERE medicineID = ?";
                Cursor cursor2 = db.rawQuery(query2, new String[]{String.valueOf(medicineID)});

                String medicineName = null;
                double medicinePrice = 0;

                if (cursor2.moveToFirst()) {
                    medicineName = cursor2.getString(cursor2.getColumnIndex("name"));
                    medicinePrice = cursor2.getDouble(cursor2.getColumnIndex("price"));
                }
                cursor2.close();

                Transactions transaction = new Transactions(transactionID, transactionDate, String.valueOf(medicineName), String.valueOf(medicinePrice), String.valueOf(quantity));
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
    private void setListeners() {
        btnLogout.setOnClickListener(e->{
            Intent intent = new Intent(this, LoginPage.class);
            startActivity(intent);
        });

        btnAboutUs.setOnClickListener(e->{
            Intent intent = new Intent(this, AboutUs.class);
            startActivity(intent);
        });
    }
}
package com.example.bluejack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LoginPage extends AppCompatActivity {
    private EditText edtEmail, edtPassword;
    private Button btnLogin, btnRegister;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        initialize();
        checkPermission();
    }
    private void checkPermission() {
        int sendSMSPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS);
        if(sendSMSPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},0);
        }
    }

    private void initialize(){
        edtEmail = findViewById(R.id.edtLoginEmail);
        edtPassword = findViewById(R.id.edtLoginPassword);
        btnLogin = findViewById(R.id.btnLoginLogin);
        btnRegister = findViewById(R.id.btnLoginRegister);

        // Check if the users table is empty
        String query = "SELECT COUNT(*) FROM users";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        // Insert a user if the table is empty
        if (count == 0) {
            ContentValues values = new ContentValues();
            values.put("name", "Admin");
            values.put("email", "admin.com");
            values.put("password", "qwe123");
            values.put("phone", "1234567890");
            values.put("isVerified", 1);

            long userID = db.insert("users", null, values);
        }

        setListener();
    }

    private void setListener(){
        btnLogin.setOnClickListener(e -> {
            if (userExists()){
                if(!isVerified()){
                    Intent intent = new Intent(this, OtpPage.class);
                    intent.putExtra("newUser", edtEmail.getText().toString());
                    startActivity(intent);
                }
                else{
                Intent intent = new Intent(this, HomePage.class);
                intent.putExtra("loggedIn", edtEmail.getText().toString());
                startActivity(intent);
                }
            }
            else if (edtEmail.getText().toString().isEmpty()
                    ||edtPassword.getText().toString().isEmpty()){
                showToast("Please Input Fields");
            }
            else {
                showToast("Login Failed");
            }
        });

        btnRegister.setOnClickListener(e -> {
            Intent intent = new Intent(this, RegisterPage.class);
            startActivity(intent);
        });
    }

    private boolean isVerified() {
        boolean verified = false;

        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int isVerified = cursor.getInt(cursor.getColumnIndex("isVerified"));

            if (isVerified == 1) {
                verified = true;
            }
            cursor.close();
        }
        return verified;
    }

    private boolean userExists() {
        boolean exists = false;

        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        if (cursor.moveToFirst()) {
            exists = true;
        }
        cursor.close();

        return exists;
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
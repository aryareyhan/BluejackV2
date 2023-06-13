package com.example.bluejack;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterPage extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPassword, edtConfirmPassword, edtPhone;
    private Button btnRegister, btnBack;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        initialize();
    }

    private void initialize(){
        edtName = findViewById(R.id.edtRegisterName);
        edtEmail = findViewById(R.id.edtRegisterEmail);
        edtPassword = findViewById(R.id.edtRegisterPassword);
        edtConfirmPassword = findViewById(R.id.edtRegisterConfirmPassword);
        edtPhone = findViewById(R.id.edtRegisterPhone);

        btnRegister = findViewById(R.id.btnRegisterRegister);
        btnBack = findViewById(R.id.btnRegisterBack);

        setListener();
    }

    private void setListener(){
        btnRegister.setOnClickListener(e -> {
            String name = edtName.getText().toString();
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();
            String confirmPassword = edtConfirmPassword.getText().toString();
            String phone = edtPhone.getText().toString();

            if(isEmpty(name, email, password, confirmPassword, phone)){
                showToast("All fields must be filled");
            }
            else if(name.length()<5){
                showToast("Name must be at least 5 characters!");
            }
            else if(!email.endsWith(".com")){
                showToast("Invalid email address!");
            }
            else if(!isPasswordAlphanumeric(password)){
                showToast("Password must be alphanumeric!");
            }
            else{
                showToast("Register Success!");

                // add to database
                ContentValues values = new ContentValues();
                values.put("name", name);
                values.put("email", email);
                values.put("password", password);
                values.put("phone", phone);
                values.put("isVerified", 0);

                long userID = db.insert("users", null, values);

                Intent intent = new Intent(this, OtpPage.class);
                intent.putExtra("newUser", edtEmail.getText().toString());
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(e -> {
            Intent intent = new Intent(this, LoginPage.class);
            startActivity(intent);
        });
    }

    private static boolean isPasswordAlphanumeric(String string){
        String regex = "^(?=.*[a-zA-Z])(?=.*[0-9])[A-Za-z0-9]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(string);

        return m.matches();
    }

    private boolean isEmpty(String... inputs) {
        for (String input : inputs) {
            if (TextUtils.isEmpty(input)) {
                return true;
            }
        }
        return false;
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
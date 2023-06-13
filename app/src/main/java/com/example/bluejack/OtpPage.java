package com.example.bluejack;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class OtpPage extends AppCompatActivity {
    private EditText edtCode;
    private Button btnVerify;
    private String newUser, OTP, message;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    SmsManager smsManager;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_page);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        edtCode = findViewById(R.id.edtOTPCode);
        btnVerify = findViewById(R.id.btnOTPVerify);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            newUser = extras.getString("newUser");
        }

        sendSMS();
        setListener();
    }

    private void sendSMS() {
        smsManager = SmsManager.getDefault();

        Random random = new Random();
        StringBuilder sb = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int digit = random.nextInt(10);
            sb.append(digit);
        }
        OTP = sb.toString();
        message = "Your OTP Code is: " + OTP;

        smsManager.sendTextMessage("5554",null, message,null,null);
    }

    private void setListener(){
        btnVerify.setOnClickListener(e -> {
            if (edtCode.getText().toString().equals(OTP)){
                showToast("Verification Success");

                ContentValues values = new ContentValues();
                values.put("isVerified", 1);

                int rowsAffected = db.update("users", values, "email = ?", new String[]{newUser});

                Intent intent = new Intent(this, HomePage.class);
                intent.putExtra("loggedIn", newUser);
                startActivity(intent);

            } else if (edtCode.getText().toString().isEmpty()){
                showToast("Please Input Field");
            } else {
                showToast("Verification Failed");
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
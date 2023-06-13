package com.example.bluejack;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MedicineDetailPage extends AppCompatActivity {
    public ImageView medImage_iv;
    public TextView medName_edt, medMan_edt, medPrice_edt, medDesc_edt;
    public EditText quantity_edt;
    public Button purchase_btn;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    SharedPreferences sp;
    private boolean isQuantityValid(){
        if(quantity_edt.getText().toString().matches("")){
            return true;
        }
        int value = Integer.parseInt(quantity_edt.getText().toString());
        return value < 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail_page);

        medName_edt = findViewById(R.id.textViewMedicineName);
        medMan_edt = findViewById(R.id.textViewMedicineManufacturer);
        medPrice_edt = findViewById(R.id.textViewMedicinePrice);
        medDesc_edt = findViewById(R.id.textViewMedicineDescription);
        medImage_iv = findViewById(R.id.imageViewMedicine);
        quantity_edt = findViewById(R.id.editTextQuantity);
        purchase_btn = findViewById(R.id.buttonPurchase);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            medName_edt.setText(extras.getString("medname"));
            medMan_edt.setText(extras.getString("medman"));
            medPrice_edt.setText(String.format("Rp. %s", extras.getString("medprice")));
            medDesc_edt.setText(extras.getString("meddesc"));
            Picasso.get().load(extras.getString("medimage")).into(medImage_iv);
        }
        purchase_btn.setOnClickListener(e -> {
            if(isQuantityValid()){
                Toast.makeText(this, "Input valid quantity!", Toast.LENGTH_SHORT).show();
            }
            else {
                addTransaction();

                Intent intent = new Intent(this, HomePage.class);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("Range")
    private void addTransaction() {
        //date
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = dateFormat.format(currentDate);

        //ambil siapa yg login
        sp = getSharedPreferences("loggedIn", MODE_PRIVATE);
        int userID = Integer.parseInt(sp.getString("loggedIn",""));
        String medicineName = medName_edt.getText().toString();

        String query = "SELECT medicineID FROM medicine WHERE name = ?";
        Cursor cursor = db.rawQuery(query, new String[]{medicineName});

        int medicineID = -1;
        if (cursor.moveToFirst()) {
            medicineID = cursor.getInt(cursor.getColumnIndex("medicineID"));
        }

        cursor.close();

        //insert to database
        if (medicineID != -1) {
            ContentValues values = new ContentValues();
            values.put("medicineID", medicineID);
            values.put("userID", userID);
            values.put("transactionDate", dateString);
            values.put("quantity",Integer.parseInt(quantity_edt.getText().toString()));

            long newRowId = db.insert("transactions", null, values);

            if (newRowId != -1) {
                showToast("Purchase success");
            } else {
                showToast("Purchase failed");
            }
        } else {
            showToast("Medicine missing");
        }
    }
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
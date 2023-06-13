package com.example.bluejack;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.bluejack.model.Datas;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity {
    RecyclerView recyclerView;
    Button btnAboutUs, btnLogout;
    SharedPreferences sp;
    ProgressDialog dialog;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    ArrayList<Datas> medicines = new ArrayList<>();
    private RequestQueue requestQueue;

    @SuppressLint({"NonConstantResourceId", "Range"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        btnAboutUs = findViewById(R.id.btnHomeAboutUs);
        btnLogout = findViewById(R.id.btnHomeLogout);
        recyclerView = findViewById(R.id.rvMedicine);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sp = getSharedPreferences("loggedIn", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();

            String email = extras.getString("loggedIn");

            String query = "SELECT userID FROM users WHERE email = ?";
            Cursor cursor = db.rawQuery(query, new String[]{email});

            int userID = -1;

            if (cursor.moveToFirst()) {
                userID = cursor.getInt(cursor.getColumnIndex("userID"));
            }
            cursor.close();

            if (userID != -1) {
                editor.putString("loggedIn", String.valueOf(userID));
                editor.apply();
            }
        }

        // bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_medicine:
                    return true;
                case R.id.navigation_transaction:
                    startActivity(new Intent(HomePage.this, TransactionPage.class));
                    return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_medicine);

        dialog = new ProgressDialog(this);
        dialog.show();

        requestQueue = Volley.newRequestQueue(this);
        fetchMedicine();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MedicineAdapter(getApplicationContext(),medicines));

        setListeners();
    }

    private void fetchMedicine() {
        // Check if the medicine table is empty
        String query = "SELECT COUNT(*) FROM medicine";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        // Insert medicines if the table is empty
        if (count == 0) {
            String url = "https://mocki.io/v1/ae13b04b-13df-4023-88a5-7346d5d3c7eb";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            JSONArray dataArray = response.getJSONArray("medicines");

                            for (int i = 0; i < dataArray.length(); i++) {
                                //initialize fetched data
                                JSONObject dataObject = dataArray.getJSONObject(i);
                                String name, manufacturer, price, image, description;
                                name = dataObject.getString("name");
                                manufacturer = dataObject.getString("manufacturer");
                                price = dataObject.getString("price");
                                image = dataObject.getString("image");
                                description = dataObject.getString("description");

                                // put to database
                                ContentValues values = new ContentValues();
                                values.put("name", name);
                                values.put("manufacturer", manufacturer);
                                values.put("price", Integer.parseInt(price));
                                values.put("image", image);
                                values.put("description", description);
                                long newRowId = db.insert("medicine", null, values);

                                Datas datas = new Datas(name,manufacturer,price,image,description);
                                medicines.add(datas);
                            }
                            dialog.cancel();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        Log.e("ASD","Can't fetch data");
                    });
            requestQueue.add(jsonObjectRequest);
        }
        else{
            String query2 = "SELECT * FROM medicine";
            Cursor cursor2 = db.rawQuery(query2, null);

            if (cursor2.moveToFirst()) {
                do {
                    @SuppressLint("Range") String name = cursor2.getString(cursor2.getColumnIndex("name"));
                    @SuppressLint("Range") String manufacturer = cursor2.getString(cursor2.getColumnIndex("manufacturer"));
                    @SuppressLint("Range") double price = cursor2.getDouble(cursor2.getColumnIndex("price"));
                    @SuppressLint("Range") String image = cursor2.getString(cursor2.getColumnIndex("image"));
                    @SuppressLint("Range") String description = cursor2.getString(cursor2.getColumnIndex("description"));

                    Datas medicine = new Datas(name, manufacturer, String.valueOf(price), image, description);
                    medicines.add(medicine);
                } while (cursor2.moveToNext());
            }
            cursor2.close();
            dialog.cancel();
        }

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
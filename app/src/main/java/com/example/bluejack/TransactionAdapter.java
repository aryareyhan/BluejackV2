package com.example.bluejack;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bluejack.model.Transactions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    Context ctx;
    ArrayList<Transactions> transactions;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    SharedPreferences sp;
    String loggedIn;

    public TransactionAdapter(Context ctx, ArrayList<Transactions> transactions, String loggedIn) {
        this.ctx = ctx;
        this.transactions = transactions;
        this.loggedIn = loggedIn;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.transaction_item,parent,false);
        return new ViewHolder(view);

    }

    @SuppressLint("Range")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.transDate_edt.setText(transactions.get(position).getDate());
        holder.transName_edt.setText(transactions.get(position).getName());
        holder.transPrice_edt.setText(transactions.get(position).getPrice());
        holder.transQty_edt.setText(transactions.get(position).getQuantity());

        dbHelper = new DatabaseHelper(ctx);
        db = dbHelper.getWritableDatabase();

        holder.delete_btn.setOnClickListener(view -> {
            int transactionID = transactions.get(position).getID();

            int rowsAffected = db.delete("transactions", "transactionID = ?", new String[]{String.valueOf(transactionID)});
            transactions.remove(position);

            notifyItemRemoved(position);
        });

        holder.edit_btn.setOnClickListener(view -> {
            if (holder.transQty_edt.getVisibility() == View.VISIBLE) {
                holder.editText.setText(holder.transQty_edt.getText().toString());

                holder.transQty_edt.setVisibility(View.GONE);
                holder.editText.setVisibility(View.VISIBLE);
                holder.edit_btn.setText("Done");

                holder.editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(holder.editText, InputMethodManager.SHOW_IMPLICIT);
            } else {
                holder.transQty_edt.setText(holder.editText.getText().toString());

                int transactionID = transactions.get(position).getID();
                int newQuantity = Integer.parseInt(holder.editText.getText().toString());

                ContentValues values = new ContentValues();
                values.put("quantity", newQuantity);

                int rowsAffected = db.update("transactions", values, "transactionID = ?", new String[]{String.valueOf(transactionID)});

                transactions.get(position).setQuantity(holder.editText.getText().toString());

                holder.editText.setVisibility(View.GONE);
                holder.transQty_edt.setVisibility(View.VISIBLE);
                holder.edit_btn.setText("Edit");

                InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(holder.editText.getWindowToken(), 0);

                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView transDate_edt, transName_edt, transPrice_edt, transQty_edt, transUser;
        Button edit_btn, delete_btn;
        EditText editText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            edit_btn = itemView.findViewById(R.id.btnEdit);
            delete_btn = itemView.findViewById(R.id.btnDelete);
            transName_edt = itemView.findViewById(R.id.tvTransName);
            transDate_edt = itemView.findViewById(R.id.tvTransDate);
            transPrice_edt = itemView.findViewById(R.id.tvTransPrice);
            transQty_edt = itemView.findViewById(R.id.tvTransQuantity);
            editText = itemView.findViewById(R.id.etTransQuantity);
        }
    }
}

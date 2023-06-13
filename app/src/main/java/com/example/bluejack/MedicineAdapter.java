package com.example.bluejack;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bluejack.model.Datas;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {

    Context ctx;
    ArrayList<Datas> medicines;

    public MedicineAdapter(Context ctx, ArrayList<Datas> medicines) {
        this.ctx = ctx;
        this.medicines = medicines;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.medicine_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.medicineName_edt.setText(medicines.get(position).getName());
        holder.medicineManufacturer_edt.setText(medicines.get(position).getManufacturer());
        holder.medicinePrice_edt.setText(String.format("Rp. %s", medicines.get(position).getPrice()));
        Picasso.get().load(medicines.get(position).getImage()).into(holder.medicine_iv);

        holder.cv.setOnClickListener(e -> {
            Intent intent = new Intent(ctx,MedicineDetailPage.class);
            intent.putExtra("medname",medicines.get(position).getName());
            intent.putExtra("medman",medicines.get(position).getManufacturer());
            intent.putExtra("meddesc",medicines.get(position).getDescription());
            intent.putExtra("medprice",medicines.get(position).getPrice());
            intent.putExtra("medimage",medicines.get(position).getImage());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView medicineName_edt, medicinePrice_edt, medicineManufacturer_edt;
        ImageView medicine_iv;
        CardView cv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineName_edt = itemView.findViewById(R.id.medicineName);
            medicinePrice_edt = itemView.findViewById(R.id.medicinePrice);
            medicineManufacturer_edt = itemView.findViewById(R.id.medicineManufacturer);
            medicine_iv = itemView.findViewById(R.id.medicineImage);
            cv = itemView.findViewById(R.id.medicineCv);
        }
    }


}

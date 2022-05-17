package com.example.thestockers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeCustomAdapter extends RecyclerView.Adapter<HomeCustomAdapter.MyViewHolder> {

    private Context context;
    Activity activity;
    private ArrayList id, date, product, quantity, unit;

    HomeCustomAdapter(Activity activity, Context context, ArrayList id, ArrayList date, ArrayList product, ArrayList quantity, ArrayList unit){
        this.activity = activity;
        this.context = context;
        this.id = id;
        this.date = date;
        this.product = product;
        this.quantity = quantity;
        this.unit = unit;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.home_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.dateTxt.setText(String.valueOf(date.get(position)));
        holder.productTxt.setText(String.valueOf(product.get(position)));
        holder.quantityTxt.setText(String.valueOf(quantity.get(position)));
        holder.unitTxt.setText(String.valueOf(unit.get(position)));

        // Set listener for all linearlayout rows
        holder.homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HomeUpdateActivity.class);
                intent.putExtra("id", String.valueOf(id.get(position)));
                intent.putExtra("product", String.valueOf(product.get(position)));
                intent.putExtra("quantity", String.valueOf(quantity.get(position)));
                intent.putExtra("unit", String.valueOf(unit.get(position)));
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return product.size();
    }

    public void filterList(ArrayList<String> filteredList){
        product = filteredList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView dateTxt, productTxt, quantityTxt, unitTxt;
        LinearLayout homeLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTxt = itemView.findViewById(R.id.add_date);
            productTxt = itemView.findViewById(R.id.product_name_txt);
            quantityTxt = itemView.findViewById(R.id.quantity_txt);
            unitTxt = itemView.findViewById(R.id.unit_txt);
            homeLayout = itemView.findViewById(R.id.homeLayout);
        }
    }

    public void deleteItem(int position){
        this.id.remove(position);
        this.date.remove(position);
        this.product.remove(position);
        this.quantity.remove(position);
        this.unit.remove(position);
        notifyItemRemoved(position);
    }

}

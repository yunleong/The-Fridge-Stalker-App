package com.example.thestockers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemCustomAdapter extends RecyclerView.Adapter<ItemCustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList item_id, item_name, item_group, item_quantity;

    ItemCustomAdapter(Context context, ArrayList item_id, ArrayList item_name, ArrayList item_group, ArrayList item_quantity){
        this.context = context;
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_group = item_group;
        this.item_quantity = item_quantity;
    }

    @NonNull
    @Override
    public ItemCustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemCustomAdapter.MyViewHolder holder, int position) {
        holder.item_name_txt.setText(String.valueOf(item_name.get(position)));
        holder.item_quantity_txt.setText(String.valueOf(item_quantity.get(position)));
    }

    @Override
    public int getItemCount() {
        return item_name.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView item_name_txt, item_quantity_txt;
        ConstraintLayout itemLayout;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            item_name_txt = itemView.findViewById(R.id.item_name_txt);
            item_quantity_txt = itemView.findViewById(R.id.item_quantity_txt);
            itemLayout = itemView.findViewById(R.id.itemLayout);
        }
    }
}

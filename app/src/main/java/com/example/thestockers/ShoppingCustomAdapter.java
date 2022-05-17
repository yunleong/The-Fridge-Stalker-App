package com.example.thestockers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShoppingCustomAdapter extends RecyclerView.Adapter<ShoppingCustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList list_id, list_title;

    ShoppingCustomAdapter(Context context, ArrayList list_id, ArrayList list_title){
        this.context = context;
        this.list_id = list_id;
        this.list_title = list_title;
    }

    @NonNull
    @Override
    public ShoppingCustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.shopping_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingCustomAdapter.MyViewHolder holder, final int position) {
        holder.list_title_txt.setText(String.valueOf(list_title.get(position)));
        holder.shoppingLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewList.class);
                intent.putExtra("GROUP_ID",Integer.valueOf(String.valueOf(list_id.get(position))));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_title.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView list_title_txt;
        ConstraintLayout shoppingLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            list_title_txt = itemView.findViewById(R.id.list_title_txt);
            shoppingLayout = itemView.findViewById(R.id.shoppingLayout);
        }
    }
}

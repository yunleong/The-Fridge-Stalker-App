package com.example.thestockers;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    FloatingActionButton scanner_button;
    ImageView emptyImg;
    TextView emptyTV;
    int waste_consumed[] = new int[2];
    int waste = 10;
    int consumed = 10;

    HomeDatabaseHelper myDB;
    ArrayList<String> entry_id, entry_date, product_name, quantity, unit;
    HomeCustomAdapter customAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Find recycler view and scanner float button
        emptyImg = view.findViewById(R.id.empty_img);
        emptyTV = view.findViewById(R.id.empty_tv);
        recyclerView = view.findViewById(R.id.home_recyclerView);
        scanner_button = view.findViewById(R.id.scan_to_add_button);
        // When scanner button is clicked, start Scanner Activity
        scanner_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScannerActivity.class);
                startActivity(intent);
            }
        });
        myDB = new HomeDatabaseHelper(this.getActivity());
        entry_id = new ArrayList<>();
        entry_date = new ArrayList<>();
        product_name = new ArrayList<>();
        quantity = new ArrayList<>();
        unit = new ArrayList<>();
        // Query the remote db
        // RemoteDBHelper.populateDB(myDB);

        storeDataInArrays();

        customAdapter = new HomeCustomAdapter(this.getActivity(), this.getActivity(), entry_id, entry_date, product_name, quantity, unit);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeItem(customAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        setHasOptionsMenu(true);

        return view;
    }

    void storeDataInArrays(){
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0) {
            emptyImg.setVisibility(View.VISIBLE);
            emptyTV.setVisibility(View.VISIBLE);
        }else{
            while(cursor.moveToNext()){
                entry_id.add(cursor.getString(0));
                entry_date.add(cursor.getString(1));
                product_name.add(cursor.getString(2));
                quantity.add(cursor.getString(3));
                unit.add(cursor.getString(4));
            }
        }
    }

    public class SwipeItem extends ItemTouchHelper.SimpleCallback {
        HomeCustomAdapter adapter;
        public SwipeItem(HomeCustomAdapter itemAdapter) {
            super(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
            this.adapter = itemAdapter;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // delete from db
            int goneQty;
            int pos = viewHolder.getBindingAdapterPosition();
            HomeDatabaseHelper db = new HomeDatabaseHelper(HomeFragment.this.getActivity());
            goneQty = Integer.parseInt(quantity.get(pos));
            RemoteDBHelper.deleteDB(product_name.get(pos));
            db.deleteOneRow(entry_id.get(pos));
            this.adapter.deleteItem(pos);

            //store waste consumption quantity
            if(direction == ItemTouchHelper.RIGHT){
                waste = waste + goneQty;
                waste_consumed[0] = waste;
                Toast.makeText(getActivity(), "Wasted D:" , Toast.LENGTH_SHORT).show();

            }else if(direction == ItemTouchHelper.LEFT){
                consumed = consumed + goneQty;
                waste_consumed[1] = consumed;
                Toast.makeText(getActivity(), "Consumed :D" , Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
                int rowMarginTop = 4*3;
                int rowMarginBottom = 3;
                int itemHeight = viewHolder.itemView.getBottom() - viewHolder.itemView.getTop();
            //Swiped right
            if(dX > 0) {
                // Set red swipe background
                final ColorDrawable redBackground = new ColorDrawable(Color.parseColor("#FF0000"));
                redBackground.setBounds(0, (int)(viewHolder.itemView.getTop() + rowMarginTop),
                        (int) (viewHolder.itemView.getLeft() + dX), (int)(viewHolder.itemView.getBottom() - rowMarginBottom));
                redBackground.draw(c);

                // Calculate position of delete icon
                Drawable icon1 = ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete);
                int iconHeight = icon1.getIntrinsicHeight();
                int iconWidth = icon1.getIntrinsicWidth();
                int iconTop = viewHolder.itemView.getTop() + (itemHeight - iconHeight) / 2;
                int iconMargin = (itemHeight - iconHeight) / 2;
                int iconLeft1 = viewHolder.itemView.getLeft() + iconMargin;
                int iconRight1 = viewHolder.itemView.getLeft() + iconMargin + iconWidth;
                int iconBottom = iconTop + iconHeight;

                //Draw delete icon
                icon1.setBounds(iconLeft1, iconTop, iconRight1, iconBottom);
                icon1.draw(c);
            }

            //Swiped left
            if (dX < 0) {
                // Set green swipe background
                final ColorDrawable greenBackground = new ColorDrawable(Color.parseColor("#21B6A8"));
                greenBackground.setBounds(0, (int)(viewHolder.itemView.getTop() + rowMarginTop),
                        viewHolder.itemView.getRight(), (int)(viewHolder.itemView.getBottom() - rowMarginBottom));
                greenBackground.draw(c);

                // Calculate position of consumed icon
                Drawable icon2 = ContextCompat.getDrawable(getActivity(), R.drawable.consumed);
                int iconHeight = icon2.getIntrinsicHeight();
                int iconWidth = icon2.getIntrinsicWidth();
                int iconTop = viewHolder.itemView.getTop() + (itemHeight - iconHeight) / 2;
                int iconMargin = (itemHeight - iconHeight) / 2;
                int iconLeft2 = viewHolder.itemView.getRight() - iconMargin - iconWidth;
                int iconRight2 = viewHolder.itemView.getRight() - iconMargin;
                int iconBottom = iconTop + iconHeight;

                //Draw delete icon
                icon2.setBounds(iconLeft2, iconTop, iconRight2, iconBottom);
                icon2.draw(c);
            }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.top_menu, menu);

        //Action search function
        MenuItem menuItem = menu.findItem(R.id.search_btn);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Do I have ... ?");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.refresh_btn){
            // Query remote db to refresh when refresh button is pressed.
            RemoteDBHelper.populateDB(myDB);

            //Refresh fragment for result
            ((MainActivity)getActivity()).switchTab(R.id.homeFragment);

            Toast.makeText(getActivity(), "Database Up-to-date", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    /*public int[] getWasteConsumptionData(){
        return waste_consumed;
    }*/

    private void filter(String text){
        ArrayList<String> filteredList = new ArrayList<>();
        for(String item : product_name){
            if(item.toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        customAdapter.filterList(filteredList);
    }

}
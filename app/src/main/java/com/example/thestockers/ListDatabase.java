package com.example.thestockers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.List;

public class ListDatabase extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "ShoppingList.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "shopping_list";
    private static final String COLUMN_PRODUCT_NAME = "product_name";
    private static final String COLUMN_GROUP_ID = "group_id";
    private static final String COLUMN_QUANTITY = "quantity";

    private boolean LOCKED = false;

    public ListDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addItem(String name, int group, int quantity){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PRODUCT_NAME, name);
        cv.put(COLUMN_GROUP_ID, group);
        cv.put(COLUMN_QUANTITY, quantity);
        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }
        else{

            Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readAllData(int id){
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE group_id = " + id;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    void unlock() { LOCKED = false; }

    void lock() { LOCKED = true; }

    boolean isLocked() { return LOCKED; }
}

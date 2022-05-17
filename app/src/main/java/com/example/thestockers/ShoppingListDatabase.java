package com.example.thestockers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class ShoppingListDatabase extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "ShoppingList.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "my_shopping_lists";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "list_name";

    private static final String TABLE_NAME2 = "shopping_list";
    private static final String COLUMN_ID2 = "_id";
    private static final String COLUMN_PRODUCT_NAME2 = "product_name";
    private static final String COLUMN_GROUP_ID2 = "group_id";
    private static final String COLUMN_QUANTITY2 = "quantity";

    public ShoppingListDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABLE_NAME +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TITLE + " TEXT);";
        db.execSQL(query);
        query =
                "CREATE TABLE " + TABLE_NAME2 +
                        " (" + COLUMN_ID2 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_PRODUCT_NAME2 + " TEXT, " +
                        COLUMN_GROUP_ID2 + " INTEGER, " +
                        COLUMN_QUANTITY2 + " INTEGER);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        onCreate(db);
    }

    void addList(String title){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }
        else{
              // Will add this back in when the lists are more functional
//            RemoteDBHelper.insertList(String.valueOf(result), title);
            Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
}

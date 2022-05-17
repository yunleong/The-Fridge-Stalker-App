package com.example.thestockers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

class HomeDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "Stockers.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "home_inventory";
    private static final String COLUMN_ID = "home_inv_id";
    private static final String COLUMN_DATE = "home_date";
    private static final String COLUMN_HOME_PRODUCT_NAME = "home_product_name" ;
    private static final String COLUMN_HOME_QUANTITY = "home_quantity";
    private static final String COLUMN_UNIT_OF_MEASURE = "product_uom";

    private boolean LOCKED = false;

    HomeDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_DATE + " TEXT, " +
                        COLUMN_HOME_PRODUCT_NAME + " TEXT, " +
                        COLUMN_HOME_QUANTITY + " INTEGER, " +
                        COLUMN_UNIT_OF_MEASURE + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addItem(String name, int quantity, String uom) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String date = getDate();
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_HOME_PRODUCT_NAME, name);
        cv.put(COLUMN_HOME_QUANTITY, quantity);
        cv.put(COLUMN_UNIT_OF_MEASURE, uom);
        long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1){
            Toast.makeText(context, "Failed" , Toast.LENGTH_SHORT).show();
        }else{
            RemoteDBHelper.insertDB(String.valueOf(result), date, name, String.valueOf(quantity), uom);
            //Toast.makeText(context, "Added Successfully" , Toast.LENGTH_SHORT).show();
        }
    }

    void addItems(List<List<String>> dataList) {
        for(int i = 0; i < dataList.size(); i++) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_ID, dataList.get(i).get(0));
            cv.put(COLUMN_DATE, dataList.get(i).get(1));
            cv.put(COLUMN_HOME_PRODUCT_NAME, dataList.get(i).get(2));
            cv.put(COLUMN_HOME_QUANTITY, dataList.get(i).get(3));
            cv.put(COLUMN_UNIT_OF_MEASURE, dataList.get(i).get(4));
            long result = db.insert(TABLE_NAME, null, cv);
            if(result == -1){
                Toast.makeText(context, "Failed" , Toast.LENGTH_SHORT).show();
            }/*else{
                Toast.makeText(context, "Added Successfully" , Toast.LENGTH_SHORT).show();
            }*/
        }
    }

    // Returned cursor will contain all the data in the db
    Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    void updateData(String row_id, String product, String quantity, String unit){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_HOME_PRODUCT_NAME, product);
        cv.put(COLUMN_HOME_QUANTITY, quantity);
        cv.put(COLUMN_UNIT_OF_MEASURE, unit);

        long result = db.update(TABLE_NAME, cv, " home_inv_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Update Failed" , Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Updated" , Toast.LENGTH_SHORT).show();
        }
    }

    void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, " home_inv_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to Delete" , Toast.LENGTH_SHORT).show();
        }else{
            RemoteDBHelper.deleteDB(String.valueOf(row_id));
        }
    }

    void deleteAllRows(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }

    String getDate(){
        LocalDate dateObj = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        return dateObj.format(formatter);
    }

    void unlock() { LOCKED = false; }

    void lock() { LOCKED = true; }

    boolean isLocked() { return LOCKED; }

}

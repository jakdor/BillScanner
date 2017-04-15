package com.asi.billscanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.asi.billscanner.dbUtilities.BillsModel;

import java.sql.Date;
import java.util.Calendar;

/**
 * Wytyczne:
 * Klasa pośrednicząca w wymianie danych z lokalną bazą danych (SQLite)
 * - Dodawnie rekordów z klasy/struktury Bill
 * - Metody/kwerendy pozwalające pobierać i usuwać dane z DB
 *
 * Proponowany układ pól w db (pewnie jeszcze się zmieni):
 *
 * Tabele Bills i Products połączone relacją "jeden do wielu"
 *
 * Tabela Products:
 * (Id)[INTEGER], (BillId)[INTEGER], (Category)[VARCHAR(64)], (ProductName)[VARCHAR(128)], (Amount)[INTEGER], (Price)[MONEY]
 *
 * Tabela Bills:
 * (Id)[INTEGER], (AddTime)[TIMESTAMP], (BillDate)[DATE], (Company)[VARCHAR(128)], (Address)[VARCHAR(128)]
 */

class DbHandler {

    private static final String CLASS_TAG = "DbHandler";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "database.db";

    private static final int BILLS_ID_COL = 0;
    private static final int BILLS_ADD_TIME_COL = 1;
    private static final int BILLS_BILL_DATE_COL = 2;
    private static final int BILLS_COMPANY_COL = 3;
    private static final int BILLS_ADDRESS_COL = 4;

    private static final String DB_CREATE_BILLS_TABLE =
            "CREATE TABLE bills(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "addTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "billDate DATE, " +
                    "company VARCHAR(128), " +
                    "address VARCHAR(128)" +
                    ");";

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private Context context;

    private static class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(Context context, String name,
                              CursorFactory cursorFactory, int version){
            super(context, name, cursorFactory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_BILLS_TABLE);
            Log.i(CLASS_TAG, "Created bills table ver." + DB_VERSION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    DbHandler(Context context){
        this.context = context;
    }

    DbHandler openDb(){
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Log.wtf(CLASS_TAG, "Can't get db: " + e.getMessage());
        }
        return this;
    }

    void closeDb(){
        db.close();
    }

    long inserBills(String company, String address, int billYear, int billMonth, int billDay){

        String billDate = Integer.toString(billYear) + "-"
                + Integer.toString(billMonth) + "-"
                + Integer.toString(billDay);

        ContentValues contentValues = new ContentValues();
        contentValues.put("billDate", billDate);
        contentValues.put("company", company);
        contentValues.put("address", address);

        return db.insert("bills", null, contentValues);
    }

    boolean updateBills(BillsModel bill){

        String where = "_id=" + bill.getId();

        Date date = bill.getBillDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String billDate = Integer.toString(calendar.get(Calendar.YEAR)) + "-"
                + Integer.toString(calendar.get(Calendar.MONTH)) + "-"
                + Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));

        ContentValues contentValues = new ContentValues();
        contentValues.put("billDate", billDate);
        contentValues.put("company", bill.getCompany());
        contentValues.put("address", bill.getAddress());

        return db.update("bills", contentValues, where, null) > 0;
    }

    boolean deleteBills(int id){
        String where = "_id=" + id;
        return db.delete("bills", where, null) > 0;
    }

    Cursor getAllBills(){
        String[] columns = {"_id", "addTime", "billDate", "company", "address"};
        return db.query("bills", columns, null, null, null, null, null);
    }


}

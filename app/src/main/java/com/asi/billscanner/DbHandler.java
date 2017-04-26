package com.asi.billscanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.asi.billscanner.dbUtilities.BillsModel;
import com.asi.billscanner.dbUtilities.ProductsModel;

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

    static final String TABLE_BILLS = "bills";

    static final String BILLS_ID = "_id";
    static final String BILLS_ADD_TIME = "addTime";
    static final String BILLS_BILL_DATE = "billDate";
    static final String BILLS_COMPANY = "company";
    static final String BILLS_ADDRESS = "address";

    private static final int BILLS_ID_COL = 0;
    private static final int BILLS_ADD_TIME_COL = 1;
    private static final int BILLS_BILL_DATE_COL = 2;
    private static final int BILLS_COMPANY_COL = 3;
    private static final int BILLS_ADDRESS_COL = 4;

    private static final String DB_CREATE_BILLS_TABLE =
            "CREATE TABLE " + TABLE_BILLS + "(" +
                    BILLS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    BILLS_ADD_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    BILLS_BILL_DATE + " DATE, " +
                    BILLS_COMPANY + " VARCHAR(128), " +
                    BILLS_ADDRESS + " VARCHAR(128)" +
                    ");";

    static final String TABLE_PRODUCTS = "products";

    static final String PRODUCTS_ID = "_id";
    static final String PRODUCTS_BILL_ID = "billId";
    static final String PRODUCTS_CATEGORY = "category";
    static final String PRODUCTS_PRODUCT_NAME = "productName";
    static final String PRODUCTS_AMOUNT = "amount";
    static final String PRODUCTS_PRICE = "price";

    private static final int PRODUCTS_ID_COL = 0;
    private static final int PRODUCTS_BILL_ID_COL = 1;
    private static final int PRODUCTS_CETEGORY_COL = 2;
    private static final int PRODUCTS_PRODUCT_NAME_COL = 3;
    private static final int PRODUCTS_AMOUNT_COL = 4;
    private static final int PRODUCTS_PRICE_COL = 5;

    //price - currency stored in cents(grosze)
    private static final String DB_CREATE_PRODUCTS_TABLE =
            "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                    PRODUCTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "FOREIGN KEY (" + PRODUCTS_BILL_ID + ") REFERENCES " + TABLE_BILLS + "(" + BILLS_ID + "), " +
                    PRODUCTS_CATEGORY + " VARCHAR(64), " +
                    PRODUCTS_PRODUCT_NAME + " VARCHAR(128), " +
                    PRODUCTS_AMOUNT + " INTEGER, " +
                    PRODUCTS_PRICE + " INTEGER" +
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
            Log.i(CLASS_TAG, "Created " + TABLE_BILLS + " table ver." + DB_VERSION);
            db.execSQL(DB_CREATE_PRODUCTS_TABLE);
            Log.i(CLASS_TAG, "Created " + TABLE_PRODUCTS + " tab ver." + DB_VERSION);
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
            Log.wtf(CLASS_TAG, "Can't get db access: " + e.getMessage());
        }
        return this;
    }

    void closeDb(){
        db.close();
    }

    /**
     * operations on bills table
     */
    long insertBills(String company, String address, int billYear, int billMonth, int billDay){

        String billDate = Integer.toString(billYear) + "-"
                + Integer.toString(billMonth) + "-"
                + Integer.toString(billDay);

        ContentValues contentValues = new ContentValues();
        contentValues.put(BILLS_BILL_DATE, billDate);
        contentValues.put(BILLS_COMPANY, company);
        contentValues.put(BILLS_ADDRESS, address);

        return db.insert(TABLE_BILLS, null, contentValues);
    }

    boolean updateBills(BillsModel bill){

        String where = BILLS_ID + "=" + Integer.toString(bill.getId());

        Date date = bill.getBillDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String billDate = Integer.toString(calendar.get(Calendar.YEAR)) + "-"
                + Integer.toString(calendar.get(Calendar.MONTH)) + "-"
                + Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));

        ContentValues contentValues = new ContentValues();
        contentValues.put(BILLS_BILL_DATE, billDate);
        contentValues.put(BILLS_COMPANY, bill.getCompany());
        contentValues.put(BILLS_ADDRESS, bill.getAddress());

        return db.update(TABLE_BILLS, contentValues, where, null) > 0;
    }

    boolean deleteBills(int id){
        String where = BILLS_ID + "=" + Integer.toString(id);
        return db.delete(TABLE_BILLS, where, null) > 0;
    }

    Cursor getBillById(int id){
        String[] columns = {BILLS_BILL_DATE, BILLS_COMPANY, BILLS_ADDRESS};
        String where = BILLS_ID + "=" + Integer.toString(id);
        return db.query(TABLE_BILLS, columns, where, null, null, null, null);
    }

    Cursor getAllBills(){
        String[] columns = {BILLS_ID, BILLS_ADD_TIME, BILLS_BILL_DATE, BILLS_COMPANY, BILLS_ADDRESS};
        return db.query(TABLE_BILLS, columns, null, null, null, null, null);
    }

    /**
     * operations on products table
     */
    long insertProducts(long billId, String category, String productName, int amount, double price){

        int cash = (int)(price * 100);

        ContentValues contentValues = new ContentValues();
        contentValues.put(PRODUCTS_BILL_ID, billId);
        contentValues.put(PRODUCTS_CATEGORY, category);
        contentValues.put(PRODUCTS_PRODUCT_NAME,  productName);
        contentValues.put(PRODUCTS_AMOUNT, amount);
        contentValues.put(PRODUCTS_PRICE, cash);

        return db.insert(TABLE_PRODUCTS, null, contentValues);
    }

    boolean updateProducts(ProductsModel product){

        String where = BILLS_ID + "=" + Integer.toString(product.getId());

        ContentValues contentValues = new ContentValues();
        contentValues.put(PRODUCTS_BILL_ID, product.getBillId());
        contentValues.put(PRODUCTS_CATEGORY, product.getCategory());
        contentValues.put(PRODUCTS_PRODUCT_NAME, product.getProductName());
        contentValues.put(PRODUCTS_AMOUNT, product.getAmount());
        contentValues.put(PRODUCTS_PRICE, product.getPrice());

        return db.update(TABLE_PRODUCTS, contentValues, where, null) > 0;
    }

    boolean deleteProducts(int id){
        String where = BILLS_ID + "=" + Integer.toString(id);
        return db.delete(TABLE_PRODUCTS, where, null) > 0;
    }

    boolean deleteProductsByBillId(int billId){
        String where = PRODUCTS_BILL_ID + "=" + Integer.toString(billId);
        return db.delete(TABLE_PRODUCTS, where, null) > 0;
    }

    boolean deleteProductsByName(int billId, String productName){
        String where = PRODUCTS_BILL_ID + "=" + Integer.toString(billId) + " AND " +
                PRODUCTS_PRODUCT_NAME + "=" + productName;

        return db.delete(TABLE_PRODUCTS, where, null) > 0;
    }

    Cursor getProductsByBillId(int billId){
        String[] columns = {PRODUCTS_CATEGORY,
                PRODUCTS_PRODUCT_NAME,
                PRODUCTS_AMOUNT,
                PRODUCTS_PRICE};
        String where = PRODUCTS_BILL_ID + "=" + Integer.toString(billId);
        return db.query(TABLE_PRODUCTS, columns, where, null, null, null, null);
    }

    Cursor getDaySum(int day, int month, int year){
        String date = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day);
        String where = PRODUCTS_BILL_ID + "=(SELECT " + BILLS_ID + " FROM " + TABLE_BILLS + " WHERE " +
                BILLS_BILL_DATE + " = " + date + ")";
        String query = "SELECT SUM(" + PRODUCTS_PRICE + ") FROM " + TABLE_PRODUCTS + " WHERE " + where;
        return db.rawQuery(query, null);
    }

    Cursor getMonthSum(int month, int year){
        String dataPart = "DATEPART(year," + BILLS_BILL_DATE + ")=" + Integer.toString(year) + " AND " +
                "DATEPART(month," + BILLS_BILL_DATE + ")=" + Integer.toString(month);
        String where = PRODUCTS_BILL_ID +
                "=(SELECT " + BILLS_ID + " FROM " + TABLE_BILLS + " WHERE " + dataPart + ")";
        String query = "SELECT SUM(" + PRODUCTS_PRICE + ") FROM " + TABLE_PRODUCTS + " WHERE " + where;
        return db.rawQuery(query, null);
    }

    Cursor getAllProducts(){
        String[] columns = {BILLS_ID,
                PRODUCTS_BILL_ID,
                PRODUCTS_CATEGORY,
                PRODUCTS_PRODUCT_NAME,
                PRODUCTS_AMOUNT,
                PRODUCTS_PRICE};
        return db.query(TABLE_PRODUCTS, columns, null, null, null, null, null);
    }
}

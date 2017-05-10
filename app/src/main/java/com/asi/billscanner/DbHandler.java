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

    static final int DB_VERSION = 1;
    static final String DB_NAME = "database.db";

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
    static final String PRODUCTS_CATEGORY = "category";
    static final String PRODUCTS_PRODUCT_NAME = "productName";
    static final String PRODUCTS_AMOUNT = "amount";
    static final String PRODUCTS_PRICE = "price";
    static final String PRODUCTS_BILL_ID = "billId";

    private static final int PRODUCTS_ID_COL = 0;
    private static final int PRODUCTS_CATEGORY_COL = 1;
    private static final int PRODUCTS_PRODUCT_NAME_COL = 2;
    private static final int PRODUCTS_AMOUNT_COL = 3;
    private static final int PRODUCTS_PRICE_COL = 4;
    private static final int PRODUCTS_BILL_ID_COL = 5;

    //price - currency stored in cents(grosze)
    private static final String DB_CREATE_PRODUCTS_TABLE =
            "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                    PRODUCTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PRODUCTS_CATEGORY + " VARCHAR(64), " +
                    PRODUCTS_PRODUCT_NAME + " VARCHAR(128), " +
                    PRODUCTS_AMOUNT + " REAL, " +
                    PRODUCTS_PRICE + " INTEGER, " +
                    PRODUCTS_BILL_ID + " INTEGER, " +
                    "FOREIGN KEY (" + PRODUCTS_BILL_ID + ") REFERENCES " + TABLE_BILLS + "(" + BILLS_ID + ")" +
                    ");";

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private Context context;

    private static class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(Context context, String name,
                              CursorFactory cursorFactory, int version){
            super(context, name, cursorFactory, version);
            Log.i(CLASS_TAG, "Checking local db status...");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_BILLS_TABLE);
            Log.i(CLASS_TAG, "Created " + TABLE_BILLS + " table ver." + DB_VERSION);
            db.execSQL(DB_CREATE_PRODUCTS_TABLE);
            Log.i(CLASS_TAG, "Created " + TABLE_PRODUCTS + " table ver." + DB_VERSION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(CLASS_TAG, "Updating local db to ver." + DB_VERSION);
        }
    }

    DbHandler(Context context){
        this.context = context;
    }

    DbHandler openDb(){
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
            Log.i(CLASS_TAG, "Local db access established");
        }catch (Exception e){
            Log.wtf(CLASS_TAG, "Can't get db access: " + e.getMessage());
        }
        return this;
    }

    boolean isDbOpen(){
        return db.isOpen();
    }

    void closeDb(){
        db.close();
        Log.i(CLASS_TAG, "closed local db connection");
    }

    private String intDateToDbFormat(int input){
        return input < 10 ? "0" + Integer.toString(input) : Integer.toString(input);
    }

    /**
     * operations on bills table
     */
    long insertBills(String company, String address, int billYear, int billMonth, int billDay){

        String billDate = Integer.toString(billYear) + "-"
                + intDateToDbFormat(billMonth) + "-"
                + intDateToDbFormat(billDay);

        ContentValues contentValues = new ContentValues();
        contentValues.put(BILLS_BILL_DATE, billDate);
        contentValues.put(BILLS_COMPANY, company);
        contentValues.put(BILLS_ADDRESS, address);

        return db.insert(TABLE_BILLS, null, contentValues);
    }

    boolean updateBills(BillsModel bill){

        String where = BILLS_ID + "=" + Integer.toString(bill.getId());

        ContentValues contentValues = new ContentValues();
        contentValues.put(BILLS_BILL_DATE, bill.getBillDate());
        contentValues.put(BILLS_COMPANY, bill.getCompany());
        contentValues.put(BILLS_ADDRESS, bill.getAddress());

        return db.update(TABLE_BILLS, contentValues, where, null) > 0;
    }

    boolean deleteBills(int id){
        String where = BILLS_ID + "=" + Integer.toString(id);
        return db.delete(TABLE_BILLS, where, null) > 0;
    }

    Cursor getBillById(int id){
        String[] columns = {BILLS_BILL_DATE, BILLS_COMPANY, BILLS_ADDRESS, BILLS_ID};
        String where = BILLS_ID + "=" + Integer.toString(id);
        return db.query(TABLE_BILLS, columns, where, null, null, null, null);
    }

    Cursor getBillsIdFromDay(int day, int month, int year){
        String[] columns = {BILLS_ID};
        String date = Integer.toString(year) + "-" + intDateToDbFormat(month) + "-" + intDateToDbFormat(day);
        String where = BILLS_BILL_DATE + "='" + date + "'";
        return db.query(TABLE_BILLS, columns, where, null, null, null, null);
    }

    Cursor getAllBills(){
        String[] columns = {BILLS_ID, BILLS_ADD_TIME, BILLS_BILL_DATE, BILLS_COMPANY, BILLS_ADDRESS};
        return db.query(TABLE_BILLS, columns, null, null, null, null, null);
    }

    /**
     * operations on products table
     */
    long insertProducts(long billId, String category, String productName, double amount, double price){

        int cash = (int)(price * 100);

        ContentValues contentValues = new ContentValues();
        contentValues.put(PRODUCTS_CATEGORY, category);
        contentValues.put(PRODUCTS_PRODUCT_NAME,  productName);
        contentValues.put(PRODUCTS_AMOUNT, amount);
        contentValues.put(PRODUCTS_PRICE, cash);
        contentValues.put(PRODUCTS_BILL_ID, billId);

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
                PRODUCTS_PRODUCT_NAME + "='" + productName +"'";

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

    Cursor getCategoryDaySum(String category, int day, int month, int year){
        String where = TABLE_PRODUCTS + "." + PRODUCTS_CATEGORY + "='" + category + "' and " +
                TABLE_BILLS + "." + BILLS_BILL_DATE + " = '" + Integer.toString(year) + "-" +
                intDateToDbFormat(month) + "-" + intDateToDbFormat(day) + "'";
        String query = "SELECT SUM(" + PRODUCTS_AMOUNT +
                " * " + PRODUCTS_PRICE + ") FROM " + TABLE_PRODUCTS +
                " INNER JOIN " + TABLE_BILLS + " on " + TABLE_BILLS + "." + BILLS_ID + "=" +
                TABLE_PRODUCTS + "." + PRODUCTS_BILL_ID +
                " WHERE " + where;
        return db.rawQuery(query, null);
    }

    Cursor getCategoryMonthSum(String category, int month, int year) {
        String where = TABLE_PRODUCTS + "." + PRODUCTS_CATEGORY + "='" + category +
                "' and " + TABLE_BILLS + "." + BILLS_BILL_DATE + " LIKE '" + Integer.toString(year)
                + "-" + intDateToDbFormat(month) + "%'";
        String query = "SELECT SUM(" +  PRODUCTS_AMOUNT + " * " +  PRODUCTS_PRICE + ") FROM " +
                TABLE_PRODUCTS + " INNER JOIN " + TABLE_BILLS + " on " + TABLE_BILLS + "." +
                BILLS_ID + "=" + TABLE_PRODUCTS + "." + PRODUCTS_BILL_ID + " WHERE " + where;
        return db.rawQuery(query, null);
    }

    Cursor getCategorySumFromTo(String category, int fromDay, int fromMonth, int fromYear,
                                int toDay, int toMonth, int toYear) {
        String[] day = {intDateToDbFormat(fromDay), intDateToDbFormat(toDay)};
        String[] month = {intDateToDbFormat(fromMonth), intDateToDbFormat(toMonth)};
        String where = TABLE_PRODUCTS + "." + PRODUCTS_CATEGORY + "='" + category +
                "' and " + TABLE_BILLS + "." + BILLS_BILL_DATE + " between '" +
                Integer.toString(fromYear) + "-" + month[0] + "-" + day[0] + "' and '" +
                Integer.toString(toYear) + "-" + month[1] + "-" + day[1] + "'";
        String query = "SELECT SUM(" +  PRODUCTS_AMOUNT + " * " +  PRODUCTS_PRICE + ") FROM " +
                TABLE_PRODUCTS + " INNER JOIN " + TABLE_BILLS + " on " + TABLE_BILLS + "." +
                BILLS_ID + "=" + TABLE_PRODUCTS + "." + PRODUCTS_BILL_ID + " WHERE " + where;
        return db.rawQuery(query, null);
    }

    Cursor getDaySum(int day, int month, int year){
        String date = Integer.toString(year) + "-" + intDateToDbFormat(month) + "-" + intDateToDbFormat(day);
        String where = TABLE_BILLS + "." + BILLS_BILL_DATE + " = '" + date + "'";
        String query = "SELECT SUM(" + PRODUCTS_AMOUNT +
                " * " + PRODUCTS_PRICE + ") FROM " + TABLE_PRODUCTS +
                " INNER JOIN " + TABLE_BILLS + " on " + TABLE_BILLS + "." + BILLS_ID + "=" +
                TABLE_PRODUCTS + "." + PRODUCTS_BILL_ID +
                " WHERE " + where;
        return db.rawQuery(query, null);
    }

    Cursor getMonthSum(int month, int year){
        String where = TABLE_BILLS + "." + BILLS_BILL_DATE + " LIKE '" + Integer.toString(year) +
                "-" + intDateToDbFormat(month) + "%'";
        String query = "SELECT SUM(" +  PRODUCTS_AMOUNT + " * " +  PRODUCTS_PRICE + ") FROM " +
                TABLE_PRODUCTS + " INNER JOIN " + TABLE_BILLS + " on " + TABLE_BILLS + "." +
                BILLS_ID + "=" + TABLE_PRODUCTS + "." + PRODUCTS_BILL_ID + " WHERE " + where;
        return db.rawQuery(query, null);
    }

    Cursor getSumFromTo(int fromDay, int fromMonth, int fromYear, int toDay, int toMonth, int toYear){
        String[] day = {intDateToDbFormat(fromDay), intDateToDbFormat(toDay)};
        String[] month = {intDateToDbFormat(fromMonth), intDateToDbFormat(toMonth)};
        String where = TABLE_BILLS + "." + BILLS_BILL_DATE + " between '" +
                Integer.toString(fromYear) + "-" + month[0] + "-" + day[0] + "' and '" +
                Integer.toString(toYear) + "-" + month[1] + "-" + day[1] + "'";
        String query = "SELECT SUM(" +  PRODUCTS_AMOUNT + " * " +  PRODUCTS_PRICE + ") FROM " +
                TABLE_PRODUCTS + " INNER JOIN " + TABLE_BILLS + " on " + TABLE_BILLS + "." +
                BILLS_ID + "=" + TABLE_PRODUCTS + "." + PRODUCTS_BILL_ID + " WHERE " + where;
        return db.rawQuery(query, null);
    }

    Cursor getUsedCategories(){
        String[] columns = {PRODUCTS_CATEGORY};
        return db.query(TABLE_PRODUCTS, columns, null, null, columns[0], null, columns[0]);
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

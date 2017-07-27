package com.asi.billscanner;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;

import com.asi.billscanner.dbUtilities.BillsModel;

import java.util.Vector;

public class BillsAdapter {

    private static final String CLASS_TAG = "BillsAdapter";
    private DbHandler dbHandler;

    BillsAdapter(DbHandler dbHandler){
        this.dbHandler = dbHandler;
    }

    /**
     * Inserts bill object into db
     */
    void addBillToDB(Bill bill){

        String dateStr = bill.getDate();
        int[] date = new int[3];

        if(!dateStr.isEmpty()){
            String[] parts = dateStr.split("-");
            if(Integer.valueOf(parts[0]) > 999) {
                for(int i = 0; i < 3; ++i){
                    date[i] = Integer.valueOf(parts[i]);
                }
            }
        }
        else{
            for(int i = 0; i < 3; ++i) {
                date[i] = 0;
            }
        }

        Long billId = dbHandler.insertBills(
                bill.getCompany(),
                bill.getAddress(),
                date[0],
                date[1],
                date[2]);

        if(billId == -1){
            Log.wtf(CLASS_TAG, "failed to insert bill row into db");
            throw new RuntimeException(CLASS_TAG + ": failed to insert bill row into db");
        }

        int productsCount = bill.getProductsSize();
        if(productsCount == 0){
            return;
        }

        for(int i = 0; i < productsCount; ++i) {
             if(dbHandler.insertProducts(
                     billId,
                     bill.getProductCategoryAtIndex(i),
                     bill.getProductNameAtIndex(i),
                     bill.getProductAmountAtIndex(i),
                     bill.getProductPriceAtIndex(i)) == -1){
                 Log.wtf(CLASS_TAG, "failed to insert product row into db");
                 throw new RuntimeException(CLASS_TAG + ": failed to insert product row into db");
             }
        }
    }

    /**
     * update Bill existing in db
     */
    void updateBillInDB(Bill bill){
        if(bill.getDbId() == -1){
            Log.e(CLASS_TAG, "bill has unspecified dbId field");
            throw new RuntimeException(CLASS_TAG + ": bill has unspecified dbId field");
        }

        BillsModel billsModel = new BillsModel(
                bill.getDbId(), null, bill.getDate(), bill.getCompany(), bill.getAddress());

        deleteProductsByBillId(bill.getDbId());

        for(int i = 0; i < bill.getProductsSize(); ++i) {
            dbHandler.insertProducts(bill.getDbId(),
                    bill.getProductCategoryAtIndex(i),
                    bill.getProductNameAtIndex(i),
                    bill.getProductAmountAtIndex(i),
                    bill.getProductPriceAtIndex(i));
        }

        dbHandler.updateBills(billsModel);
    }

    /**
     * loads Bill from db by id
     */
    Bill getBillById(int id){
        Bill bill;

        Cursor billsCursor = dbHandler.getBillById(id);
        if(billsCursor == null) {
            Log.wtf(CLASS_TAG, "db query returned null");
            throw new RuntimeException(CLASS_TAG + ": db query returned null");
        }

        billsCursor.moveToFirst();
        String data = billsCursor.getString(billsCursor.getColumnIndex(DbHandler.BILLS_BILL_DATE));
        String company = billsCursor.getString(billsCursor.getColumnIndex(DbHandler.BILLS_COMPANY));
        String address = billsCursor.getString(billsCursor.getColumnIndex(DbHandler.BILLS_ADDRESS));
        int dbId = billsCursor.getInt(billsCursor.getColumnIndex(DbHandler.BILLS_ID));
        billsCursor.close();

        Log.d(CLASS_TAG, "Bill data from db: "+ dbId + " " + data + " " + company + " " + address);

        bill = new Bill(data, company, address, dbId);

        Cursor productsCursor = dbHandler.getProductsByBillId(id);
        if(productsCursor == null){
            Log.wtf(CLASS_TAG, "db query returned null");
            throw new RuntimeException(CLASS_TAG + ": db query returned null");
        }

        Log.d(CLASS_TAG,  DatabaseUtils.dumpCursorToString(productsCursor));

        productsCursor.moveToFirst();
        for(int i = 0; i < productsCursor.getCount(); ++i){
            String category = productsCursor.getString(productsCursor.getColumnIndex(DbHandler.PRODUCTS_CATEGORY));
            String name = productsCursor.getString(productsCursor.getColumnIndex(DbHandler.PRODUCTS_PRODUCT_NAME));
            double amount = productsCursor.getDouble(productsCursor.getColumnIndex(DbHandler.PRODUCTS_AMOUNT));
            int price = productsCursor.getInt(productsCursor.getColumnIndex(DbHandler.PRODUCTS_PRICE));

            double finalPrice = (double)price/100.0;
            bill.addNewProduct(name, category, amount, finalPrice);

            productsCursor.moveToNext();
        }

        productsCursor.close();

        return bill;
    }

    Vector <Bill> getBillsWithDate(int day, int month, int year){
        Vector <Bill> bills = new Vector<>();

        Cursor billIds = dbHandler.getBillsIdFromDay(day, month, year);
        if(billIds == null){
            Log.wtf(CLASS_TAG, "db query returned null");
            throw new RuntimeException(CLASS_TAG + ": db query returned null");
        }

        Log.d(CLASS_TAG, DatabaseUtils.dumpCursorToString(billIds));

        billIds.moveToFirst();
        for(int i = 0; i < billIds.getCount(); ++i){
            bills.add(getBillById(billIds.getInt(billIds.getColumnIndex(DbHandler.BILLS_ID))));
            billIds.moveToNext();
        }
        billIds.close();

        return bills;
    }

    /**
     * CategorySum queries - returns products price filtered by category from given period
     */
    double getCategorySum(String category, int fromDay, int fromMonth,
                             int fromYear, int toDay, int toMonth, int toYear){
        Cursor sumCursor = dbHandler.getCategorySumFromTo(
                category, fromDay, fromMonth, fromYear, toDay, toMonth, toYear);
        return getSumCore(sumCursor);
    }

    double getCategorySum(String category, int day, int month, int year){
        Cursor sumCursor = dbHandler.getCategoryDaySum(category, day, month, year);
        return getSumCore(sumCursor);
    }

    double getCategorySum(String category, int month, int year){
        Cursor sumCursor = dbHandler.getCategoryMonthSum(category, month, year);
        return getSumCore(sumCursor);
    }

    /**
     * Sum queries - returns products price from given period
     */
    double getSum(int fromDay, int fromMonth, int fromYear, int toDay, int toMonth, int toYear){
        Cursor sumCursor = dbHandler.getSumFromTo(fromDay, fromMonth, fromYear, toDay, toMonth, toYear);
        return getSumCore(sumCursor);
    }

    double getSum(int day, int month, int year){
        Cursor sumCursor = dbHandler.getDaySum(day, month, year);
        return getSumCore(sumCursor);
    }

    double getSum(int month, int year){
        Cursor sumCursor = dbHandler.getMonthSum(month, year);
        return getSumCore(sumCursor);
    }

    /**
     * converts sum type query result into double
     */
    private double getSumCore(Cursor sumCursor){
        if(sumCursor == null){
            Log.wtf(CLASS_TAG, "db query returned null");
            throw new RuntimeException(CLASS_TAG + ": db query returned null");
        }

        sumCursor.moveToFirst();
        double sum = sumCursor.getDouble(0) / 100.0;
        sumCursor.close();

        return sum;
    }

    /**
     * gets all categories from db
     */
    public Vector<String> getUsedCategories(){
        Vector<String> categories = new Vector<>();

        Cursor categoriesCursor = dbHandler.getUsedCategories();
        if(categoriesCursor == null){
            Log.wtf(CLASS_TAG, "db query returned null");
            throw new RuntimeException(CLASS_TAG + ": db query returned null");
        }

        categoriesCursor.moveToFirst();
        for(int i = 0; i < categoriesCursor.getCount(); ++i){
            categories.add(categoriesCursor.getString(0));
            categoriesCursor.moveToNext();
        }

        return categories;
    }

    /**
     * delete bill row and linked products
     */
    void deleteBillById(int id){
        if(!dbHandler.deleteBills(id)){
            Log.wtf(CLASS_TAG, "failed to delete bill row from db");
            throw new RuntimeException(CLASS_TAG + ": failed to delete bill row from db");
        }

        if(!dbHandler.deleteProductsByBillId(id)){
            Log.wtf(CLASS_TAG, "failed to delete products row/s from db");
            throw new RuntimeException(CLASS_TAG + ": failed to delete products row/s from db");
        }
    }

    /**
     * delete single product entry from bill
     */
    void deleteProductsByName(int billId, String productName){
        if(!dbHandler.deleteProductsByName(billId, productName)){
            Log.wtf(CLASS_TAG, "failed to delete product linked to given bill");
            throw new RuntimeException(CLASS_TAG + ": failed to delete product linked to given bill");
        }
    }

    /**
     * delete all products from bill
     */
    void deleteProductsByBillId(int billId){
        if(!dbHandler.deleteProductsByBillId(billId)){
            Log.wtf(CLASS_TAG, "failed to delete all products linked to given bill");
            throw new RuntimeException(CLASS_TAG + ": failed to delete all products linked to given bill");
        }
    }

    /**
     * gets all categories discarded by user
     */
    public Vector<String> getDiscardedCategories(){
        Vector<String> categories = new Vector<>();

        Cursor categoriesCursor = dbHandler.getDiscardedCategories();
        if(categoriesCursor == null){
            Log.wtf(CLASS_TAG, "db query returned null");
            throw new RuntimeException(CLASS_TAG + ": db query returned null");
        }

        categoriesCursor.moveToFirst();
        for(int i = 0; i < categoriesCursor.getCount(); ++i){
            categories.add(categoriesCursor.getString(0));
            categoriesCursor.moveToNext();
        }

        return categories;
    }

    /**
     * delete discarded category by name
     */
    public void deleteDiscardedCategory(String category){
        if(!dbHandler.deleteDiscardedCategory(category)){
            Log.wtf(CLASS_TAG, "failed to delete discarded category from db");
            throw new RuntimeException(CLASS_TAG + ": failed to delete discarded category from db");
        }
    }

    /**
     * add discarded category to db
     */
    public void addDiscardedCategory(String category) {
        if(dbHandler.insertDiscardedCategory(category) == -1){
            Log.wtf(CLASS_TAG, "failed to insert discarded category into db");
            throw new RuntimeException(CLASS_TAG + ": failed to insert discarded category into db");
        }
    }
}

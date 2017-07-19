package com.asi.billscanner;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import static junit.framework.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.Vector;

@RunWith(AndroidJUnit4.class)
public class DbTest{

    private static final double DELTA = 0.0001;

    private Context mockContext;
    private DbHandler dbHandler;
    private BillsAdapter billsAdapter;

    private final String DUMMY_COMPANY_1 = "Biedra";
    private final String DUMMY_DATE_1 = "2017-02-01";
    private final String DUMMY_ADDRESS_1 = "12 Baker st. 23543 London";
    private final String DUMMY_COMPANY_2 = "Lidle";
    private final String DUMMY_DATE_2 = "2017-02-03";
    private final String DUMMY_ADDRESS_2 = "31 Długa 32-123 Wrocław";

    private final String DUMMY_NAME_1 = "Ser";
    private final String DUMMY_CATEGORY_1 = "Spożywcze 123";
    private final double DUMMY_AMOUNT_1 = 3.54;
    private final double DUMMY_PRICE_1 = 1.23;
    private final String DUMMY_NAME_2 = "Modelarstwo";
    private final String DUMMY_CATEGORY_2 = "Hobby";
    private final double DUMMY_AMOUNT_2 = 1.0;
    private final double DUMMY_PRICE_2 = 13.99;

    private final String DUMMY_DC_1 = "dupa";
    private final String DUMMY_DC_2 = "Spożywcze 123";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        mockContext = new RenamingDelegatingContext(InstrumentationRegistry.getTargetContext(), "test_");

        dbHandler = new DbHandler(mockContext);
        billsAdapter = new BillsAdapter(dbHandler);
        dbHandler.openDb();

        Bill dummyBill = new Bill(DUMMY_DATE_1, DUMMY_COMPANY_1, DUMMY_ADDRESS_1);
        dummyBill.addNewProduct(DUMMY_NAME_1, DUMMY_CATEGORY_1, DUMMY_AMOUNT_1, DUMMY_PRICE_1);
        dummyBill.addNewProduct(DUMMY_NAME_2, DUMMY_CATEGORY_2, DUMMY_AMOUNT_2, DUMMY_PRICE_2);

        Bill dummyBill2 = new Bill(DUMMY_DATE_2, DUMMY_COMPANY_2, DUMMY_ADDRESS_2);
        dummyBill2.addNewProduct(DUMMY_NAME_2, DUMMY_CATEGORY_2, DUMMY_AMOUNT_2, DUMMY_PRICE_2);

        Bill dummyBill3 = new Bill(DUMMY_DATE_2, DUMMY_COMPANY_1, DUMMY_ADDRESS_1);
        dummyBill3.addNewProduct(DUMMY_NAME_1, DUMMY_CATEGORY_1, DUMMY_AMOUNT_1, DUMMY_PRICE_1);

        billsAdapter.addBillToDB(dummyBill);
        billsAdapter.addBillToDB(dummyBill2);
        billsAdapter.addBillToDB(dummyBill3);

        billsAdapter.addDiscardedCategory(DUMMY_DC_1);
        billsAdapter.addDiscardedCategory(DUMMY_DC_2);
    }

    @After
    public void cleanUp(){
        dbHandler.closeDb();
    }

    @Test
    public void getAllBills() throws Exception {
        Cursor cursor = dbHandler.getAllBills();

        cursor.moveToFirst();

        assertEquals(3, cursor.getCount());
        assertEquals(5, cursor.getColumnCount());
        assertEquals(1, cursor.getInt(cursor.getColumnIndex(DbHandler.BILLS_ID)));
        cursor.moveToLast();
        assertEquals(3, cursor.getInt(cursor.getColumnIndex(DbHandler.BILLS_ID)));

        cursor.close();
    }

    @Test
    public void getAllProducts() throws Exception {
        Cursor cursor = dbHandler.getAllProducts();

        cursor.moveToFirst();

        assertEquals(4, cursor.getCount());
        assertEquals(6, cursor.getColumnCount());

        //check relation with bills
        assertEquals(1, cursor.getInt(cursor.getColumnIndex(DbHandler.PRODUCTS_ID)));
        assertEquals(1, cursor.getInt(cursor.getColumnIndex(DbHandler.PRODUCTS_BILL_ID)));
        cursor.moveToNext();
        assertEquals(2, cursor.getInt(cursor.getColumnIndex(DbHandler.PRODUCTS_ID)));
        assertEquals(1, cursor.getInt(cursor.getColumnIndex(DbHandler.PRODUCTS_BILL_ID)));
        cursor.moveToNext();
        assertEquals(3, cursor.getInt(cursor.getColumnIndex(DbHandler.PRODUCTS_ID)));
        assertEquals(2, cursor.getInt(cursor.getColumnIndex(DbHandler.PRODUCTS_BILL_ID)));

        cursor.close();
    }

    @Test
    public void addBillToDb() throws Exception {
        Bill dummyBill = new Bill(DUMMY_DATE_2, DUMMY_COMPANY_1, DUMMY_ADDRESS_2);
        dummyBill.addNewProduct(DUMMY_NAME_1, DUMMY_CATEGORY_2, DUMMY_AMOUNT_2, DUMMY_PRICE_1);

        Cursor cursor = dbHandler.getAllBills();
        assertEquals(3, cursor.getCount());
        assertEquals(5, cursor.getColumnCount());
        cursor.close();

        billsAdapter.addBillToDB(dummyBill);
        cursor = dbHandler.getAllBills();

        assertEquals(4, cursor.getCount());
        assertEquals(5, cursor.getColumnCount());
        cursor.moveToLast();
        assertEquals(4, cursor.getInt(cursor.getColumnIndex(DbHandler.BILLS_ID)));
        assertEquals(DUMMY_DATE_2, cursor.getString(cursor.getColumnIndex(DbHandler.BILLS_BILL_DATE)));
        assertEquals(DUMMY_COMPANY_1, cursor.getString(cursor.getColumnIndex(DbHandler.BILLS_COMPANY)));
        assertEquals(DUMMY_ADDRESS_2, cursor.getString(cursor.getColumnIndex(DbHandler.BILLS_ADDRESS)));

        cursor.close();
        cursor = dbHandler.getAllProducts();
        cursor.moveToLast();

        assertEquals(4, cursor.getInt(cursor.getColumnIndex(DbHandler.PRODUCTS_BILL_ID)));
        assertEquals(DUMMY_NAME_1, cursor.getString(cursor.getColumnIndex(DbHandler.PRODUCTS_PRODUCT_NAME)));
        assertEquals(DUMMY_CATEGORY_2, cursor.getString(cursor.getColumnIndex(DbHandler.PRODUCTS_CATEGORY)));
        assertEquals(DUMMY_AMOUNT_2, cursor.getDouble(cursor.getColumnIndex(DbHandler.PRODUCTS_AMOUNT)), DELTA);
        assertEquals(DUMMY_PRICE_1, cursor.getInt(cursor.getColumnIndex(DbHandler.PRODUCTS_PRICE)) / 100.0, DELTA);

        cursor.close();
    }

    @Test
    public void InvalidBillInUpdateBillInDb() throws Exception {
        Bill dummyBill = new Bill(DUMMY_COMPANY_1, DUMMY_COMPANY_1, DUMMY_ADDRESS_1);
        exception.expect(RuntimeException.class);

        billsAdapter.updateBillInDB(dummyBill);
    }

    @Test
    public void updateBillInDb() throws Exception {
        Bill bill = billsAdapter.getBillById(1);
        bill.removeProductAtIndex(1);
        Cursor productsCursor = dbHandler.getAllProducts();
        int productsCursorCount = productsCursor.getCount();
        productsCursor.close();

        billsAdapter.updateBillInDB(bill);
        double sum = billsAdapter.getSum(1, 2, 2017);
        productsCursor = dbHandler.getAllProducts();
        assertEquals(DUMMY_AMOUNT_1 * DUMMY_PRICE_1, sum, DELTA);
        assertEquals(productsCursorCount - 1, productsCursor.getCount());

        productsCursor.close();
    }

    @Test
    public void getBillById() throws Exception {
        Bill bill = billsAdapter.getBillById(1);

        assertEquals(1, bill.getDbId());
        assertEquals(DUMMY_DATE_1, bill.getDate());
        assertEquals(DUMMY_ADDRESS_1, bill.getAddress());
        assertEquals(DUMMY_COMPANY_1, bill.getCompany());

        assertEquals(2, bill.getProductsSize());
        assertEquals(DUMMY_NAME_1, bill.getProductAtIndex(0).name);
        assertEquals(DUMMY_CATEGORY_1, bill.getProductAtIndex(0).category);
        assertEquals(DUMMY_AMOUNT_1, bill.getProductAtIndex(0).amount, DELTA);
        assertEquals(DUMMY_PRICE_1, bill.getProductAtIndex(0).price, DELTA);
        assertEquals(DUMMY_NAME_2, bill.getProductAtIndex(1).name);
        assertEquals(DUMMY_CATEGORY_2, bill.getProductAtIndex(1).category);
        assertEquals(DUMMY_AMOUNT_2, bill.getProductAtIndex(1).amount, DELTA);
        assertEquals(DUMMY_PRICE_2, bill.getProductAtIndex(1).price, DELTA);
    }

    @Test
    public void getBillsWithDate() throws Exception {
        Vector <Bill> bills = billsAdapter.getBillsWithDate(3, 2, 2017);

        assertEquals(2, bills.size());
        assertEquals(1, bills.get(0).getProductsSize());
        assertEquals(1, bills.get(1).getProductsSize());
        assertEquals(2, bills.get(0).getDbId());
        assertEquals(3, bills.get(1).getDbId());
        assertEquals(DUMMY_NAME_2, bills.get(0).getProductAtIndex(0).name);
        assertEquals(DUMMY_CATEGORY_2, bills.get(0).getProductAtIndex(0).category);
        assertEquals(DUMMY_AMOUNT_2, bills.get(0).getProductAtIndex(0).amount, DELTA);
        assertEquals(DUMMY_PRICE_2, bills.get(0).getProductAtIndex(0).price, DELTA);
        assertEquals(DUMMY_NAME_1, bills.get(1).getProductAtIndex(0).name);
        assertEquals(DUMMY_CATEGORY_1, bills.get(1).getProductAtIndex(0).category);
        assertEquals(DUMMY_AMOUNT_1, bills.get(1).getProductAtIndex(0).amount, DELTA);
        assertEquals(DUMMY_PRICE_1, bills.get(1).getProductAtIndex(0).price, DELTA);
    }

    @Test
    public void getCategorySumFromTo() throws Exception {
        double sum = billsAdapter.getCategorySum(DUMMY_CATEGORY_1, 1, 2, 2017, 3, 2, 2017);

        assertEquals(DUMMY_AMOUNT_1 * DUMMY_PRICE_1 + DUMMY_AMOUNT_1 * DUMMY_PRICE_1, sum, DELTA);
        sum = billsAdapter.getCategorySum(DUMMY_CATEGORY_1, 3, 4, 2015, 2, 6, 2018);
        assertEquals(DUMMY_AMOUNT_1 * DUMMY_PRICE_1 + DUMMY_AMOUNT_1 * DUMMY_PRICE_1, sum, DELTA);
        sum = billsAdapter.getCategorySum(DUMMY_CATEGORY_1, 1, 2, 2017, 1, 2, 2017);
        assertEquals(DUMMY_AMOUNT_1 * DUMMY_PRICE_1, sum, DELTA);
        sum = billsAdapter.getCategorySum(DUMMY_CATEGORY_2, 1, 2, 2017, 1, 2, 2017);
        assertEquals(DUMMY_AMOUNT_2 * DUMMY_PRICE_2, sum, DELTA);
        sum = billsAdapter.getCategorySum(DUMMY_CATEGORY_1, 3, 2, 2017, 21, 2, 2017);
        assertEquals(DUMMY_AMOUNT_1 * DUMMY_PRICE_1, sum, DELTA);
        sum = billsAdapter.getCategorySum(DUMMY_CATEGORY_1, 3, 2, 2018, 10, 2, 2013);
        assertEquals(0.0, sum, DELTA);
        sum = billsAdapter.getCategorySum(DUMMY_CATEGORY_1, 1, 1, 2017, 33, 1, 2017);
        assertEquals(0.0, sum, DELTA);
    }

    @Test
    public void getCategorySumDay() throws Exception {
        double sum = billsAdapter.getCategorySum(DUMMY_CATEGORY_1, 1, 2, 2017);

        assertEquals(DUMMY_AMOUNT_1 * DUMMY_PRICE_1, sum, DELTA);
        sum = billsAdapter.getCategorySum(DUMMY_CATEGORY_2, 3, 2, 2017);
        assertEquals(DUMMY_AMOUNT_2 * DUMMY_PRICE_2, sum, DELTA);
        sum = billsAdapter.getCategorySum(DUMMY_CATEGORY_2, 4, 2, 2017);
        assertEquals(0.0, sum, DELTA);
        sum = billsAdapter.getCategorySum("dupa", 3, 2, 2017);
        assertEquals(0.0, sum, DELTA);
        sum = billsAdapter.getCategorySum("dupa", 2, 2, 2017);
        assertEquals(0.0, sum, DELTA);
    }

    @Test
    public void getCategorySumMonth() throws Exception {
        double sum = billsAdapter.getCategorySum(DUMMY_CATEGORY_1, 2, 2017);

        assertEquals(DUMMY_AMOUNT_1 * DUMMY_PRICE_1 + DUMMY_AMOUNT_1 * DUMMY_PRICE_1, sum, DELTA);
        sum = billsAdapter.getCategorySum(DUMMY_CATEGORY_2, 2, 2017);
        assertEquals(DUMMY_AMOUNT_2 * DUMMY_PRICE_2 + DUMMY_AMOUNT_2 * DUMMY_PRICE_2, sum, DELTA);
        sum = billsAdapter.getCategorySum(DUMMY_CATEGORY_1, 4, 2017);
        assertEquals(0.0, sum, DELTA);
        sum = billsAdapter.getCategorySum("dupa", 2, 2017);
        assertEquals(0.0, sum, DELTA);
    }

    @Test
    public void getSumFromTo() throws Exception {
        double sum = billsAdapter.getSum(1, 2, 2017, 3, 2, 2017);

        assertEquals(DUMMY_AMOUNT_1 * DUMMY_PRICE_1 + DUMMY_AMOUNT_2 * DUMMY_PRICE_2
                + DUMMY_AMOUNT_1 * DUMMY_PRICE_1 + DUMMY_AMOUNT_2 * DUMMY_PRICE_2, sum, DELTA);
        sum = billsAdapter.getSum(3, 4, 2015, 2, 6, 2018);
        assertEquals(DUMMY_AMOUNT_1 * DUMMY_PRICE_1 + DUMMY_AMOUNT_2 * DUMMY_PRICE_2
                + DUMMY_AMOUNT_1 * DUMMY_PRICE_1 + DUMMY_AMOUNT_2 * DUMMY_PRICE_2, sum, DELTA);
        sum = billsAdapter.getSum(1, 2, 2017, 1, 2, 2017);
        assertEquals(DUMMY_AMOUNT_1 * DUMMY_PRICE_1 + DUMMY_AMOUNT_2 * DUMMY_PRICE_2, sum, DELTA);
        sum = billsAdapter.getSum(3, 2, 2017, 21, 2, 2017);
        assertEquals(DUMMY_AMOUNT_1 * DUMMY_PRICE_1 + DUMMY_AMOUNT_2 * DUMMY_PRICE_2, sum, DELTA);
        sum = billsAdapter.getSum(3, 2, 2018, 10, 2, 2013);
        assertEquals(0.0, sum, DELTA);
        sum = billsAdapter.getSum(1, 1, 2017, 33, 1, 2017);
        assertEquals(0.0, sum, DELTA);
    }

    @Test
    public void getSumDay() throws Exception {
        double sum = billsAdapter.getSum(3, 2, 2017);

        assertEquals(DUMMY_AMOUNT_1 * DUMMY_PRICE_1 + DUMMY_AMOUNT_2 * DUMMY_PRICE_2, sum, DELTA);
        sum = billsAdapter.getSum(1, 2, 2017);
        assertEquals(DUMMY_AMOUNT_1 * DUMMY_PRICE_1 + DUMMY_AMOUNT_2 * DUMMY_PRICE_2, sum, DELTA);
        sum = billsAdapter.getSum(3, 2, 2016);
        assertEquals(0.0, sum, DELTA);
        sum = billsAdapter.getSum(3, 4, 2017);
        assertEquals(0.0, sum, DELTA);
    }

    @Test
    public void getSumMonth() throws Exception {
        double sum = billsAdapter.getSum(2, 2017);

        assertEquals(DUMMY_AMOUNT_1 * DUMMY_PRICE_1 + DUMMY_AMOUNT_2 * DUMMY_PRICE_2
                + DUMMY_AMOUNT_1 * DUMMY_PRICE_1 + DUMMY_AMOUNT_2 * DUMMY_PRICE_2, sum, DELTA);
        sum = billsAdapter.getSum(7, 2017);
        assertEquals(0.0, sum, DELTA);
        sum = billsAdapter.getSum(2, 2011);
        assertEquals(0.0, sum, DELTA);
    }

    @Test
    public void getUsedCategories() throws Exception {
        Vector<String> categories = billsAdapter.getUsedCategories();

        assertEquals(2, categories.size());
        assertEquals(DUMMY_CATEGORY_2, categories.get(0));
        assertEquals(DUMMY_CATEGORY_1, categories.get(1));
    }

    @Test
    public void deleteBillById() throws Exception {
        Cursor billCursor = dbHandler.getAllBills();
        int billCursorCount = billCursor.getCount();
        billCursor.close();
        Cursor productsCursor = dbHandler.getAllProducts();
        int productsCursorCount = productsCursor.getCount();
        productsCursor.close();

        billsAdapter.deleteBillById(1);
        billCursor = dbHandler.getAllBills();
        productsCursor = dbHandler.getAllProducts();

        assertEquals(billCursorCount - 1, billCursor.getCount());
        assertEquals(productsCursorCount - 2, productsCursor.getCount());
        billCursor.moveToFirst();
        assertEquals(2, billCursor.getInt(billCursor.getColumnIndex(DbHandler.BILLS_ID)));

        billCursor.close();
        productsCursor.close();
    }

    @Test
    public void deleteProducts() throws Exception {
        Cursor productsCursor = dbHandler.getAllProducts();
        int productsCursorCount = productsCursor.getCount();
        productsCursor.close();

        dbHandler.deleteProducts(2);
        productsCursor = dbHandler.getAllProducts();

        assertEquals(productsCursorCount - 1, productsCursor.getCount());
        productsCursor.moveToFirst();
        assertEquals(1,
                productsCursor.getInt(productsCursor.getColumnIndex(DbHandler.PRODUCTS_BILL_ID)));
        productsCursor.moveToNext();
        assertEquals(2,
                productsCursor.getInt(productsCursor.getColumnIndex(DbHandler.PRODUCTS_BILL_ID)));

        productsCursor.close();
    }

    @Test
    public void deleteProductsByName() throws Exception {
        Cursor productsCursor = dbHandler.getAllProducts();
        int productsCursorCount = productsCursor.getCount();
        productsCursor.close();

        billsAdapter.deleteProductsByName(1, DUMMY_NAME_1);
        productsCursor = dbHandler.getAllProducts();

        assertEquals(productsCursorCount - 1, productsCursor.getCount());
        productsCursor.moveToFirst();
        assertEquals(DUMMY_NAME_2,
                productsCursor.getString(productsCursor.getColumnIndex(DbHandler.PRODUCTS_PRODUCT_NAME)));

        productsCursor.close();
    }

    @Test
    public void deleteProductsByBillId() throws Exception {
        Cursor productsCursor = dbHandler.getAllProducts();
        int productsCursorCount = productsCursor.getCount();
        productsCursor.close();

        billsAdapter.deleteProductsByBillId(1);
        productsCursor = dbHandler.getAllProducts();

        assertEquals(productsCursorCount - 2, productsCursor.getCount());
        assertEquals(0.0, billsAdapter.getSum(1, 2, 2017));
        double sum = billsAdapter.getSum(3, 2, 2017);
        assertEquals(DUMMY_AMOUNT_1 * DUMMY_PRICE_1 + DUMMY_AMOUNT_2 * DUMMY_PRICE_2, sum, DELTA);
    }

    @Test
    public void getDiscardedCategories() throws Exception {
        Vector<String> dcVector = billsAdapter.getDiscardedCategories();

        assertEquals(2, dcVector.size());
        assertEquals(DUMMY_DC_1, dcVector.elementAt(0));
        assertEquals(DUMMY_DC_2, dcVector.elementAt(1));
    }

    @Test
    public void deleteDiscardedCategory() throws Exception {
        billsAdapter.deleteDiscardedCategory(DUMMY_DC_2);
        Vector<String> dcVector = billsAdapter.getDiscardedCategories();

        assertEquals(1, dcVector.size());
        assertEquals(DUMMY_DC_1, dcVector.elementAt(0));
    }

    @Test
    public void addDiscardedCategory() throws Exception {
        billsAdapter.addDiscardedCategory("dania");
        Vector<String> dcVector = billsAdapter.getDiscardedCategories();

        assertEquals(3, dcVector.size());
        assertEquals(DUMMY_DC_1, dcVector.elementAt(0));
        assertEquals("dania", dcVector.elementAt(1));
        assertEquals(DUMMY_DC_2, dcVector.elementAt(2));
    }
}

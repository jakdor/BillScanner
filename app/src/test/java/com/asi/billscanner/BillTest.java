package com.asi.billscanner;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Vector;

import static org.junit.Assert.*;

public class BillTest {

    private static final double DELTA = 0.0001;

    private Bill bill;

    private final String DUMMY_COMPANY = "Biedra";
    private final String DUMMY_DATE = "2017-02-01";
    private final String DUMMY_ADDRESS = "12 Baker st. 23543 London";

    private final String DUMMY_NAME = "Ser";
    private final String DUMMY_CATEGORY = "Spożywcze 123";
    private final double DUMMY_AMOUNT = 3.1;
    private final double DUMMY_PRICE = 1.23;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        bill = new Bill(DUMMY_DATE, DUMMY_COMPANY, DUMMY_ADDRESS);
        bill.addNewProduct(DUMMY_NAME, DUMMY_CATEGORY, DUMMY_AMOUNT, DUMMY_PRICE);
    }

    @Test
    public void addNewProduct() throws Exception {
        int size = bill.getProductsSize();
        bill.addNewProduct(DUMMY_NAME, DUMMY_CATEGORY, DUMMY_AMOUNT, DUMMY_PRICE);

        assertEquals(size + 1, bill.getProductsSize());
    }

    @Test
    public void tryAccessInvalidIndex() throws Exception {
        exception.expect(RuntimeException.class);

        bill.removeProductAtIndex(-1);
        bill.removeProductAtIndex(bill.getProductsSize() + 1);
    }

    @Test
    public void removeProductAtIndex() throws Exception {
        int size = bill.getProductsSize();
        bill.removeProductAtIndex(0);

        assertEquals(size - 1, bill.getProductsSize());
    }

    @Test
    public void getProductAtIndex() throws Exception {
        Bill.Product testProduct = new Bill.Product(DUMMY_NAME, DUMMY_CATEGORY, DUMMY_AMOUNT, DUMMY_PRICE);
        Bill.Product actualProduct = bill.getProductAtIndex(0);

        assertEquals(testProduct.name, actualProduct.name);
        assertEquals(testProduct.category, actualProduct.category);
        assertEquals(testProduct.amount, actualProduct.amount, DELTA);
        assertEquals(testProduct.price, actualProduct.price, DELTA);
    }

    @Test
    public void getProductList() throws Exception {
        List <Bill.Product> testList = new Vector<>();
        testList.add(new Bill.Product(DUMMY_NAME, DUMMY_CATEGORY, DUMMY_AMOUNT, DUMMY_PRICE));
        List <Bill.Product> actualList;
        actualList = bill.getProductList();

        assertEquals(testList.get(0).name, actualList.get(0).name);
        assertEquals(testList.get(0).category, actualList.get(0).category);
        assertEquals(testList.get(0).amount, actualList.get(0).amount, DELTA);
        assertEquals(testList.get(0).price, actualList.get(0).price, DELTA);
    }

    @Test
    public void setProductNameAtIndex() throws Exception {
        String testStr = "Test Name";
        bill.setProductsNameAtIndex(0, testStr);

        assertEquals(bill.getProductNameAtIndex(0), testStr);
    }

    @Test
    public void setProductCategoryAtIndex() throws Exception {
        String testStr = "Test Category";
        bill.setProductsCategoryAtIndex(0, testStr);

        assertEquals(bill.getProductCategoryAtIndex(0), testStr);
    }

    @Test
    public void setProductAmountAtIndex() throws Exception {
        int testVal = 5;
        bill.setProductsAmountAtIndex(0, testVal);

        assertEquals(bill.getProductAmountAtIndex(0), testVal, DELTA);
    }

    @Test
    public void setProductPriceAtIndex() throws Exception {
        double testVal = 9.87;
        bill.setProductsPrice(0, testVal);

        assertEquals(bill.getProductPriceAtIndex(0), testVal, DELTA);
    }

    @Test
    public void getProductNameAtIndex() throws Exception {
        String actualName = bill.getProductNameAtIndex(0);

        assertEquals(DUMMY_NAME, actualName);
    }

    @Test
    public void getProductCategoryAtIndex() throws Exception {
        String actualCategory = bill.getProductCategoryAtIndex(0);

        assertEquals(DUMMY_CATEGORY, actualCategory);
    }

    @Test
    public void getProductAmountAtIndex() throws Exception {
        double actualAmount = bill.getProductAmountAtIndex(0);

        assertEquals(DUMMY_AMOUNT, actualAmount, DELTA);
    }

    @Test
    public void getProductPriceAtIndex() throws Exception {
        double actualPrice = bill.getProductPriceAtIndex(0);

        assertEquals(DUMMY_PRICE, actualPrice, DELTA);
    }

    @Test
    public void getBillSum(){
        bill.addNewProduct(DUMMY_NAME, DUMMY_CATEGORY, DUMMY_AMOUNT, DUMMY_PRICE);

        double actualPrice = bill.getBillSum();

        assertEquals(DUMMY_PRICE + DUMMY_PRICE, actualPrice, DELTA);
    }

    @Test
    public void getCategorySum(){
        bill.addNewProduct(DUMMY_NAME, "dvdvdvasfv dfsbvc345t5rhb", DUMMY_AMOUNT, DUMMY_PRICE);
        bill.addNewProduct(DUMMY_NAME, DUMMY_CATEGORY, DUMMY_AMOUNT, DUMMY_PRICE);

        double actualPrice = bill.getCategorySum(DUMMY_CATEGORY);

        assertEquals(DUMMY_PRICE + DUMMY_PRICE, actualPrice, DELTA);
    }

    @Test
    public void getDate() throws Exception {
        String actualDate = bill.getDate();

        assertEquals(DUMMY_DATE, actualDate);
    }

    @Test
    public void setDate() throws Exception {
        String testStr = "09.12.2011";
        bill.setDate(testStr);

        assertEquals(testStr, bill.getDate());
    }

    @Test
    public void getCompany() throws Exception {
        String actualCompany = bill.getCompany();

        assertEquals(DUMMY_COMPANY, actualCompany);
    }

    @Test
    public void setCompany() throws Exception {
        String testStr = "Kaufland";
        bill.setCompany(testStr);

        assertEquals(testStr, bill.getCompany());
    }

    @Test
    public void getAddress() throws Exception {
        String actualAddress = bill.getAddress();

        assertEquals(DUMMY_ADDRESS, actualAddress);
    }

    @Test
    public void setAddress() throws Exception {
        String testStr = "Długa 212, 43400 Warszawa";
        bill.setAddress(testStr);

        assertEquals(testStr, bill.getAddress());
    }

    @Test
    public void getProductsSize() throws Exception {
        assertTrue(bill.getProductsSize() > 0);
    }

    @Test
    public void setDbId() throws Exception {
        bill.setDbId(5);
        assertEquals(5, bill.getDbId());
    }

    @Test
    public void getDbID() throws Exception {
        assertTrue(bill.getDbId() >= -1);
    }
}
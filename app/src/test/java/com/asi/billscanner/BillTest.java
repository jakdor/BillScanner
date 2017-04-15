package com.asi.billscanner;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Vector;

import static org.junit.Assert.*;

public class BillTest {

    private Bill bill;

    private String dummyCompany = "Biedra";
    private String dummyDate = "1.2.2017";
    private String dummyAddress = "12 Baker st. 23543 London";

    private String dummyName = "Ser";
    private String dummyCategory = "Spożywcze 123";
    private int dummyAmount = 3;
    private double dummyPrice = 1.23;

    @Rule
    public ExpectedException exception =
            ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        bill = new Bill(dummyDate, dummyCompany, dummyAddress);
        bill.addNewProduct(dummyName, dummyCategory, dummyAmount, dummyPrice);
    }

    @Test
    public void addNewProduct() throws Exception {
        int size = bill.getProductsSize();
        bill.addNewProduct(dummyName, dummyCategory, dummyAmount, dummyPrice);

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
        Bill.Product testProduct = new Bill.Product(dummyName, dummyCategory, dummyAmount, dummyPrice);
        Bill.Product actualProduct = bill.getProductAtIndex(0);

        assertEquals(testProduct.name, actualProduct.name);
        assertEquals(testProduct.category, actualProduct.category);
        assertEquals(testProduct.amount, actualProduct.amount);
        assertEquals(testProduct.price, actualProduct.price, 0.0001);
    }

    @Test
    public void getProductList() throws Exception {
        List <Bill.Product> testList = new Vector<>();
        testList.add(new Bill.Product(dummyName, dummyCategory, dummyAmount, dummyPrice));
        List <Bill.Product> actualList;
        actualList = bill.getProductList();

        assertEquals(testList.get(0).name, actualList.get(0).name);
        assertEquals(testList.get(0).category, actualList.get(0).category);
        assertEquals(testList.get(0).amount, actualList.get(0).amount);
        assertEquals(testList.get(0).price, actualList.get(0).price, 0.0001);
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

        assertEquals(bill.getProductAmountAtIndex(0), testVal);
    }

    @Test
    public void setProductPriceAtIndex() throws Exception {
        double testVal = 9.87;
        bill.setProductsPrice(0, testVal);

        assertEquals(bill.getProductPriceAtIndex(0), testVal, 0.0001);
    }

    @Test
    public void getProductNameAtIndex() throws Exception {
        String actualName = bill.getProductNameAtIndex(0);

        assertEquals(dummyName, actualName);
    }

    @Test
    public void getProductCategoryAtIndex() throws Exception {
        String actualCategory = bill.getProductCategoryAtIndex(0);

        assertEquals(dummyCategory, actualCategory);
    }

    @Test
    public void getProductAmountAtIndex() throws Exception {
        int actualAmount = bill.getProductAmountAtIndex(0);

        assertEquals(dummyAmount, actualAmount);
    }

    @Test
    public void getProductPriceAtIndex() throws Exception {
        double actualPrice = bill.getProductPriceAtIndex(0);

        assertEquals(dummyPrice, actualPrice, 0.0001);
    }

    @Test
    public void getDate() throws Exception {
        String actualDate = bill.getDate();

        assertEquals(dummyDate, actualDate);
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

        assertEquals(dummyCompany, actualCompany);
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

        assertEquals(dummyAddress, actualAddress);
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
}
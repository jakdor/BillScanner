package com.asi.billscanner;

import java.util.List;
import java.util.Vector;

/**
 * Wytyczne:
 * - klasa/struktura pośrednicząca w wymianie danych dot. pojedyńczego rachunku
 */

class Bill {

    static class Product{ //subclass used only as a getter (optimization)
        Product(String name, String category, int amount, double price){
            this.name = name;
            this.category = category;
            this.amount = amount;
            this.price = price;
        }

        String name;
        String category;
        int amount;
        double price;
    }

    private String date = "";
    private String company = "";
    private String address = "";

    //products
    private List <String> productsName;
    private List <String> productsCategory;
    private List <Integer> productsAmount;
    private List <Double> productsPrice;
    private int productsSize = 0;

    Bill(){
        productsName = new Vector<>();
        productsCategory = new Vector<>();
        productsAmount = new Vector<>();
        productsPrice = new Vector<>();
    }

    Bill(String date, String company, String address){
        this();
        this.date = date;
        this.company = company;
        this.address = address;
    }

    void addNewProduct(String name, String category, int amount, double price){
        productsName.add(name);
        productsCategory.add(category);
        productsAmount.add(amount);
        productsPrice.add(price);
        ++productsSize;
    }

    private void checkIndexValidity(int index){
        if(index > productsSize || index < 0){
            throw new RuntimeException("Bill removeProductAtIndex(int index): Invalid index, memory violation!");
        }
    }

    void removeProductAtIndex(int index){
        checkIndexValidity(index);
        productsName.remove(index);
        productsCategory.remove(index);
        productsAmount.remove(index);
        productsPrice.remove(index);
        --productsSize;
    }

    Product getProductAtIndex(int index){
        checkIndexValidity(index);
        return new Product(productsName.get(index),
                productsCategory.get(index),
                productsAmount.get(index),
                productsPrice.get(index));
    }

    List <Product> getProductList(){
        List <Product> productVector = new Vector<>();

        for (int i = 0; i < productsSize; ++i){
            productVector.add(getProductAtIndex(i));
        }

        return productVector;
    }

    String getProductNameAtIndex(int index){
        checkIndexValidity(index);
        return productsName.get(index);
    }

    String getProductCategoryAtIndex(int index){
        checkIndexValidity(index);
        return productsCategory.get(index);
    }

    int getProductAmountAtIndex(int index){
        checkIndexValidity(index);
        return productsAmount.get(index);
    }

    double getProductPriceAtIndex(int index){
        checkIndexValidity(index);
        return productsPrice.get(index);
    }

    String getDate() {
        return date;
    }

    void setDate(String date) {
        this.date = date;
    }

    String getCompany() {
        return company;
    }

    void setCompany(String company) {
        this.company = company;
    }

    String getAddress() {
        return address;
    }

    void setAddress(String address) {
        this.address = address;
    }

    int getProductsSize() {
        return productsSize;
    }
}

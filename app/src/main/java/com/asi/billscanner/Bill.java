package com.asi.billscanner;

import java.util.List;
import java.util.Vector;

/**
 * Wytyczne:
 * - klasa/struktura pośrednicząca w wymianie danych dot. pojedyńczego rachunku
 */

class Bill {

    /**
     * subclass used only as a getter (optimization)
     */
    static class Product{
        Product(String name, String category, double amount, double price){
            this.name = name;
            this.category = category;
            this.amount = amount;
            this.price = price;
        }

        String name;
        String category;
        double amount;
        double price;
    }

    private String date = "";
    private String company = "";
    private String address = "";

    //products
    private List <String> productsName;
    private List <String> productsCategory;
    private List <Double> productsAmount;
    private List <Double> productsPrice;
    private int productsSize = 0;

    private int dbId = -1;

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

    Bill(String date, String company, String address, int dbId){
        this(date, company, address);
        this.dbId = dbId;
    }

    void addNewProduct(String name, String category, double amount, double price){
        productsName.add(name);
        productsCategory.add(category);
        productsAmount.add(amount);
        productsPrice.add(price);
        ++productsSize;
    }

    private void checkIndexValidity(int index){
        if(index > productsSize || index < 0){
            throw new RuntimeException("Bill: Invalid product index, memory violation!");
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

    void setProductsNameAtIndex(int index, String value){
        checkIndexValidity(index);
        productsName.set(index, value);
    }

    void setProductsCategoryAtIndex(int index, String value){
        checkIndexValidity(index);
        productsCategory.set(index, value);
    }

    void setProductsAmountAtIndex(int index, double value){
        checkIndexValidity(index);
        productsAmount.set(index, value);
    }

    void setProductsPrice (int index, double value){
        checkIndexValidity(index);
        productsPrice.set(index, value);
    }

    String getProductNameAtIndex(int index){
        checkIndexValidity(index);
        return productsName.get(index);
    }

    String getProductCategoryAtIndex(int index){
        checkIndexValidity(index);
        return productsCategory.get(index);
    }

    double getProductAmountAtIndex(int index){
        checkIndexValidity(index);
        return productsAmount.get(index);
    }

    double getProductPriceAtIndex(int index){
        checkIndexValidity(index);
        return productsPrice.get(index);
    }

    double getBillSum(){
        double sum = 0.0;
        for(int i = 0; i < productsSize; ++i){
            sum += productsAmount.get(i) * productsPrice.get(i);
        }
        return sum;
    }

    double getCategorySum(String category){
        double sum = 0.0;
        for (int i = 0; i < productsSize; ++i){
            if(productsCategory.get(i).equals(category)){
                sum += productsPrice.get(i);
            }
        }
        return sum;
    }

    int getDateYear(){
        return Integer.parseInt(date.substring(0, 4));
    }

    int getDateMonth(){
        return Integer.parseInt(date.substring(5, 7));
    }

    int getDateDay(){
        return Integer.parseInt(date.substring(8, 10));
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

    /**
     * will return corresponding id from db bills table , if not loaded from db: -1
     */
    int getDbId() {
        return dbId;
    }

    void setDbId(int dbId) {
        this.dbId = dbId;
    }
}

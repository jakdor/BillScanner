package com.asi.billscanner.dbUtilities;

public class ProductsModel {

    private int id;
    private int billId;
    private String category;
    private String productName;
    private double amount;
    private int price;

    ProductsModel(int id,
                  int billId,
                  String category,
                  String productName,
                  double amount,
                  int price){
        this.id = id;
        this.billId = billId;
        this.category = category;
        this.productName = productName;
        this.amount = amount;
        this.price = price;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}

package com.asi.billscanner.dbUtilities;

import java.sql.Date;
import java.sql.Timestamp;

public class BillsModel {

    private int id;
    private Timestamp addTime;
    private Date billDate;
    private String company;
    private String address;

    BillsModel(int id,
               Timestamp addTime,
               Date billDate,
               String company,
               String address){
        this.id = id;
        this.addTime = addTime;
        this.billDate = billDate;
        this.company = company;
        this.address = address;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

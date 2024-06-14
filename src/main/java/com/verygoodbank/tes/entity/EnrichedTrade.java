package com.verygoodbank.tes.entity;

public class EnrichedTrade {
    private String date;
    private String productName;
    private String currency;
    private double price;

    public EnrichedTrade(String date, String productName, String currency, double price) {
        this.date = date;
        this.productName = productName;
        this.currency = currency;
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
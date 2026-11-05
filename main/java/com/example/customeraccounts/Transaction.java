package com.example.customeraccounts;

public class Transaction {
    private int id;
    private int customerId;
    private String name;
    private double amount;
    private String date;
    private String type;

    public Transaction() {
    }

    public Transaction(int customerId, String name, double amount, String date, String type) {
        this.customerId = customerId;
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.type = type;
    }

    public Transaction(int id, int customerId, String name, double amount, String date, String type) {
        this.id = id;
        this.customerId = customerId;
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

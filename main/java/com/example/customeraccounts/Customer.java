package com.example.customeraccounts;
public class Customer {
    private int id;
    private String name;
    private String phone;
    private double balance;
    private boolean isZeroBalance;
    public Customer() {
    }
    public Customer(int id, String name, String phone, double balance, boolean isZeroBalance) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.balance = balance;
        this.isZeroBalance = isZeroBalance;
    }
    public Customer(String name, String phone, double balance, boolean isZeroBalance) {
        this.name = name;
        this.phone = phone;
        this.balance = balance;
        this.isZeroBalance = isZeroBalance;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isZeroBalance() {
        return isZeroBalance;
    }

    public void setZeroBalance(boolean zeroBalance) {
        isZeroBalance = zeroBalance;
    }
}

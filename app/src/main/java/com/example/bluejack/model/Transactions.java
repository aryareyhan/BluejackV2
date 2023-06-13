package com.example.bluejack.model;

public class Transactions {
    int ID;
    String date, name, price, quantity;

    public Transactions(int ID, String date, String name, String price, String quantity) {
        this.ID = ID;
        this.date = date;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getID() {
        return ID;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }
}

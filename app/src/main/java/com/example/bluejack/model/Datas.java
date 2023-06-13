package com.example.bluejack.model;

public class Datas {
    private String name, manufacturer, price, image, description;

    public Datas(String name, String manufacturer, String price, String image, String description) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.price = price;
        this.image = image;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }
}

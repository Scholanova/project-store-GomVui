package com.scholanova.projectstore.models;

public class Stock {
    private Integer id;
    private String name;
    private String type;
    private int value;
    private int storeId;

    public Stock() {
    }

    public Stock(Integer id, String name, String type, int value, int storeId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.value = value;
        this.storeId = storeId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public int getStoreId() {
        return storeId;
    }

}

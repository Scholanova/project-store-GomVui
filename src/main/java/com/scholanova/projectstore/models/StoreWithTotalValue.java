package com.scholanova.projectstore.models;

public class StoreWithTotalValue {

    private Integer id;
    private String name;

    private Integer stockTotalValue;

    public StoreWithTotalValue() {
    }

    public StoreWithTotalValue(Integer id, String name, Integer stockTotalValue) {
        this.id = id;
        this.name = name;
        this.stockTotalValue = stockTotalValue;
    }

    public Integer getStockTotalValue() {
        return stockTotalValue;
    }

    public void setStockTotalValue(Integer stockTotalValue) {
        this.stockTotalValue = stockTotalValue;
    }

    public Integer getId() {
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
}

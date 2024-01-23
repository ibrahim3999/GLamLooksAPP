package com.example.glamlooksapp.utils;

import java.util.ArrayList;

public class ProductManager {
    private static ProductManager instance;
    private ArrayList<Product> productList;

    private ProductManager() {
        // Initialize productList or perform any setup
        productList = new ArrayList<>();
    }

    public static synchronized ProductManager getInstance() {
        if (instance == null) {
            instance = new ProductManager();
        }
        return instance;
    }

    public ArrayList<Product> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
    }

}

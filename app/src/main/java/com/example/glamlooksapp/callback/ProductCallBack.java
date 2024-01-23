package com.example.glamlooksapp.callback;

import com.example.glamlooksapp.utils.Product;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public interface ProductCallBack {
    void onAddIProductsComplete(Task<Void> task);
    void onFetchProductsComplete(ArrayList<Product> products);
}

package com.example.glamlooksapp.fragments.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glamlooksapp.R;
import com.example.glamlooksapp.callback.ProductCallBack;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Product;
import com.example.glamlooksapp.Adapter.ProductAdapter;
import com.example.glamlooksapp.utils.ProductManager;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class ProductsFragment extends Fragment {
    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private ArrayList<Product> productList;
    Database database;
  //  int check;
    public ProductsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        database = new Database();
        initViews();
        initRecyclerView();
        intiVars();
        return view;
    }

    private void intiVars() {
        database.setProductCallBack(new ProductCallBack() {
            @Override
            public void onAddIProductsComplete(Task<Void> task) {}
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onFetchProductsComplete(ArrayList<Product> products) {
                productList.clear();
                productList.addAll(products);
                productAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initViews() {
        ProductManager productManager = ProductManager.getInstance();
        productAdapter = new ProductAdapter(getContext(), productManager.getProductList());
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void initRecyclerView() {
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), productList);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewProducts.setAdapter(productAdapter);
     //   if(check != 0) {
            database.fetchProducts();
     //   }
      //  check++;

    }
}

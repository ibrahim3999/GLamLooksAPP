package com.example.glamlooksapp.fragments.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glamlooksapp.R;
import com.example.glamlooksapp.callback.ProductCallBack;
import com.example.glamlooksapp.callback.UserFetchCallback;
import com.example.glamlooksapp.utils.Database;
import com.example.glamlooksapp.utils.Product;
import com.example.glamlooksapp.Adapter.ProductAdapter;
import com.example.glamlooksapp.utils.ProductManager;
import com.example.glamlooksapp.utils.User;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class ProductsFragment extends Fragment {

    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private ArrayList<Product> productList;

    Database database;

    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        database = new Database();

        initRecyclerView();
        intiVars();
        return view;
    }

    private void intiVars() {

        database.setProductCallBack(new ProductCallBack() {
            @Override
            public void onAddIProductsComplete(Task<Void> task) {

            }

            @Override
            public void onFetchProductsComplete(ArrayList<Product> products) {
                productList.clear();
                productList.addAll(products);
                productAdapter.notifyDataSetChanged();
            }
        });

    }

    private void initVars() {
        ProductManager productManager = ProductManager.getInstance();
        productAdapter = new ProductAdapter(getContext(), productManager.getProductList());
        recyclerViewProducts.setAdapter(productAdapter);
    }


    private void initRecyclerView() {
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), productList);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewProducts.setAdapter(productAdapter);

        // Fetch products from the database and update the list
        database.fetchProducts();


    }




}

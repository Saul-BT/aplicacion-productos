package com.example.productmanager;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.productmanager.adapters.AdapterProducts;
import com.example.productmanager.firebase.Product;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment {

    private View fragmentView;
    private RecyclerView recyclerView;
    private AdapterProducts productsAdapter;

    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        setHasOptionsMenu(true);
        setUpComponents(view);

        return view;
    }

    private void setUpComponents(View view) {
        fragmentView = view;

        Toolbar barra = fragmentView.findViewById(R.id.toolbar);
        barra.inflateMenu(R.menu.menu_user);
        barra.setTitle(R.string.products_toolbar_title);

        List<Product> products = new ArrayList<Product>() {{
            add(new Product("Leche", null, 3.5f));
            add(new Product("Huevo", null, 1f));
            add(new Product("Jamón", null, 50.99f));
        }};

        recyclerView = view.findViewById(R.id.recycler_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL));

        productsAdapter = new AdapterProducts(this, R.layout.product_view, products);

        recyclerView.setAdapter(productsAdapter);
    }
}

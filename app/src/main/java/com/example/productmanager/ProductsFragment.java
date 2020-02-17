package com.example.productmanager;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.productmanager.adapters.AdapterProducts;
import com.example.productmanager.model.Product;
import com.example.productmanager.model.UserType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment {

    private FloatingActionButton addProductFab;

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

    @SuppressLint("RestrictedApi")
    private void setUpComponents(View view) {
        fragmentView = view;

        Toolbar barra = fragmentView.findViewById(R.id.toolbar);
        barra.inflateMenu(R.menu.menu_user);
        barra.setTitle(R.string.products_toolbar_title);

        addProductFab = fragmentView.findViewById(R.id.fab_add_product);

        if (MainActivity.currentUser.getType().canManageProducts) {
            addProductFab.setVisibility(View.VISIBLE);
            addProductFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view).navigate(R.id.go_to_add_product);
                }
            });
        }


        recyclerView = view.findViewById(R.id.recycler_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL));

        //productsAdapter = new AdapterProducts(this, R.layout.product_view, products);

        recyclerView.setAdapter(productsAdapter);
    }
}

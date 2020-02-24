package com.example.productmanager;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import com.example.productmanager.adapters.AdapterProducts;
import com.example.productmanager.model.FireManager;
import com.example.productmanager.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment {

    private FireManager fm = FireManager.getInstance();

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
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_products, container, false);

        setUpComponents(view);

        return view;
    }

    @SuppressLint("RestrictedApi")
    private void setUpComponents(final View view) {
        fragmentView = view;

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.show();
        //toolbar.inflateMenu(R.menu.menu_user);
        actionBar.setTitle(R.string.products_toolbar_title);

        addProductFab = fragmentView.findViewById(R.id.fab_add_product);

        if (MainActivity.currentUser.getType().canManageProducts) {
            addProductFab.setVisibility(View.VISIBLE);
            addProductFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view).navigate(R.id.go_to_set_product);
                }
            });
        }


        recyclerView = view.findViewById(R.id.recycler_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        productsAdapter = new AdapterProducts(getActivity(), R.layout.product_view, new ArrayList<Product>());
        recyclerView.setAdapter(productsAdapter);

        fm.dbProductsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    List<Product> products = new ArrayList<>();

                    for (QueryDocumentSnapshot document : result) {
                        products.add(document.toObject(Product.class));
                    }

                    //productsAdapter = new AdapterProducts(ProductsFragment.this, R.layout.product_view, products);
                    productsAdapter.setProducts(products);
                    productsAdapter.notifyDataSetChanged();
                }
            }
        });


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_user, menu);

        MenuItem accountItem = menu.findItem(R.id.mi_account);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productsAdapter.getFilter().filter(newText);
                return false;
            }
        });

        accountItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Navigation.findNavController(fragmentView).navigate(R.id.go_to_account);
                return true;
            }
        });
    }
}

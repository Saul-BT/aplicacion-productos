package com.example.productmanager;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.productmanager.adapters.AdapterProducts;
import com.example.productmanager.model.FireManager;
import com.example.productmanager.model.Product;
import com.example.productmanager.model.UserSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment {

    private ZxingOrient codeScanner;
    private FireManager fm = FireManager.getInstance();

    private FloatingActionButton addProductFab;

    private View fragmentView;
    private RecyclerView recyclerView;
    private AdapterProducts productsAdapter;
    Button bScanSearch;

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

    @SuppressLint({"RestrictedApi", "ResourceType"})
    private void setUpComponents(final View view) {
        fragmentView = view;

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.show();
        actionBar.setTitle(R.string.products_toolbar_title);

        codeScanner = new ZxingOrient(this);
        codeScanner.setIcon(R.drawable.ic_launcher_logo)
                .setToolbarColor(getString(R.color.colorAccent))
                .setInfoBoxColor(getString(R.color.colorAccent))
                .setInfo(getString(R.string.info_scanner));

        bScanSearch = fragmentView.findViewById(R.id.b_scan_search);
        addProductFab = fragmentView.findViewById(R.id.fab_add_product);

        if (UserSession.currentUser.getType().canManageProducts) {
            addProductFab.setVisibility(View.VISIBLE);
        }

        recyclerView = view.findViewById(R.id.recycler_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        productsAdapter = new AdapterProducts(getActivity(), R.layout.product_view, new ArrayList<Product>());
        recyclerView.setAdapter(productsAdapter);

        fm.dbProductsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documents) {
                List<Product> products = new ArrayList<>();

                for (QueryDocumentSnapshot document : documents) {
                    products.add(document.toObject(Product.class));
                }

                productsAdapter.setProducts(products);
            }
        });

        bScanSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.initiateScan();
            }
        });

        addProductFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.go_to_set_product);
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

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                bScanSearch.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case ZxingOrient.REQUEST_CODE: {
                ZxingOrientResult result = ZxingOrient.parseActivityResult(requestCode, resultCode, data);

                if (result == null) return;

                gotoProductDetails(result.getContents());
                break;
            }
        }
    }

    private void gotoProductDetails(String code) {
        Product product = productsAdapter.getProductByCode(code);

        if (product == null) {
            Toast.makeText(getContext(),
                    getActivity().getString(R.string.scan_searched_product_not_found),
                    Toast.LENGTH_LONG).show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable("selectedProduct", product);

        Navigation.findNavController(fragmentView).navigate(R.id.go_to_product_details, bundle);
    }


}

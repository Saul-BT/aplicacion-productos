package com.example.productmanager;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.productmanager.adapters.AdapterOpinedProducts;
import com.example.productmanager.model.FireManager;
import com.example.productmanager.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private FireManager fm = FireManager.getInstance();

    private AdapterOpinedProducts adapterOpinedProducts;
    private RecyclerView recyclerOpinedProducts;

    private TextView accounTRealName, accountEmail, accountOpinedProductHeader;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        if (MainActivity.currentUser.getType().canManageUsers) {
            view.findViewById(R.id.nsv_other_accounts).setVisibility(View.VISIBLE);
        }

        Context ctx = getActivity();

        accounTRealName = view.findViewById(R.id.tv_account_realname);
        accountEmail = view.findViewById(R.id.tv_account_email);
        accountOpinedProductHeader = view.findViewById(R.id.tv_account_opined_products);

        accounTRealName.setText(ctx.getString(R.string.account_realname_template,
                MainActivity.currentUser.getRealname()));
        accountEmail.setText(ctx.getString(R.string.account_email_template,
                MainActivity.currentUser.getEmail()));
        accountOpinedProductHeader.setText(ctx.getString(R.string.account_opined_products_header_template,
                MainActivity.currentUser.getUsername()));

        recyclerOpinedProducts = view.findViewById(R.id.recycler_opined_products);
        recyclerOpinedProducts.setLayoutManager(new GridLayoutManager(getActivity(), getResponsiveSpan()));

        adapterOpinedProducts = new AdapterOpinedProducts(
                getActivity(),
                R.layout.opined_product_view,
                new ArrayList<Product>()
        );
        recyclerOpinedProducts.setAdapter(adapterOpinedProducts);

        fm.dbProductsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    List<Product> products = new ArrayList<>();

                    for (QueryDocumentSnapshot document : result) {
                        products.add(document.toObject(Product.class));
                    }

                    adapterOpinedProducts.setProducts(products);
                    adapterOpinedProducts.notifyDataSetChanged();
                }
            }
        });

        return view;
    }

    private int getResponsiveSpan() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int productImageSize = (int) getActivity().getResources()
                .getDimension(R.dimen.list_product_image_size);
        int layoutPadding = (int) getActivity().getResources()
                .getDimension(R.dimen.main_layout_padding);
        int imageMargin = (int) getActivity().getResources()
                .getDimension(R.dimen.grid_product_image_margin);

        //int a = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 40, displayMetrics);
        //int b = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, displayMetrics);

        return ((screenWidth - layoutPadding * 2) / (productImageSize + imageMargin * 2));
    }
}

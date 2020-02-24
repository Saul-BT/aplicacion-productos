package com.example.productmanager;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.productmanager.adapters.AdapterOpinedProducts;
import com.example.productmanager.adapters.AdapterUsers;
import com.example.productmanager.model.FireManager;
import com.example.productmanager.model.Product;
import com.example.productmanager.model.User;
import com.example.productmanager.model.UserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private FireManager fm = FireManager.getInstance();

    private AdapterOpinedProducts adapterOpinedProducts;
    private RecyclerView recyclerOpinedProducts;

    private AdapterUsers adapterUsers;
    private RecyclerView recyclerUsers;

    private TextView accounTRealName, accountEmail, accountOpinedProductHeader;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().hide();

        if (MainActivity.currentUser.getType().canManageUsers) {
            view.findViewById(R.id.ll_other_accounts).setVisibility(View.VISIBLE);
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

        fm.dbOpinionsRef.whereEqualTo("userRef", fm.dbUsersRef.document(MainActivity.currentUser.getUsername()))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    Set<Product> products = new HashSet<>();

                    for (QueryDocumentSnapshot document : result) {
                        ((DocumentReference) document.get("productRef"))
                                .get().addOnCompleteListener(addProductsToAdapter(products));
                    }
                }
            }
        });

        recyclerUsers = view.findViewById(R.id.recycler_users);
        recyclerUsers.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapterUsers = new AdapterUsers(getActivity(), R.layout.user_view, new ArrayList<User>());
        recyclerUsers.setAdapter(adapterUsers);

        fm.dbUsersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    List<User> users = new ArrayList<>();

                    for (QueryDocumentSnapshot document : result) {
                        User user = document.toObject(User.class);
                        if (user.getType() == UserType.OWNER) continue;

                        users.add(user);
                    }

                    adapterUsers.setUsers(users);
                    adapterUsers.notifyDataSetChanged();
                }
            }
        });

        return view;
    }

    private OnCompleteListener<DocumentSnapshot> addProductsToAdapter(final Set<Product> productsToCompare) {
        return new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    Product opinedProduct = document.toObject(Product.class);

                    if (!productsToCompare.contains(opinedProduct)) {
                        productsToCompare.add(opinedProduct);
                        adapterOpinedProducts.addProduct(opinedProduct);
                        adapterOpinedProducts.notifyItemChanged(
                                adapterOpinedProducts.getItemCount() - 1);
                    }
                }
            }
        };
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

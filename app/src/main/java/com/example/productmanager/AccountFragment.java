package com.example.productmanager;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.productmanager.adapters.AdapterOpinedProducts;
import com.example.productmanager.adapters.AdapterUsers;
import com.example.productmanager.model.FireManager;
import com.example.productmanager.model.Product;
import com.example.productmanager.model.User;
import com.example.productmanager.model.UserSession;
import com.example.productmanager.model.UserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private View fragmentView;

    private FireManager fm = FireManager.getInstance();

    private AdapterOpinedProducts adapterOpinedProducts;
    private RecyclerView recyclerOpinedProducts;

    private AdapterUsers adapterUsers;
    private RecyclerView recyclerUsers;

    private ImageButton ibSignOut;
    private TextView tvAccountRealName, tvAccountEmail, tvAccountOpinedProductHeader;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().hide();

        setUpComponents(view);

        return view;
    }

    private void setUpComponents(View view) {
        fragmentView = view;

        if (UserSession.currentUser.getType().canManageUsers) {
            view.findViewById(R.id.ll_other_accounts).setVisibility(View.VISIBLE);
        }

        Context ctx = getActivity();

        ibSignOut = view.findViewById(R.id.ib_sign_out);
        tvAccountRealName = view.findViewById(R.id.tv_account_realname);
        tvAccountEmail = view.findViewById(R.id.tv_account_email);
        tvAccountOpinedProductHeader = view.findViewById(R.id.tv_account_opined_products);

        tvAccountRealName.setText(ctx.getString(R.string.account_realname_template,
                UserSession.currentUser.getRealname()));
        tvAccountEmail.setText(ctx.getString(R.string.account_email_template,
                UserSession.currentUser.getEmail()));
        tvAccountOpinedProductHeader.setText(ctx.getString(R.string.account_opined_products_header_template,
                UserSession.currentUser.getUsername()));

        setUpAdapters();

        ibSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserSession.miss(getContext());
                Navigation.findNavController(view).navigate(R.id.go_to_sign_in);
            }
        });
    }

    private void setUpAdapters() {
        recyclerOpinedProducts = fragmentView.findViewById(R.id.recycler_opined_products);
        recyclerOpinedProducts.setLayoutManager(new GridLayoutManager(getActivity(), getResponsiveSpan()));

        adapterOpinedProducts = new AdapterOpinedProducts(
                getActivity(),
                R.layout.opined_product_view,
                new ArrayList<Product>()
        );
        recyclerOpinedProducts.setAdapter(adapterOpinedProducts);

        fm.dbOpinionsRef.whereEqualTo("userRef", fm.dbUsersRef.document(UserSession.currentUser.getUsername()))
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documents) {
                Set<Product> products = new HashSet<>();

                for (QueryDocumentSnapshot document : documents) {
                    ((DocumentReference) document.get("productRef"))
                            .get().addOnCompleteListener(addProductsToAdapter(products));
                }
            }
        });

        recyclerUsers = fragmentView.findViewById(R.id.recycler_users);
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
                        if (UserSession.currentUser.equals(user)) continue;

                        users.add(user);
                    }

                    adapterUsers.setUsers(users);
                    adapterUsers.notifyDataSetChanged();
                }
            }
        });
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

        return ((screenWidth - layoutPadding * 2) / (productImageSize + imageMargin * 2));
    }
}

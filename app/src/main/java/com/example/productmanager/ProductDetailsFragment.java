package com.example.productmanager;


import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.productmanager.adapters.AdapterOpinions;
import com.example.productmanager.model.FireManager;
import com.example.productmanager.model.ImageConverter;
import com.example.productmanager.model.Opinion;
import com.example.productmanager.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductDetailsFragment extends Fragment {

    private FireManager fm = FireManager.getInstance();

    private Product selectedProduct;
    private RecyclerView recyclerView;
    private AdapterOpinions opinionsAdapter;
    private ImageView productPhoto;
    private LinearLayout detailsHead;
    private TextView productName, productCode, productPrice, productDescription, etMessage;
    private ImageView ivSetDetailsHeadVisibility;
    private boolean isDetailsHeadVisible;

    private DecimalFormat df = new DecimalFormat("###.#");

    public ProductDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);
        selectedProduct = getArguments().getParcelable("selectedProduct");

        isDetailsHeadVisible = true;
        ivSetDetailsHeadVisibility = view.findViewById(R.id.ib_expand_opinions);
        detailsHead = view.findViewById(R.id.product_details_head);
        etMessage = view.findViewById(R.id.et_opinion_message_send);

        productName = view.findViewById(R.id.tv_product_name);
        productCode = view.findViewById(R.id.tv_product_code);
        productPrice = view.findViewById(R.id.tv_product_price);
        productPhoto = view.findViewById(R.id.iv_product_details_photo);
        productDescription = view.findViewById(R.id.tv_product_description);

        productName.setText(selectedProduct.getName());
        productCode.setText(selectedProduct.getCode());
        productPrice.setText(getString(
                R.string.price_with_currency_template,
                df.format(selectedProduct.getPrice())));
        productDescription.setText(selectedProduct.getDescription());

        recyclerView = view.findViewById(R.id.recyler_opinions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        opinionsAdapter = new AdapterOpinions(getActivity(), R.layout.opinion_view, new ArrayList<Opinion>());
        recyclerView.setAdapter(opinionsAdapter);

        if (!selectedProduct.getEncodedPhoto().isEmpty()) {
            Bitmap bitmap = ImageConverter.convertBase64ToBitmap(selectedProduct.getEncodedPhoto());
            productPhoto.setImageDrawable(ImageConverter.getCoolBitmapDrawable(bitmap));
        }

        fm.dbOpinionsRef.whereEqualTo("productRef", fm.dbProductsRef.document(selectedProduct.getCode()))
                .orderBy("date").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    List<Opinion> opinions = new ArrayList<>();

                    for (QueryDocumentSnapshot document : result) {
                        opinions.add(new Opinion(
                                document.get("date", String.class),
                                document.get("author", String.class),
                                document.get("message", String.class)
                        ));
                    }

                    //productsAdapter = new AdapterProducts(ProductsFragment.this, R.layout.product_view, products);
                    opinionsAdapter.setOpinions(opinions);
                    opinionsAdapter.notifyDataSetChanged();
                }
            }
        });

        ivSetDetailsHeadVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int visibility;

                if (isDetailsHeadVisible) {
                    visibility = View.GONE;
                    isDetailsHeadVisible = false;
                    ivSetDetailsHeadVisibility.setImageDrawable(
                            getActivity().getDrawable(R.drawable.ic_fullscreen_exit_black_30dp)
                    );
                }
                else {
                    visibility = View.VISIBLE;
                    isDetailsHeadVisible = true;
                    ivSetDetailsHeadVisibility.setImageDrawable(
                            getActivity().getDrawable(R.drawable.ic_fullscreen_black_30dp)
                    );
                }

                detailsHead.setVisibility(visibility);
            }
        });

        view.findViewById(R.id.fab_send_opinion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etMessage.getText().toString().isEmpty()) return;

                Opinion newOpinion = new Opinion(
                        String.valueOf(new Date().getTime()),
                        MainActivity.currentUser.getUsername(),
                        etMessage.getText().toString()
                );

                opinionsAdapter.opinions.add(newOpinion);
                opinionsAdapter.notifyItemChanged(opinionsAdapter.opinions.size() - 1);
                fm.addOpinion(selectedProduct, newOpinion);

                etMessage.setText(null);
            }
        });

        if (MainActivity.currentUser.getType().canManageProducts) {
            Button editProduct = view.findViewById(R.id.b_edit_product);
            Button deleteProduct = view.findViewById(R.id.b_delete_product);

            editProduct.setVisibility(View.VISIBLE);
            deleteProduct.setVisibility(View.VISIBLE);

            editProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("productToEdit", selectedProduct);

                    Navigation.findNavController(view).popBackStack();
                    Navigation.findNavController(view).navigate(R.id.go_to_set_product, bundle);
                }
            });

            deleteProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fm.dbProductsRef.document(selectedProduct.getCode()).delete();
                    Navigation.findNavController(view).popBackStack();
                }
            });
        }

        return view;
    }

}

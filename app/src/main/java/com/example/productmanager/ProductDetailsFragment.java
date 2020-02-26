package com.example.productmanager;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import com.example.productmanager.model.UserSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
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

    private View fragmentView;

    private FireManager fm = FireManager.getInstance();

    private Product selectedProduct;

    private RecyclerView recyclerView;
    private AdapterOpinions opinionsAdapter;

    private ImageView etProductPhoto, ivSetDetailsHeadVisibility;
    private LinearLayout detailsHead;
    private TextView etProductName, etProductCode, etProductPrice, etProductDescription, etMessage;
    private Button editProduct, deleteProduct;
    private boolean isDetailsHeadVisible;

    private DecimalFormat df = new DecimalFormat("###.##");

    public ProductDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().hide();

        setUpComponents(view);

        return view;
    }

    private void setUpComponents(View view) {
        fragmentView = view;

        selectedProduct = getArguments().getParcelable("selectedProduct");

        editProduct = view.findViewById(R.id.b_edit_product);
        deleteProduct = view.findViewById(R.id.b_delete_product);

        isDetailsHeadVisible = true;
        ivSetDetailsHeadVisibility = view.findViewById(R.id.iv_expand_opinions);
        detailsHead = view.findViewById(R.id.product_details_head);
        etMessage = view.findViewById(R.id.et_opinion_message_send);

        etProductName = view.findViewById(R.id.tv_product_name);
        etProductCode = view.findViewById(R.id.tv_product_code);
        etProductPrice = view.findViewById(R.id.tv_product_price);
        etProductPhoto = view.findViewById(R.id.iv_product_details_photo);
        etProductDescription = view.findViewById(R.id.tv_product_description);

        etProductName.setText(selectedProduct.getName());
        etProductCode.setText(selectedProduct.getCode());
        etProductPrice.setText(getString(
                R.string.price_with_currency_template,
                df.format(selectedProduct.getPrice())));
        etProductDescription.setText(selectedProduct.getDescription());

        recyclerView = view.findViewById(R.id.recyler_opinions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        opinionsAdapter = new AdapterOpinions(getActivity(), R.layout.opinion_view, new ArrayList<Opinion>());
        recyclerView.setAdapter(opinionsAdapter);

        if (UserSession.currentUser.getType().canManageProducts) {
            editProduct.setVisibility(View.VISIBLE);
            deleteProduct.setVisibility(View.VISIBLE);
        }

        setUpClickListeners();

        if (!selectedProduct.getEncodedPhoto().isEmpty()) {
            Bitmap bitmap = ImageConverter.convertBase64ToBitmap(selectedProduct.getEncodedPhoto());
            etProductPhoto.setImageDrawable(ImageConverter.getCoolBitmapDrawable(bitmap));
        }

        fm.dbOpinionsRef.whereEqualTo("productRef", fm.dbProductsRef.document(selectedProduct.getCode()))
                .orderBy("date", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(addOpinionsToAdapter());
    }

    private OnSuccessListener<? super QuerySnapshot> addOpinionsToAdapter() {
        return new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documents) {
                List<Opinion> opinions = new ArrayList<>();

                for (QueryDocumentSnapshot document : documents) {
                    opinions.add(new Opinion(
                            document.get("date", String.class),
                            document.get("author", String.class),
                            document.get("message", String.class)
                    ));
                }

                opinionsAdapter.setOpinions(opinions);
            }
        };
    }

    private void setUpClickListeners() {
        ivSetDetailsHeadVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int visibility = View.VISIBLE;
                Drawable expandIcon = getActivity().getDrawable(R.drawable.ic_fullscreen_black_30dp);

                if (isDetailsHeadVisible) {
                    visibility = View.GONE;
                    expandIcon = getActivity().getDrawable(R.drawable.ic_fullscreen_exit_black_30dp);
                }

                detailsHead.setVisibility(visibility);
                ivSetDetailsHeadVisibility.setImageDrawable(expandIcon);
                isDetailsHeadVisible = !isDetailsHeadVisible;
            }
        });

        fragmentView.findViewById(R.id.fab_send_opinion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etMessage.getText().toString().isEmpty()) return;

                Opinion newOpinion = new Opinion(
                        String.valueOf(new Date().getTime()),
                        UserSession.currentUser.getUsername(),
                        etMessage.getText().toString()
                );

                opinionsAdapter.addOpinion(newOpinion);
                fm.setOpinion(selectedProduct, newOpinion);

                etMessage.setText(null);
            }
        });

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

}

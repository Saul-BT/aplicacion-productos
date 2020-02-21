package com.example.productmanager;


import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.productmanager.model.FireManager;
import com.example.productmanager.model.ImageConverter;
import com.example.productmanager.model.Product;

import java.text.DecimalFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductDetailsFragment extends Fragment {

    private FireManager fm = FireManager.getInstance();

    private Product selectedProduct;
    private ImageView productPhoto;
    private TextView productName, productCode, productPrice, productDescription;

    private DecimalFormat df = new DecimalFormat("###.#");

    public ProductDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);
        selectedProduct = getArguments().getParcelable("selectedProduct");

        productName = view.findViewById(R.id.tv_product_name);
        productCode = view.findViewById(R.id.tv_product_code);
        productPrice = view.findViewById(R.id.tv_product_price);
        productPhoto = view.findViewById(R.id.iv_product_details_photo);
        productDescription = view.findViewById(R.id.tv_product_description);

        productName.setText(selectedProduct.getName());
        productCode.setText(selectedProduct.getCode());
        productPrice.setText(getString(
                R.string.price_with_currency,
                df.format(selectedProduct.getPrice())));
        productDescription.setText(selectedProduct.getDescription());

        if (!selectedProduct.getEncodedPhoto().isEmpty()) {
            Bitmap bitmap = ImageConverter.convertBase64ToBitmap(selectedProduct.getEncodedPhoto());
            productPhoto.setImageDrawable(ImageConverter.getCoolBitmapDrawable(bitmap));
        }

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

package com.example.productmanager;


import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.productmanager.model.ImageConverter;
import com.example.productmanager.model.Product;

import java.text.DecimalFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductDetailsFragment extends Fragment {

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
        Bitmap bitmap = ImageConverter.convertBase64ToBitmap(selectedProduct.getEncodedPhoto());

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
        productPhoto.setImageDrawable(ImageConverter.getCoolBitmapDrawable(bitmap));
        productDescription.setText(selectedProduct.getDescription());

        return view;
    }

}

package com.example.productmanager;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.productmanager.model.FireManager;
import com.example.productmanager.model.ImageConverter;
import com.example.productmanager.model.Product;
import com.google.android.material.textfield.TextInputEditText;

import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;


/**
 * A simple {@link Fragment} subclass.
 */
public class SetProductFragment extends Fragment {

    ZxingOrient codeScanner;
    private FireManager fm = FireManager.getInstance();

    private Bitmap bitmapPhoto;
    private ImageView productPhoto;
    private TextInputEditText productName, productCode, productPrice, productDescription;

    private final int TAKE_PHOTO_REQUEST_CODE = 100;

    public SetProductFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_product, container, false);
        codeScanner = new ZxingOrient(this);
        codeScanner.setToolbarColor(getString(R.color.colorAccent))
                   .setInfoBoxColor(getString(R.color.colorAccent))
                   .setInfo(getString(R.string.info_scanner));

        productName = view.findViewById(R.id.et_add_product_name);
        productCode = view.findViewById(R.id.et_add_product_code);
        productPrice = view.findViewById(R.id.et_add_product_price);
        productPhoto = view.findViewById(R.id.iv_add_product_photo);
        productDescription = view.findViewById(R.id.et_add_product_description);

        if (getArguments() != null) {
            Product productToEdit = getArguments().getParcelable("productToEdit");

            productName.setText(productToEdit.getName());
            productCode.setText(productToEdit.getCode());
            productPrice.setText(String.valueOf(productToEdit.getPrice()));
            productDescription.setText(productToEdit.getDescription());

            if (!productToEdit.getEncodedPhoto().isEmpty()) {
                bitmapPhoto = ImageConverter.convertBase64ToBitmap(productToEdit.getEncodedPhoto());
                productPhoto.setImageDrawable(ImageConverter.getCoolBitmapDrawable(bitmapPhoto));
            }
        }

        view.findViewById(R.id.fab_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePhoto = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePhoto, TAKE_PHOTO_REQUEST_CODE);
            }
        });

        view.findViewById(R.id.b_scan_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.initiateScan();
            }
        });

        view.findViewById(R.id.b_add_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product newProduct = new Product(
                        bitmapPhoto != null ? ImageConverter.convertBitmapToBase64(bitmapPhoto) : "",
                        productName.getText().toString(),
                        productCode.getText().toString(),
                        productDescription.getText().toString(),
                        Float.parseFloat(productPrice.getText().toString())
                );

                fm.addProduct(newProduct);
                Navigation.findNavController(view).popBackStack();
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case ZxingOrient.REQUEST_CODE: {
                ZxingOrientResult result = ZxingOrient.parseActivityResult(requestCode, resultCode, data);

                if (result == null) return;

                productCode.setText(result.getContents());
                break;
            }
            case TAKE_PHOTO_REQUEST_CODE: {
                bitmapPhoto = (Bitmap) data.getExtras().get("data");
                RoundedBitmapDrawable coolPhoto =
                        ImageConverter.getCoolBitmapDrawable(bitmapPhoto);

                productPhoto.setImageDrawable(coolPhoto);
                break;
            }
        }

    }
}

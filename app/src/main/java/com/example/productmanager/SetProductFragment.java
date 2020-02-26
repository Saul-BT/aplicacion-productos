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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.productmanager.model.FireManager;
import com.example.productmanager.model.ImageConverter;
import com.example.productmanager.model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;


/**
 * A simple {@link Fragment} subclass.
 */
public class SetProductFragment extends Fragment {

    private ZxingOrient codeScanner;
    private FireManager fm = FireManager.getInstance();

    private Bitmap bitmapPhoto;
    private ImageView etProductPhoto;
    private TextInputEditText etProductName, etProductCode, etProductPrice, etProductDescription;
    private Button bScanCode, bAddProduct;
    private FloatingActionButton fabTakePhoto;

    private final int TAKE_PHOTO_REQUEST_CODE = 100;

    public SetProductFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_product, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().hide();

        codeScanner = new ZxingOrient(this);
        codeScanner.setIcon(R.drawable.ic_launcher_logo)
                .setToolbarColor(getString(R.color.colorAccent))
                .setInfoBoxColor(getString(R.color.colorAccent))
                .setInfo(getString(R.string.info_scanner));

        bScanCode = view.findViewById(R.id.b_scan_code);
        bAddProduct = view.findViewById(R.id.b_add_product);
        fabTakePhoto = view.findViewById(R.id.fab_take_photo);

        etProductName = view.findViewById(R.id.et_add_product_name);
        etProductCode = view.findViewById(R.id.et_add_product_code);
        etProductPrice = view.findViewById(R.id.et_add_product_price);
        etProductPhoto = view.findViewById(R.id.iv_add_product_photo);
        etProductDescription = view.findViewById(R.id.et_add_product_description);

        if (getArguments() != null) {
            editProduct();
        }

        fabTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePhoto = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePhoto, TAKE_PHOTO_REQUEST_CODE);
            }
        });

        bScanCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.initiateScan();
            }
        });

        bAddProduct.setOnClickListener(addProduct());

        return view;
    }

    private void editProduct() {
        Product productToEdit = getArguments().getParcelable("productToEdit");

        etProductName.setText(productToEdit.getName());
        etProductCode.setText(productToEdit.getCode());
        etProductPrice.setText(String.valueOf(productToEdit.getPrice()));
        etProductDescription.setText(productToEdit.getDescription());

        bScanCode.setEnabled(false);
        etProductCode.setEnabled(false);

        if (!productToEdit.getEncodedPhoto().isEmpty()) {
            bitmapPhoto = ImageConverter.convertBase64ToBitmap(productToEdit.getEncodedPhoto());
            etProductPhoto.setImageDrawable(ImageConverter.getCoolBitmapDrawable(bitmapPhoto));
        }
    }

    private View.OnClickListener addProduct() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String encodedBitmap = bitmapPhoto != null
                        ? ImageConverter.convertBitmapToBase64(bitmapPhoto)
                        : "";
                String productName = etProductName.getText().toString();
                String productCode = etProductCode.getText().toString();
                String productDescription = etProductDescription.getText().toString();
                String dataProductPrice = etProductPrice.getText().toString();

                if (isSomethingEmpty(productName, productCode, dataProductPrice)) {
                    Toast.makeText(getContext(),
                            R.string.uncompleted_fields_message,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                float productPrice = Float.parseFloat(dataProductPrice);

                Product newProduct = new Product(
                        encodedBitmap,
                        productName,
                        productCode,
                        productDescription,
                        productPrice
                );

                fm.setProduct(newProduct);
                Navigation.findNavController(view).popBackStack();
            }

            private boolean isSomethingEmpty(String... data) {
                for (String chunk : data)
                    if (chunk.isEmpty()) return true;

                return false;
            }
        };
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case ZxingOrient.REQUEST_CODE: {
                ZxingOrientResult result = ZxingOrient.parseActivityResult(requestCode, resultCode, data);

                if (result == null) return;

                etProductCode.setText(result.getContents());
                break;
            }
            case TAKE_PHOTO_REQUEST_CODE: {
                bitmapPhoto = (Bitmap) data.getExtras().get("data");
                RoundedBitmapDrawable coolPhoto =
                        ImageConverter.getCoolBitmapDrawable(bitmapPhoto);

                etProductPhoto.setImageDrawable(coolPhoto);
                break;
            }
        }

    }
}

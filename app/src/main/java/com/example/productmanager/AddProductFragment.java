package com.example.productmanager;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.productmanager.model.FireManager;
import com.example.productmanager.model.Product;
import com.google.android.material.textfield.TextInputEditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddProductFragment extends Fragment {

    private FireManager fm = FireManager.getInstance();

    public AddProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);
        final TextInputEditText productName, code, price, description;

        productName = view.findViewById(R.id.et_add_product_name);
        code = view.findViewById(R.id.et_add_product_code);
        price = view.findViewById(R.id.et_add_product_price);
        description = view.findViewById(R.id.et_add_product_description);

        view.findViewById(R.id.b_add_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product newProduct = new Product(
                        productName.getText().toString(),
                        code.getText().toString(),
                        description.getText().toString(),
                        Float.parseFloat(price.getText().toString())
                );

                fm.addProduct(newProduct);
                Navigation.findNavController(view).popBackStack();
            }
        });

        return view;
    }

}

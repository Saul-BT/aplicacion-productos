package com.example.productmanager;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.productmanager.model.FireManager;
import com.example.productmanager.model.User;
import com.example.productmanager.model.UserType;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private FireManager fm = FireManager.getInstance();

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        final TextInputEditText userName, realName, email, pass;

        userName = view.findViewById(R.id.et_sign_up_username);
        realName = view.findViewById(R.id.et_sign_up_real_name);
        email = view.findViewById(R.id.et_sign_up_email);
        pass = view.findViewById(R.id.et_sign_up_pass);

        Button submitUser = view.findViewById(R.id.b_sign_up);

        submitUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User newUser = new User(
                        userName.getText().toString(),
                        realName.getText().toString(),
                        email.getText().toString(),
                        Hashing.sha512().hashString(pass.getText().toString(), Charsets.UTF_8).toString(),
                        UserType.NORMAL
                );

                fm.setUser(newUser);
                MainActivity.currentUser = newUser;
                Navigation.findNavController(view).navigate(R.id.go_to_products);
            }
        });

        view.findViewById(R.id.tv_link_terms_and_conditions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.go_to_terms);
            }
        });

        return view;
    }

}

package com.example.productmanager;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.productmanager.firebase.FireManager;
import com.example.productmanager.firebase.User;
import com.example.productmanager.firebase.UserType;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import static java.security.spec.MGF1ParameterSpec.SHA1;


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
                User newUser;

                newUser = new User(
                        userName.getText().toString(),
                        realName.getText().toString(),
                        email.getText().toString(),
                        Hashing.sha512().hashString(pass.getText().toString(), Charsets.UTF_8).toString(),
                        UserType.NORMAL
                );

                fm.addUser(newUser);
                Navigation.findNavController(view).navigate(R.id.go_to_products);
            }
        });

        return view;
    }

}

package com.example.productmanager;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.productmanager.model.FireManager;
import com.example.productmanager.model.User;
import com.example.productmanager.model.UserSession;
import com.example.productmanager.model.UserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.firebase.firestore.DocumentSnapshot;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private View fragmentView;
    private FireManager fm = FireManager.getInstance();

    private TextInputEditText etUsername, etRealName, etEmail, etPass;
    private CheckBox cbTermsAgree;

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().hide();

        setUpComponents(view);

        return view;
    }

    private void setUpComponents(View view) {
        fragmentView = view;

        etUsername = fragmentView.findViewById(R.id.et_sign_up_username);
        etRealName = fragmentView.findViewById(R.id.et_sign_up_real_name);
        etEmail = fragmentView.findViewById(R.id.et_sign_up_email);
        etPass = fragmentView.findViewById(R.id.et_sign_up_pass);
        cbTermsAgree = fragmentView.findViewById(R.id.cb_sign_up_terms_agree);

        Button bSubmitUser = fragmentView.findViewById(R.id.b_sign_up);

        bSubmitUser.setOnClickListener(submitUser());

        // Navigate to TermsFragment
        fragmentView.findViewById(R.id.tv_link_terms_and_conditions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.go_to_terms);
            }
        });
    }

    private View.OnClickListener submitUser() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String realName = etRealName.getText().toString();
                String email = etEmail.getText().toString();
                String pass = etPass.getText().toString();

                if (isSomethingEmpty(username, realName, email, pass)) {
                    Toast.makeText(getContext(),
                            fragmentView.getResources().getString(R.string.uncompleted_fields_message),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (!username.matches("\\w{4,15}")) {
                    etUsername.requestFocus();
                    etUsername.setError(fragmentView.getResources()
                            .getString(R.string.invalid_username_message));

                    return;
                }

                if (!email.matches("\\w+@\\w+\\.\\w+")) {
                    etEmail.requestFocus();
                    etEmail.setError(fragmentView.getResources()
                            .getString(R.string.invalid_email_message));

                    return;
                }

                if (!cbTermsAgree.isChecked()) {
                    Toast.makeText(getContext(),
                            fragmentView.getResources().getString(R.string.unchecked_terms_message),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                User newUser = new User(
                        username,
                        realName,
                        email,
                        Hashing.sha512().hashString(pass, Charsets.UTF_8).toString(),
                        UserType.NORMAL
                );

                fm.dbUsersRef.document(username).get().addOnCompleteListener(registerUser(newUser));
            }

            private boolean isSomethingEmpty(String... data) {
                for (String chunk : data)
                    if (chunk.isEmpty()) return true;

                return false;
            }
        };
    }

    private OnCompleteListener<DocumentSnapshot> registerUser(final User newUser) {
        return new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Toast.makeText(getActivity(),
                                fragmentView.getResources()
                                        .getString(R.string.user_already_exists_error,
                                                newUser.getUsername()),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    UserSession.currentUser = newUser;
                    fm.setUser(newUser);

                    Navigation.findNavController(fragmentView).popBackStack();
                    Navigation.findNavController(fragmentView).navigate(R.id.go_to_products);
                }
                else {
                    Toast.makeText(getActivity(),
                            fragmentView.getResources().getString(R.string.unknown_sign_up_error),
                            Toast.LENGTH_LONG).show();
                }
            }
        };
    }

}

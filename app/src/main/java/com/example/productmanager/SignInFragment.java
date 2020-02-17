package com.example.productmanager;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.productmanager.firebase.FireManager;
import com.example.productmanager.firebase.User;
import com.example.productmanager.firebase.UserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    private View fragmentView;
    private FireManager fm = FireManager.getInstance();

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;


    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        setUpComponents(view);

        return view;
    }

    private void setUpComponents(View view) {
        fragmentView = view;

        etUsername = fragmentView.findViewById(R.id.et_sign_in_username);
        etPassword = fragmentView.findViewById(R.id.et_sign_in_pass);

        fragmentView.findViewById(R.id.b_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getActivity(),
                            fragmentView.getResources().getString(R.string.uncompleted_fields_message),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                fm.dbUsersRef.document(username).get()
                        .addOnCompleteListener(checkAccess());
            }
        });

        fragmentView.findViewById(R.id.b_want_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.go_to_sign_up);
            }
        });
    }

    private OnCompleteListener<DocumentSnapshot> checkAccess() {
        return new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String hashedPass = Hashing.sha512()
                            .hashString(etPassword.getText().toString(), Charsets.UTF_8).toString();
                    DocumentSnapshot document = task.getResult();

                    if (document.exists() && document.get("pass", String.class).equals(hashedPass)) {
                        MainActivity.currentUser = new User(
                                document.getId(),
                                document.get("realName", String.class),
                                document.get("email", String.class),
                                null,
                                document.get("type", UserType.class)
                                );
                        Navigation.findNavController(fragmentView).navigate(R.id.go_to_products);
                    }
                    else
                        Toast.makeText(getActivity(),
                                fragmentView.getResources().getString(R.string.incorrect_login_error),
                                Toast.LENGTH_LONG).show();

                    etUsername.setText(null);
                    etPassword.setText(null);

                    etUsername.clearFocus();
                    etPassword.clearFocus();
                }
                else
                    Toast.makeText(getActivity(),
                            fragmentView.getResources().getString(R.string.unknown_login_error),
                            Toast.LENGTH_LONG).show();
            }
        };
    }

}

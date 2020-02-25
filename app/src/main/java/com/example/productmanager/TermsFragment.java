package com.example.productmanager;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TermsFragment extends Fragment {

    private TextView tvTerms;

    public TermsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terms, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().hide();

        tvTerms = view.findViewById(R.id.tv_terms_and_conditions);

        tvTerms.setText(R.string.terms_and_conditions);

        view.findViewById(R.id.b_i_agree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });

        return view;
    }

}

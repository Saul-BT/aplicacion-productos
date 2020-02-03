package com.example.productmanager.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.Executor;

public class FireManager {

    final FirebaseFirestore db;
    public final CollectionReference dbUsersRef;
    public final CollectionReference dbProductsRef;
    public final CollectionReference dbOpinionsRef;

    private static FireManager fm;

    private FireManager() {
        db = FirebaseFirestore.getInstance();
        dbUsersRef = db.collection("usuarios");

        dbProductsRef = null;
        dbOpinionsRef = null;
    }

    public static FireManager getInstance() {
        if (fm == null)
            fm = new FireManager();

        return fm;
    }

    public void addUser(final User newUser) {
        dbUsersRef.document(newUser.getUsername()).set(
            new Object() {
                public final String realName = newUser.getRealname();
                public final String email = newUser.getEmail();
                public final String pass = newUser.getPass();
                public final UserType type = newUser.getType();
        });
    }
}

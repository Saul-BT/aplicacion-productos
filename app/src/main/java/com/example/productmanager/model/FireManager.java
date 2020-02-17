package com.example.productmanager.model;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

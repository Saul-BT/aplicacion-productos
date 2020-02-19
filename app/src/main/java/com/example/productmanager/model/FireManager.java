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
        dbUsersRef = db.collection("users");
        dbProductsRef = db.collection("products");

        dbOpinionsRef = null;
    }

    public static FireManager getInstance() {
        if (fm == null)
            fm = new FireManager();

        return fm;
    }

    public void addUser(final User newUser) {
        dbUsersRef.document(newUser.getUsername()).set(newUser);
    }

    public void addProduct(final Product newProduct) {
        dbProductsRef.document(newProduct.getCode()).set(newProduct);
    }
}

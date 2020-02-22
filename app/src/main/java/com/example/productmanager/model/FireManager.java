package com.example.productmanager.model;

import com.example.productmanager.MainActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
        dbOpinionsRef = db.collection("opinions");
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

    public void addOpinion(final Product product, final Opinion opinion) {
        fm.dbOpinionsRef.document().set(new Object() {
            public DocumentReference userRef = fm.dbUsersRef.document(MainActivity.currentUser.getUsername());
            public DocumentReference productRef = fm.dbProductsRef.document(product.getCode());
            public String date = opinion.getDate();
            public String author = opinion.getAuthor();
            public String message = opinion.getMessage();
        });
    }
}

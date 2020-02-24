package com.example.productmanager.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productmanager.R;
import com.example.productmanager.model.ImageConverter;
import com.example.productmanager.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AdapterOpinedProducts extends RecyclerView.Adapter<AdapterOpinedProducts.ProductsHolder> {

    private int resId;
    private Context ctx;
    private List<Product> products;

    public AdapterOpinedProducts(Context ctx, int resId, List<Product> products) {
        this.resId = resId;
        this.products = products;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public AdapterOpinedProducts.ProductsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterOpinedProducts.ProductsHolder holder = null;

        try {
            View view = ((AppCompatActivity) ctx).getLayoutInflater()
                    .inflate(this.resId, parent, false);
            holder = new AdapterOpinedProducts.ProductsHolder(view);
        }
        catch (Exception e) {
            Log.i("Informacion", e.getMessage());
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOpinedProducts.ProductsHolder holder, final int position) {
        holder.product = this.products.get(position);
        String encodedPhoto = holder.product.getEncodedPhoto();
        Drawable coolPhoto = ctx.getDrawable(R.drawable.preview);

        if (!encodedPhoto.isEmpty()) {
            Bitmap decodedPhoto = ImageConverter.convertBase64ToBitmap(encodedPhoto);
            coolPhoto = ImageConverter.getCoolBitmapDrawable(decodedPhoto);
        }

        int productImageSize = (int) ctx.getResources().getDimension(R.dimen.list_product_image_size);
        holder.image.getLayoutParams().height = productImageSize;
        holder.image.getLayoutParams().width = productImageSize;

        holder.image.setImageDrawable(coolPhoto);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("selectedProduct", products.get(position));

                Navigation.findNavController(view).navigate(R.id.go_to_product_details, bundle);
            }
        });
    }

    public void addProduct(Product products) {
        this.products.add(products);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductsHolder extends RecyclerView.ViewHolder {
        public View view;
        public ImageView image;
        public Product product;

        public ProductsHolder(@NonNull View productView) {
            super(productView);
            this.view = productView;
            this.image = productView.findViewById(R.id.iv_opined_product_image);
        }
    }
}

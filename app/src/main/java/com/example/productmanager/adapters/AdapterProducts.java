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

import com.example.productmanager.ProductsFragment;
import com.example.productmanager.R;
import com.example.productmanager.model.ImageConverter;
import com.example.productmanager.model.Product;

import java.util.List;

public class AdapterProducts extends RecyclerView.Adapter<AdapterProducts.ProductsHolder> {

    private int resId;
    private Context ctx;
    private List<Product> products;

    public AdapterProducts(Context ctx, int resId, List<Product> products) {
        this.resId = resId;
        this.products = products;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ProductsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProductsHolder holder = null;

        try {
            View view = ((AppCompatActivity) ctx).getLayoutInflater()
                    .inflate(this.resId, parent, false);
            holder = new ProductsHolder(view);
        }
        catch (Exception e) {
            Log.i("Informacion", e.getMessage());
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsHolder holder, final int position) {
        holder.product = this.products.get(position);
        String encodedPhoto = holder.product.getEncodedPhoto();
        Drawable coolPhoto = ctx.getDrawable(R.drawable.preview);

        if (!encodedPhoto.isEmpty()) {
            Bitmap decodedPhoto = ImageConverter.convertBase64ToBitmap(encodedPhoto);
            coolPhoto = ImageConverter.getCoolBitmapDrawable(decodedPhoto);
        }

        holder.image.getLayoutParams().height = 130;
        holder.image.getLayoutParams().width = 130;

        holder.image.setImageDrawable(coolPhoto);
        holder.tvName.setText(holder.product.getName());
        holder.tvPrice.setText(String.valueOf(holder.product.getPrice()));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("selectedProduct", products.get(position));

                Navigation.findNavController(view).navigate(R.id.go_to_product_details, bundle);
            }
        });
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductsHolder extends RecyclerView.ViewHolder {
        public View view;
        public ImageView image;
        public TextView tvName;
        public TextView tvPrice;
        public Product product;

        public ProductsHolder(@NonNull View productView) {
            super(productView);
            this.view = productView;
            this.image = productView.findViewById(R.id.iv_product_image);
            this.tvName = productView.findViewById(R.id.tv_product_name);
            this.tvPrice = productView.findViewById(R.id.tv_product_price);
        }
    }
}

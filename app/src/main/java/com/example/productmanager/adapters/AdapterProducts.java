package com.example.productmanager.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productmanager.ProductsFragment;
import com.example.productmanager.R;
import com.example.productmanager.firebase.Product;

import java.util.List;

public class AdapterProducts extends RecyclerView.Adapter<AdapterProducts.ProductsHolder> {

    private int resId;
    private Context ctx;
    private List<Product> products;
    private ProductsFragment fragment;

    public AdapterProducts(ProductsFragment fragment, int resId, List<Product> products) {
        this.resId = resId;
        this.products = products;
        this.fragment = fragment;
        this.ctx = fragment.getActivity();
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
    public void onBindViewHolder(@NonNull ProductsHolder holder, int position) {
        holder.product = this.products.get(position);

        holder.tvName.setText(holder.product.getName());
        holder.tvPrice.setText(String.valueOf(holder.product.getPrice()));
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
            this.tvName = productView.findViewById(R.id.tv_product_name);
            this.tvPrice = productView.findViewById(R.id.tv_product_price);
        }
    }
}

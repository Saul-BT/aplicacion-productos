package com.example.productmanager.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productmanager.R;
import com.example.productmanager.model.ImageConverter;
import com.example.productmanager.model.Product;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AdapterProducts extends RecyclerView.Adapter<AdapterProducts.ProductsHolder> implements Filterable {

    private int resId;
    private Context ctx;
    private List<Product> products;
    private List<Product> allProducts;
    private DecimalFormat df = new DecimalFormat("###.##");

    public AdapterProducts(Context ctx, int resId, List<Product> products) {
        this.resId = resId;
        this.products = products;
        this.allProducts = new ArrayList<>(products);
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

        int productImageSize = (int) ctx.getResources().getDimension(R.dimen.list_product_image_size);
        String formattedPrice = ctx.getString(
                R.string.price_with_currency_template,
                df.format(holder.product.getPrice()));
        holder.image.getLayoutParams().height = productImageSize;
        holder.image.getLayoutParams().width = productImageSize;

        holder.image.setImageDrawable(coolPhoto);
        holder.tvName.setText(holder.product.getName());
        holder.tvPrice.setText(formattedPrice);

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
        this.allProducts = new ArrayList<>(products);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public Product getProductByCode(String code) {
        for (Product product : allProducts) {
            if (product.getCode().equals(code))
                return product;
        }

        return null;
    }

    @Override
    public Filter getFilter() {
        return productFilter;
    }

    private Filter productFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Product> filteredProducts = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredProducts.addAll(allProducts);
            }
            else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Product product : allProducts) {
                    String productName = product.getName().toLowerCase();
                    if (productName.contains(filterPattern)) {
                        filteredProducts.add(product);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredProducts;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            products.clear();
            products.addAll(((List) filterResults.values));
            notifyDataSetChanged();
        }
    };

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

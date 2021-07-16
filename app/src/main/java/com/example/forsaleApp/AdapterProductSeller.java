package com.example.forsaleApp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.forsaleApp.Fragments.HomeFragment;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.HolderProductSeller> implements Filterable
{
    private Context context;
    public ArrayList<ModelProduct> productList, filterList;
    private FilterProduct filter;

    public AdapterProductSeller(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @NotNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType)
    {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_seller, parent, false);
        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterProductSeller.HolderProductSeller holder, int position)
    {
        //get data
        ModelProduct modelProduct = productList.get(position);
        String id = modelProduct.getProductId();
        String userid = modelProduct.getUserId();
        String productImage = modelProduct.getProductImage();
        String productName = modelProduct.getProductName();
        String productDescription = modelProduct.getProductDescription();
        String productCategory = modelProduct.getProductCategory();
        String productPrice = modelProduct.getProductPrice();
        String timestamp = modelProduct.getTimestamp();
        String Date = modelProduct.getDate();

        //set data
        holder.product_Name.setText(productName);
        holder.product_Description.setText(productDescription);
        holder.product_Price.setText("₹"+productPrice);
        holder.date.setText(Date);
        try {
            Picasso.get().load(productImage).placeholder(R.drawable.add_image).into(holder.productImage);
        }catch (Exception e){
            holder.productImage.setImageResource(R.drawable.add_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handle item click, show item details
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
        {
            filter = new FilterProduct(this, filterList);
        }
        return filter;
    }

    class HolderProductSeller extends RecyclerView.ViewHolder
    {
        private ImageView productImage;
        private TextView product_Name, product_Description, product_Price, date;

        public HolderProductSeller(@NonNull @NotNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            product_Name = itemView.findViewById(R.id.product_name);
            product_Description = itemView.findViewById(R.id.product_description);
            product_Price = itemView.findViewById(R.id.product_price);
            date = itemView.findViewById(R.id.date);

        }
    }
}

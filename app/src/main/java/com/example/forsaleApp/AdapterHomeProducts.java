package com.example.forsaleApp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.forsaleApp.Activities.EditProduct;
import com.example.forsaleApp.Activities.ProductDetails;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterHomeProducts extends RecyclerView.Adapter<AdapterHomeProducts.HolderHomeProducts> implements Filterable
{
    private Context context;
    public ArrayList<ModelProduct> productList, filterList;
    private FilterProduct filter;

    public AdapterHomeProducts(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @NotNull
    @Override
    public HolderHomeProducts onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType)
    {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_home_products, parent, false);
        return new HolderHomeProducts(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterHomeProducts.HolderHomeProducts holder, int position)
    {
        //get data
        final ModelProduct modelHomeProducts = productList.get(position);
        String name = modelHomeProducts.getName();
        String email = modelHomeProducts.getEmail();
        String password = modelHomeProducts.getPassword();
        String id = modelHomeProducts.getProductId();
        String userid = modelHomeProducts.getUserId();
        String productImage = modelHomeProducts.getProductImage();
        String productName = modelHomeProducts.getProductName();
        String productDescription = modelHomeProducts.getProductDescription();
        String productCategory = modelHomeProducts.getProductCategory();
        String productPrice = modelHomeProducts.getProductPrice();
        String timestamp = modelHomeProducts.getTimestamp();
        String Date = modelHomeProducts.getDate();
        String uid = modelHomeProducts.getUid();



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
                // detailsBottomSheet(modelProduct);//here models product contains details of clicked product
                Intent intent = new Intent(context, ProductDetails.class);
                intent.putExtra("ProductId", id);
                context.startActivity(intent);
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

    class HolderHomeProducts extends RecyclerView.ViewHolder
    {
        private ImageView productImage;
        private TextView product_Name, product_Description, product_Price, date;

        public HolderHomeProducts(@NonNull @NotNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            product_Name = itemView.findViewById(R.id.product_name);
            product_Description = itemView.findViewById(R.id.product_description);
            product_Price = itemView.findViewById(R.id.product_price);
            date = itemView.findViewById(R.id.date);

        }
    }
}

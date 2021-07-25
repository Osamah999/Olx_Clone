package com.example.forsaleApp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forsaleApp.Activities.ProductDetails;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>
{
    private final Context context;
    public ArrayList<ModelProduct> productList;


    public UserAdapter(Context context, ArrayList<ModelProduct> productList){
        this.context = context;
        this.productList = productList;

    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_lists, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserAdapter.ViewHolder holder, int position) {

        //get data
        final ModelProduct modelHomeProducts = productList.get(position);
        String name = modelHomeProducts.getName();
        String email = modelHomeProducts.getEmail();
        String password = modelHomeProducts.getPassword();
        String id = modelHomeProducts.getProductId();
        String userid = modelHomeProducts.getUserId();
        String userName = modelHomeProducts.getUserName();
        String productImage = modelHomeProducts.getProductImage();
        String productName = modelHomeProducts.getProductName();
        String productDescription = modelHomeProducts.getProductDescription();
        String productCategory = modelHomeProducts.getProductCategory();
        String productPrice = modelHomeProducts.getProductPrice();
        String latitude = modelHomeProducts.getLatitude();
        String longitude = modelHomeProducts.getLongitude();
        String timestamp = modelHomeProducts.getTimestamp();
        String Date = modelHomeProducts.getDate();
        String uid = modelHomeProducts.getUid();



        //set data
        holder.username.setText(userName);
        /*holder.product_Description.setText(productDescription);
        holder.product_Price.setText("₹"+productPrice);
        holder.date.setText(Date);
        try {
            Picasso.get().load(productImage).placeholder(R.drawable.add_image).into(holder.productImage);
        }catch (Exception e){
            holder.productImage.setImageResource(R.drawable.add_image);
        }*/

        holder.itemView.setOnClickListener(view -> {
            //handle item click, show item details
            Intent intent = new Intent(context, ProductDetails.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("ProductId", id);
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}

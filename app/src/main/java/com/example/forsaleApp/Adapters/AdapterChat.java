package com.example.forsaleApp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forsaleApp.Activities.ChatActivity;
import com.example.forsaleApp.ModelProduct;
import com.example.forsaleApp.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.ViewHolder>
{
    private final Context context;
    public ArrayList<ModelProduct> productList;


    public AdapterChat(Context context, ArrayList<ModelProduct> productList){
        this.context = context;
        this.productList = productList;

    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_lists, parent, false);
        return new AdapterChat.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterChat.ViewHolder holder, int position) {

        //get data
        final ModelProduct modelProduct = productList.get(position);
        String id = modelProduct.getProductId();
        String receiver = modelProduct.getReceiver();
        String userName = modelProduct.getUserName();

        //set data
        holder.username.setText(userName);

        holder.itemView.setOnClickListener(view -> {
            //handle item click, show item details
            Intent intent = new Intent(context, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("UserId", receiver);
            intent.putExtra("UserName", userName);
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

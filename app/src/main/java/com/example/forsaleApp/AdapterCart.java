package com.example.forsaleApp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import android.content.Intent;
import android.view.LayoutInflater;

import com.example.forsaleApp.Activities.AddToCart;
import com.example.forsaleApp.Activities.EditProduct;
import com.example.forsaleApp.Activities.ProductDetails;
import com.squareup.picasso.Picasso;
public class AdapterCart extends RecyclerView.Adapter<AdapterCart.HolderCart>
{

    private final Context context;
    public ArrayList<ModelProduct> productList;

    public AdapterCart(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;

    }

    @NonNull
    @NotNull
    @Override
    public HolderCart onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_seller, parent, false);
        return new HolderCart(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterCart.HolderCart holder, int position)
    {
        //get data
        final ModelProduct modelProduct = productList.get(position);
        String id = modelProduct.getProductId();
        String userid = modelProduct.getUserId();
        String productImage = modelProduct.getProductImage();
        String productName = modelProduct.getProductName();
        String productDescription = modelProduct.getProductDescription();
        String productCategory = modelProduct.getProductCategory();
        String productPrice = modelProduct.getProductPrice();
        String timestamp = modelProduct.getTimestamp();
        String Date = modelProduct.getDate();
        String uid = modelProduct.getUid();

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

        holder.itemView.setOnClickListener(view -> {
            //handle item click, show item details
            Intent intent = new Intent(context, AddToCart.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("ProductId", id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class HolderCart extends RecyclerView.ViewHolder {

        private final ImageView productImage;
        private final TextView product_Name;
        private final TextView product_Description;
        private final TextView product_Price;
        private final TextView date;

        public HolderCart(@NonNull @NotNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            product_Name = itemView.findViewById(R.id.product_name);
            product_Description = itemView.findViewById(R.id.product_description);
            product_Price = itemView.findViewById(R.id.product_price);
            date = itemView.findViewById(R.id.date);
        }
    }
}

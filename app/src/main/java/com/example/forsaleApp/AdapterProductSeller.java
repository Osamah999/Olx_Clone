package com.example.forsaleApp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.example.forsaleApp.Fragments.CartFragment;
import com.example.forsaleApp.Fragments.HomeFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
                Intent intent = new Intent(context, EditProduct.class);
                intent.putExtra("ProductId", id);
                context.startActivity(intent);
            }
        });

    }

   /* private void detailsBottomSheet(ModelProduct modelProduct)
    {
        //bottom sheet
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        //inflate view for bottom sheet
        View view = LayoutInflater.from(context).inflate(R.layout.product_sheet, null);
        //set view to bottom sheet
        bottomSheetDialog.setContentView(view);

        //init views of bottom sheet
        ImageButton backBtn = view.findViewById(R.id.back_btn);
        ImageButton deleteBtn = view.findViewById(R.id.delete_btn);
        ImageButton editBtn = view.findViewById(R.id.edit_btn);
        ImageView product_Image = view.findViewById(R.id.product_image);
        TextView product_Name = view.findViewById(R.id.product_name);
        TextView product_Description = view.findViewById(R.id.product_description);
        TextView product_Category = view.findViewById(R.id.product_category);
        TextView product_Price = view.findViewById(R.id.product_price);

        //get data
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
        product_Name.setText(productName);
        product_Description.setText(productDescription);
        product_Category.setText(productCategory);
        product_Price.setText("₹"+productPrice);

        try {
            Picasso.get().load(productImage).placeholder(R.drawable.add_image).into(product_Image);
        }catch (Exception e){
            product_Image.setImageResource(R.drawable.add_image);
        }

        //show dialog
        bottomSheetDialog.show();

        //edit click
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //open edit product activity
                Intent intent = new Intent(context, EditProduct.class);
                intent.putExtra("ProductId", id);
                context.startActivity(intent);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this product?")
                        .setCancelable(false)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteProduct(id);//id is the product id
                            }
                        })
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dismiss bottom sheet
                bottomSheetDialog.dismiss();
            }
        });

    }

    private void deleteProduct(String id)
    {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("delete Product");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait we are deleting the product...");
        progressDialog.show();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //product deleted
                        progressDialog.dismiss();
                        Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        //failed uploading image
                        progressDialog.dismiss();
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }*/


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

package com.example.forsaleApp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.forsaleApp.Adapters.AdapterCart;
import com.example.forsaleApp.Adapters.AdapterProductSeller;
import com.example.forsaleApp.ModelProduct;
import com.example.forsaleApp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class ProductsFragment extends Fragment {

    private RecyclerView productRCV;
    private TextView emptyView;
    private ImageView NoDataImage;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelProduct> productList;
    private AdapterProductSeller adapterProductSeller;
    private AdapterCart adapterCart;
    private ProgressBar progressBar;

    public ProductsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        productRCV = view.findViewById(R.id.product_rcv);
        emptyView = view.findViewById(R.id.empty_view);
        NoDataImage = view.findViewById(R.id.No_data_image);
        progressBar = view.findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.VISIBLE);
        loadAllProducts();

        return view;
    }

    private void loadAllProducts()
    {
        productList = new ArrayList<>();

        //get all products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
                    {
                        //before getting reset list
                        productList.clear();
                        for (DataSnapshot ds: snapshot.getChildren())
                        {
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        //set adapter
                        adapterProductSeller = new AdapterProductSeller(getContext(), productList);
                        //set adapter
                        productRCV.setAdapter(adapterProductSeller);
                        progressBar.setVisibility(View.GONE);

                        if (productList.isEmpty()) {
                            productRCV.setVisibility(View.GONE);
                            NoDataImage.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        else {
                            productRCV.setVisibility(View.VISIBLE);
                            NoDataImage.setVisibility(View.GONE);
                            emptyView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error)
                    {

                    }
                });
    }
}
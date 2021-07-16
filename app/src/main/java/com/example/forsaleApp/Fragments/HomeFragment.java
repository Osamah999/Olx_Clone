package com.example.forsaleApp.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.forsaleApp.AdapterProductSeller;
import com.example.forsaleApp.AddActivity;
import com.example.forsaleApp.Constants;
import com.example.forsaleApp.HomeActivity;
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
import java.util.Objects;


public class HomeFragment extends Fragment {

    private EditText searchProduct;
    private ImageButton filterbtn;
    private TextView filtertxt;
    private RecyclerView productRCV;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelProduct> productList;
    private AdapterProductSeller adapterProductSeller;

    public HomeFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        searchProduct = view.findViewById(R.id.search_product);
        filterbtn = view.findViewById(R.id.filter_btn);
        filtertxt = view.findViewById(R.id.filter_producttxt);
        productRCV = view.findViewById(R.id.product_rcv);

        firebaseAuth = FirebaseAuth.getInstance();

        loadAllProducts();

        //search
        searchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterProductSeller.getFilter().filter(charSequence);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        filterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Category :")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //get selected item
                                String selected = Constants.productCategories1[i];
                                filtertxt.setText(selected);
                                if (selected.equals("All"))
                                {
                                    //load all
                                    loadAllProducts();
                                }
                                else
                                {
                                    //load filtered
                                    loadFilteredProducts(selected);
                                }

                            }
                        })
                .show();
            }
        });

        return view;
    }

    private void loadFilteredProducts(String selected)
    {
        productList = new ArrayList<>();

        //get all products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(Objects.requireNonNull(firebaseAuth.getUid())).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
                    {
                        //before getting reset list
                        productList.clear();
                        for (DataSnapshot ds: snapshot.getChildren())
                        {
                            String productCategory = ""+ds.child("ProductCategory").getValue();

                            //if selected category matches product category the add in list
                            if (selected.equals(productCategory))
                            {
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }

                        }
                        //set adapter
                        adapterProductSeller = new AdapterProductSeller(getContext().getApplicationContext(), productList);
                        //set adapter
                        productRCV.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error)
                    {

                    }
                });
    }

    private void loadAllProducts()
    {
        productList = new ArrayList<>();

        //get all products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(Objects.requireNonNull(firebaseAuth.getUid())).child("Products")
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
                        adapterProductSeller = new AdapterProductSeller(getContext().getApplicationContext(), productList);
                        //set adapter
                        productRCV.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error)
                    {

                    }
                });
    }
}
package com.example.forsaleApp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.forsaleApp.ModelProduct;
import com.example.forsaleApp.R;
import com.example.forsaleApp.Adapters.AdapterChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class ChatFragment extends Fragment {

    private RecyclerView Recycler_view;
    private TextView emptyView;
    private ImageView NoDataImage;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    public ArrayList<ModelProduct> productList;
    public AdapterChat adapterChat;

    private String UserName, UserId;

    public ChatFragment(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        Recycler_view = view.findViewById(R.id.recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        NoDataImage = view.findViewById(R.id.No_data_image);
        progressBar = view.findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.VISIBLE);

        Recycler_view.setHasFixedSize(true);
        Recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));


        loadAllUsers();


        return  view;
    }

    private void loadAllUsers()
    {
        productList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid()).child("Chat")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
                    {
                        productList.clear();
                        for (DataSnapshot ds: snapshot.getChildren())
                        {
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        //set adapter
                        adapterChat = new AdapterChat(getContext(), productList);
                        //set adapter
                        Recycler_view.setAdapter(adapterChat);
                        progressBar.setVisibility(View.GONE);

                        if (productList.isEmpty()) {
                            Recycler_view.setVisibility(View.GONE);
                            NoDataImage.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        else {
                            Recycler_view.setVisibility(View.VISIBLE);
                            NoDataImage.setVisibility(View.GONE);
                            emptyView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

    }
}
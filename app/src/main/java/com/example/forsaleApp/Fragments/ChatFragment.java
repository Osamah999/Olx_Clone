package com.example.forsaleApp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.forsaleApp.AdapterHomeProducts;
import com.example.forsaleApp.ModelProduct;
import com.example.forsaleApp.R;
import com.example.forsaleApp.UserAdapter;
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

    private FirebaseAuth firebaseAuth;

    public ArrayList<ModelProduct> productList;
    public UserAdapter userAdapter;

    public ChatFragment(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        Recycler_view = view.findViewById(R.id.recycler_view);

        firebaseAuth = FirebaseAuth.getInstance();

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
                        userAdapter = new UserAdapter(getContext().getApplicationContext(), productList);
                        //set adapter
                        Recycler_view.setAdapter(userAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

    }
}
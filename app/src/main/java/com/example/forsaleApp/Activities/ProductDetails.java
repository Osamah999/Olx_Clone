package com.example.forsaleApp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forsaleApp.Fragments.ChatFragment;
import com.example.forsaleApp.R;
import com.example.forsaleApp.Utility.NetworkChangeListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetails extends AppCompatActivity implements OnMapReadyCallback{

    private TextView Product_Name, Product_Description, Product_Price, Product_Location, User_Name, date;
    private ImageView Product_Image;
    private Button Chat_btn, Favorite_btn;

    private String ProductId, ProductImage, Latitude, Longitude, UserId,UserName;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private String Description, Price, Pname, Location, ProductDate, userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Product_Image = findViewById(R.id.product_image);
        Product_Name = findViewById(R.id.product_name);
        Product_Description = findViewById(R.id.product_description);
        Product_Price = findViewById(R.id.product_price);
        Product_Location = findViewById(R.id.location_txt);
        User_Name = findViewById(R.id.user_name);
        date = findViewById(R.id.date);
        Chat_btn = findViewById(R.id.chat_btn);
        Favorite_btn = findViewById(R.id.favorite_btn);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googel_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //get id of the product from intent
        ProductId = getIntent().getStringExtra("ProductId");
        ProductImage = getIntent().getStringExtra("ProductImage");
        Latitude = getIntent().getStringExtra("Latitude");
        Longitude = getIntent().getStringExtra("Longitude");
        UserId = getIntent().getStringExtra("UserId");
        UserName = getIntent().getStringExtra("UserName");

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        loadProductDetails();

        Favorite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AddToCart();
            }
        });

        Chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Chat();
            }
        });
    }

    private void loadProductDetails()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Products").child(ProductId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
                    {
                        //get data
                        String ProductId = ""+snapshot.child("ProductId").getValue();
                        String ProductImage = ""+snapshot.child("ProductImage").getValue();
                        String ProductName = ""+snapshot.child("ProductName").getValue();
                        String ProductDescription = ""+snapshot.child("ProductDescription").getValue();
                        String ProductPrice = ""+snapshot.child("ProductPrice").getValue();
                        String Location = ""+snapshot.child("Location").getValue();
                        String Date = ""+snapshot.child("Date").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String UserId = ""+snapshot.child("UserId").getValue();
                        String UserName = ""+snapshot.child("UserName").getValue();


                        //set data to views

                        Product_Name.setText(ProductName);
                        Product_Description.setText(ProductDescription);
                        Product_Price.setText("₹"+ProductPrice);
                        Product_Location.setText(Location);
                        User_Name.setText(UserName);
                        date.setText(Date);

                        try {
                            Picasso.get().load(ProductImage).placeholder(R.drawable.add_image).into(Product_Image);
                        }
                        catch (Exception e)
                        {
                            Product_Image.setImageResource(R.drawable.add_image);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }

                });
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Products").child(ProductId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        //get data
                        String lat = snapshot.child("Latitude").getValue().toString();
                        String lng = snapshot.child("Longitude").getValue().toString();

                        double latitude =Double.parseDouble(lat);
                        double longitude =Double.parseDouble(lng);

                        //Initialize lat and lng
                        LatLng latLng = new LatLng(latitude,longitude);
                        //create marker options
                        MarkerOptions options = new MarkerOptions().position(latLng);
                        //zoom map
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));
                        //add marker on map
                        googleMap.addMarker(options);

                    }
                    @Override
                    public void onCancelled (@NonNull @NotNull DatabaseError error){

                    }
                });
    }

    private void AddToCart()
    {
        progressDialog.setTitle("Add to favorite");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait we are adding the product...");
        progressDialog.show();

        Pname = Product_Name.getText().toString().trim();
        Description = Product_Description.getText().toString().trim();
        Price = Product_Price.getText().toString().trim();
        Location = Product_Location.getText().toString().trim();
        userName = User_Name.getText().toString().trim();
        ProductDate = date.getText().toString().trim();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ProductId", "" + ProductId);
        hashMap.put("ProductImage", ""+ProductImage);
        hashMap.put("ProductName", "" + Pname);
        hashMap.put("ProductDescription", "" + Description);
        hashMap.put("ProductPrice", "" + Price);
        hashMap.put("Latitude", "" + Latitude);
        hashMap.put("Longitude", "" + Longitude);
        hashMap.put("Location", "" + Location);
        hashMap.put("Date", "" + ProductDate);
        hashMap.put("UserId", "" + UserId);
        hashMap.put("UserName", ""+ userName);
        //add to db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid()).child("Cart").child(ProductId).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(ProductDetails.this, "Product added successfully!", Toast.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        //failed adding to db
                        progressDialog.dismiss();
                    }
                });
    }


    private void Chat()
    {
        progressDialog.setTitle("Add to favorite");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait we are adding the product...");
        progressDialog.show();

        Pname = Product_Name.getText().toString().trim();
        Description = Product_Description.getText().toString().trim();
        Price = Product_Price.getText().toString().trim();
        Location = Product_Location.getText().toString().trim();
        userName = User_Name.getText().toString().trim();
        ProductDate = date.getText().toString().trim();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ProductId", "" + ProductId);
        hashMap.put("Receiver", "" + UserId);
        hashMap.put("Sender", "" + firebaseAuth.getUid());
        hashMap.put("UserName", ""+ userName);
        //add to db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid()).child("Chat").child(ProductId).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(ProductDetails.this, "Product added successfully!", Toast.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        //failed adding to db
                        progressDialog.dismiss();
                    }
                });

    }

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }


}

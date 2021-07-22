package com.example.forsaleApp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.forsaleApp.R;
import com.example.forsaleApp.Utility.NetworkChangeListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class ProductDetails extends AppCompatActivity implements OnMapReadyCallback{

    private TextView Product_Name, Product_Description, Product_Price, Product_Location;
    private ImageView Product_Image;
    private Button Chat_btn, Favorite_btn;

    private String ProductId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Product_Image = findViewById(R.id.product_image);
        Product_Name = findViewById(R.id.product_name);
        Product_Description = findViewById(R.id.product_description);
        Product_Price = findViewById(R.id.product_price);
        Product_Location = findViewById(R.id.location_txt);
        Chat_btn = findViewById(R.id.chat_btn);
        Favorite_btn = findViewById(R.id.favorite_btn);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googel_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //get id of the product from intent
        ProductId = getIntent().getStringExtra("ProductId");

        loadProductDetails();
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


                        //set data to views
                        Product_Name.setText(ProductName);
                        Product_Description.setText(ProductDescription);
                        Product_Price.setText("₹"+ProductPrice);
                        Product_Location.setText(Location);

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

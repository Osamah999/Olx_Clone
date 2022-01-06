package com.example.forsaleApp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.forsaleApp.Constants;
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
import com.google.android.material.textfield.TextInputEditText;
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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EditProduct extends AppCompatActivity implements LocationListener, OnMapReadyCallback{

    ImageView product_image;
    TextInputEditText product_name, product_category, product_description, product_price;
    TextInputEditText country_ad, state_ad, city_ad, location_ad;
    Button update_product, get_location, delete_product;

    private String Description, Price, Pname, productCategory, Country, State, City, Location;

    private String ProductId;

    private static final int GalleryPick = 1;
    private static final int CameraPick = 3;
    private Uri ImageUri;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private SupportMapFragment supportMapFragment;
    private LocationManager locationManager;
    private double latitude, longitude;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        product_image = findViewById(R.id.product_image);
        product_name = findViewById(R.id.product_name);
        product_category = findViewById(R.id.category);
        product_description = findViewById(R.id.product_description);
        product_price = findViewById(R.id.product_price);
        country_ad = findViewById(R.id.country_txt);
        state_ad = findViewById(R.id.state_txt);
        city_ad = findViewById(R.id.city_txt);
        location_ad = findViewById(R.id.location_txt);
        update_product = findViewById(R.id.update_product);
        delete_product = findViewById(R.id.delete_product);
        get_location = findViewById(R.id.get_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googel_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //get id of the product from intent
        ProductId = getIntent().getStringExtra("ProductId");

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        loadProductDetails();

        product_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (ActivityCompat.checkSelfPermission(EditProduct.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.checkSelfPermission(EditProduct.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    {
                        //When permission grated
                        //call method
                        ShowImagePickDialog();;
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(EditProduct.this, new String[]{Manifest.permission.CAMERA}, 3);
                    }

                }
                else
                {
                    //when permission denied
                    //Request permission
                    ActivityCompat.requestPermissions(EditProduct.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                }
            }

        });

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googel_map);

        if (ActivityCompat.checkSelfPermission(EditProduct.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(EditProduct.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }

        get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }

        });

        update_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });

        delete_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                deleteDialog();
            }

        });

        product_category.setFocusable(false);
        product_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pick category
                categoryDialog();
            }
        });

    }

    private void loadProductDetails()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid()).child("Products").child(ProductId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
                    {
                        //get data
                        String ProductId = ""+snapshot.child("ProductId").getValue();
                        String ProductImage = ""+snapshot.child("ProductImage").getValue();
                        String ProductName = ""+snapshot.child("ProductName").getValue();
                        String ProductCategory = ""+snapshot.child("ProductCategory").getValue();
                        String ProductDescription = ""+snapshot.child("ProductDescription").getValue();
                        String ProductPrice = ""+snapshot.child("ProductPrice").getValue();
                        String Latitude = ""+snapshot.child("Latitude").getValue();
                        String Longitude = ""+snapshot.child("Longitude").getValue();
                        String Country = ""+snapshot.child("Country").getValue();
                        String State = ""+snapshot.child("State").getValue();
                        String City = ""+snapshot.child("City").getValue();
                        String Location = ""+snapshot.child("Location").getValue();
                        String Date = ""+snapshot.child("Date").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String UserId = ""+snapshot.child("UserId").getValue();


                        //set data to views
                        product_name.setText(ProductName);
                        product_category.setText(ProductCategory);
                        product_description.setText(ProductDescription);
                        product_price.setText(ProductPrice);
                        country_ad.setText(Country);
                        state_ad.setText(State);
                        city_ad.setText(City);
                        location_ad.setText(Location);

                        try {
                            Picasso.get().load(ProductImage).placeholder(R.drawable.add_image).into(product_image);
                        }
                        catch (Exception e)
                        {
                            product_image.setImageResource(R.drawable.add_image);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void categoryDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(Constants.productCategories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        //get picked category
                        String category = Constants.productCategories[i];

                        //set picked category
                        product_category.setText(category);

                    }
                })
                .show();
    }

    private void ShowImagePickDialog()
    {
        String[] options = {"Camera", "Gallery"};
        //Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (i == 0)
                        {
                            PickFromCamera();
                        }
                        else
                        {
                            OpenGallery();
                        }
                    }
                })
                .show();
    }

    private void PickFromCamera()
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image_Description");

        ImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUri);
        startActivityForResult(intent, CameraPick);

    }

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Pictures"), GalleryPick);

    }

    private void ValidateProductData()
    {
        Pname = product_name.getText().toString().trim();
        productCategory = product_category.getText().toString().trim();
        Description = product_description.getText().toString().trim();
        Price = product_price.getText().toString().trim();
        Country = country_ad.getText().toString().trim();
        State = state_ad.getText().toString().trim();
        City = city_ad.getText().toString().trim();
        Location = location_ad.getText().toString().trim();


        if (TextUtils.isEmpty(Pname))
        {
            product_name.setError("Please write product name!");
            product_name.requestFocus();
        }
        else if (TextUtils.isEmpty(productCategory))
        {
            product_name.setError("Please select product category!");
            product_name.requestFocus();
        }
        else if (TextUtils.isEmpty(Description))
        {
            product_description.setError("Please write product description!");
            product_description.requestFocus();
        }
        else if (TextUtils.isEmpty(Price))
        {
            product_price.setError("Please write your price!");
            product_price.requestFocus();
        }
        else if (TextUtils.isEmpty(Country))
        {
            country_ad.setError("Please write your country name!");
            country_ad.requestFocus();
        }
        else if (TextUtils.isEmpty(State))
        {
            state_ad.setError("Please write your state name!");
            state_ad.requestFocus();
        }
        else if (TextUtils.isEmpty(City))
        {
            city_ad.setError("Please write your city name!");
            city_ad.requestFocus();
        }
        else if (TextUtils.isEmpty(Location))
        {
            location_ad.setError("Please write your location!");
            location_ad.requestFocus();
        }
        else
        {
            updateProduct();
        }
    }

    private void updateProduct()
    {
        progressDialog.setTitle("Update Product");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait we are updating the product...");
        progressDialog.show();

        if (ImageUri == null)
        {
            //update without image

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("ProductName", "" + Pname);
            hashMap.put("ProductCategory", "" + productCategory);
            hashMap.put("ProductDescription", "" + Description);
            hashMap.put("ProductPrice", "" + Price);
            //hashMap.put("Latitude", "" + latitude);
            //hashMap.put("Longitude", "" + longitude);
            hashMap.put("Country", "" + Country);
            hashMap.put("State", "" + State);
            hashMap.put("City", "" + City);
            hashMap.put("Location", "" + Location);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
            reference.child(firebaseAuth.getUid()).child("Products").child(ProductId)
                    .updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProduct.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Products").child(ProductId)
                    .updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused)
                        {
                            progressDialog.dismiss();
                            Intent intent = new Intent(EditProduct.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Toast.makeText(EditProduct.this, "Product details updated successfully", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProduct.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
        else
        {
            //update with image

            //first upload image
            //image name and path on firebase storage
            String filePathAndName = "product_images/" + "" + ProductId;//override previous image using same id
            //upload image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(ImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            //image uploaded, get url of uploaded image
                            Task<Uri>uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful())
                            {
                                //setup data in hashmap to update
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("ProductImage", "" + downloadImageUri);
                                hashMap.put("ProductName", "" + Pname);
                                hashMap.put("ProductCategory", "" + productCategory);
                                hashMap.put("ProductDescription", "" + Description);
                                hashMap.put("ProductPrice", "" + Price);
                                //hashMap.put("Latitude", "" + latitude);
                                //hashMap.put("Longitude", "" + longitude);
                                hashMap.put("Country", "" + Country);
                                hashMap.put("State", "" + State);
                                hashMap.put("City", "" + City);
                                hashMap.put("Location", "" + Location);

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                                reference.child(firebaseAuth.getUid()).child("Products").child(ProductId)
                                        .updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused)
                                            {

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull @NotNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(EditProduct.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("Products").child(ProductId)
                                        .updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused)
                                            {
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(EditProduct.this, HomeActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                Toast.makeText(EditProduct.this, "Product details updated successfully", Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull @NotNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(EditProduct.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e)
                        {
                            //upload failed
                            progressDialog.dismiss();
                            Toast.makeText(EditProduct.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void getCurrentLocation()
    {

        progressDialog.setTitle("Getting location");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid()).child("Products").child(ProductId)
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

    @Override
    public void onLocationChanged(@NonNull Location location)
    {
        //location detected
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull @NotNull GoogleMap googleMap)
            {
                //Initialize lat and lng
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                //create marker options
                MarkerOptions options = new MarkerOptions().position(latLng).title("You are here");
                //zoom map
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));
                //add marker on map
                googleMap.addMarker(options);
            }
        });

        findAddress();
        locationManager.removeUpdates(this);
    }

    private void findAddress()
    {
        //find address country ,state, city
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String address = addresses.get(0).getAddressLine(0);//complete address
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String county = addresses.get(0).getCountryName();

            //set address
            country_ad.setText(county);
            state_ad.setText(state);
            city_ad.setText(city);
            location_ad.setText(address);

            progressDialog.dismiss();

        }
        catch (Exception e)
        {
            progressDialog.dismiss();
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onProviderDisabled(@NonNull String provider)
    {
        //gps / location disable
        progressDialog.dismiss();
        Toast.makeText(this, "Please turn on location!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK)
        {
            ImageUri = data.getData();
            product_image.setImageURI(ImageUri);
        }
        if (requestCode == CameraPick && resultCode == RESULT_OK)
        {
            //ImageUri = data.getData();
            product_image.setImageURI(ImageUri);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AlertDialog.Builder alertDialog;

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                }
                else
                {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    alertDialog = new AlertDialog.Builder(EditProduct.this);
                    alertDialog.setTitle("Location permission is required");
                    alertDialog.setMessage("Do you want to open app settings");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String packageName = "com.example.forsaleApp";
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + packageName));
                            startActivity(intent);
                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditProduct.this.finish();
                        }
                    });
                    alertDialog.show();
                }
            }
            break;
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(EditProduct.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    {
                        //When permission grated
                        //call method
                        ShowImagePickDialog();
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(EditProduct.this, new String[]{Manifest.permission.CAMERA}, 3);
                    }
                }
                else
                {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    alertDialog = new AlertDialog.Builder(EditProduct.this);
                    alertDialog.setTitle("Storage permission is required");
                    alertDialog.setMessage("Do you want to open app settings");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String packageName = "com.example.forsaleApp";
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + packageName));
                            startActivity(intent);
                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            // AddActivity.this.finish();
                        }
                    });
                    alertDialog.show();
                }
            }
            break;
            case 3: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    ShowImagePickDialog();
                }
                else
                {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    alertDialog = new AlertDialog.Builder(EditProduct.this);
                    alertDialog.setTitle("camera permission is required");
                    alertDialog.setMessage("Do you want to open app settings");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String packageName = "com.example.forsaleApp";
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + packageName));
                            startActivity(intent);
                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    alertDialog.show();
                }
            }
            break;

        }
    }

    private void deleteDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditProduct.this);
        alertDialog = new AlertDialog.Builder(EditProduct.this);
        alertDialog.setTitle("Delete");
        alertDialog.setMessage("Are you sure you want to delete this product?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteProduct(ProductId);//id is the product id
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void deleteProduct(String productId)
    {
        ProgressDialog progressDialog = new ProgressDialog(EditProduct.this);
        progressDialog.setTitle("delete Product");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait we are deleting the product...");
        progressDialog.show();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid()).child("Products").child(productId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused)
                    {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        //failed uploading image
                        progressDialog.dismiss();
                        Toast.makeText(EditProduct.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Products").child(productId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused)
                    {
                        //product deleted
                        progressDialog.dismiss();
                        // EditProduct.this.finish();
                        Intent intent = new Intent(EditProduct.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Toast.makeText(EditProduct.this, "Product deleted successfully", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e)
                    {
                        //failed uploading image
                        progressDialog.dismiss();
                        Toast.makeText(EditProduct.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) ;
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
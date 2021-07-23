package com.example.forsaleApp.Activities;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.graphics.Bitmap;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.forsaleApp.Constants;
import com.example.forsaleApp.R;
import com.example.forsaleApp.User;
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
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class AddActivity extends AppCompatActivity implements LocationListener {

    ImageView product_image;
    TextView user_name;
    TextInputEditText product_name, product_category, product_description, product_price;
    TextInputEditText country_ad, state_ad, city_ad, location_ad;
    Button add_product, get_location;

    private String Description, Price, Pname, productCategory, Country, State, City, Location, saveCurrentDate, userName;

    private static final int GalleryPick = 1;
    private static final int CameraPick = 3;
    private Uri ImageUri;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private SupportMapFragment supportMapFragment;
    private LocationManager locationManager;
    private double latitude, longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        product_image = findViewById(R.id.product_Image);
        product_name = findViewById(R.id.product_name);
        product_category = findViewById(R.id.category);
        product_description = findViewById(R.id.product_description);
        product_price = findViewById(R.id.product_price);
        country_ad = findViewById(R.id.country_txt);
        state_ad = findViewById(R.id.state_txt);
        city_ad = findViewById(R.id.city_txt);
        location_ad = findViewById(R.id.location_txt);
        add_product = findViewById(R.id.add_product);
        get_location = findViewById(R.id.get_location);
        user_name = findViewById(R.id.user_name);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("users");
        databaseReference1.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
                    {
                        String UserName = ""+snapshot.child("name").getValue();

                        user_name.setText(UserName);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    {
                        //When permission grated
                        //call method
                        ShowImagePickDialog();
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.CAMERA}, 3);
                    }

                }
                else
                {
                    //when permission denied
                    //Request permission
                    ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                }
            }

        });

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googel_map);

        //check location permission
        if (ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }

        get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }

        });

        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
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

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Pictures"), GalleryPick);

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
        userName = user_name.getText().toString().trim();


        if (ImageUri == null)
        {
            Toast.makeText(this, "Product image is required!", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(Pname))
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
        else if (latitude == 0.0 || longitude == 0.0)
        {
            Toast.makeText(this, "Please click on get current location to detect your location!", Toast.LENGTH_LONG).show();
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
            StoreProductInformation();
        }
    }

    private void StoreProductInformation()
    {
        progressDialog.setTitle("Add New Product");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait we are adding the product...");
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        String timestamp = "" + System.currentTimeMillis();

        String filePathAndName = "product_images/" + "" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(ImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image uploaded
                        //get url of uploaded image
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadImageUri = uriTask.getResult();

                        if (uriTask.isSuccessful()) {

                            //url of image received, upload to firebase
                            //set data to upload
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("ProductId", "" + timestamp);
                            hashMap.put("ProductImage", "" + downloadImageUri);
                            hashMap.put("ProductName", "" + Pname);
                            hashMap.put("ProductCategory", "" + productCategory);
                            hashMap.put("ProductDescription", "" + Description);
                            hashMap.put("ProductPrice", "" + Price);
                            hashMap.put("Latitude", "" + latitude);
                            hashMap.put("Longitude", "" + longitude);
                            hashMap.put("Country", "" + Country);
                            hashMap.put("State", "" + State);
                            hashMap.put("City", "" + City);
                            hashMap.put("Location", "" + Location);
                            hashMap.put("Date", "" + saveCurrentDate);
                            hashMap.put("timestamp", "" + timestamp);
                            hashMap.put("UserId", "" + firebaseAuth.getUid());
                            hashMap.put("UserName", ""+ userName);
                            //add to db
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                            reference.child(firebaseAuth.getUid()).child("Products").child(timestamp).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            //failed adding to db
                                            progressDialog.dismiss();
                                            Toast.makeText(AddActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("Products").child(timestamp).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            progressDialog.dismiss();
                                            Toast.makeText(AddActivity.this, "Product added successfully", Toast.LENGTH_LONG).show();
                                            clearData();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            //failed adding to db
                                            progressDialog.dismiss();
                                            Toast.makeText(AddActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        //failed uploading image
                        progressDialog.dismiss();
                        Toast.makeText(AddActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void clearData()
    {
        //clear data after uploading product
        product_name.setText("");
        product_category.setText("");
        product_description.setText("");
        product_price.setText("");
        country_ad.setText("");
        state_ad.setText("");
        city_ad.setText("");
        location_ad.setText("");
        product_image.setImageResource(R.drawable.add_image);
        product_image = null;

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
    public void onLocationChanged(@NonNull Location location)
    {
        //location detected
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        if (location != null)
        {
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
        }

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
                    alertDialog = new AlertDialog.Builder(AddActivity.this);
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
                            AddActivity.this.finish();
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
                    if (ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    {
                        //When permission grated
                        //call method
                        ShowImagePickDialog();
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.CAMERA}, 3);
                    }
                }
                else
                {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    alertDialog = new AlertDialog.Builder(AddActivity.this);
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
                    alertDialog = new AlertDialog.Builder(AddActivity.this);
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
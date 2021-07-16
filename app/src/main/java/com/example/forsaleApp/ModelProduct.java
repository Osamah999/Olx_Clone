package com.example.forsaleApp;

public class ModelProduct
{
    private String ProductId, ProductImage, ProductName, ProductCategory, ProductDescription, ProductPrice,
            Latitude, Longitude, Country, State, City, Location, Date, timestamp, UserId;

    public ModelProduct()
    {

    }

    public ModelProduct(String productId, String productImage, String productName, String productCategory,
                        String productDescription, String productPrice, String latitude, String longitude,
                        String country, String state, String city, String location, String date, String timestamp,
                        String userId)
    {
        ProductId = productId;
        ProductImage = productImage;
        ProductName = productName;
        ProductCategory = productCategory;
        ProductDescription = productDescription;
        ProductPrice = productPrice;
        Latitude = latitude;
        Longitude = longitude;
        Country = country;
        State = state;
        City = city;
        Location = location;
        Date = date;
        this.timestamp = timestamp;
        UserId = userId;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getProductImage() {
        return ProductImage;
    }

    public void setProductImage(String productImage) {
        ProductImage = productImage;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductCategory() {
        return ProductCategory;
    }

    public void setProductCategory(String productCategory) {
        ProductCategory = productCategory;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }

    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        ProductPrice = productPrice;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}

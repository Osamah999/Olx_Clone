package com.example.forsaleApp;

public class ModelProduct
{
    private String Name, Email,Password, ProductId, ProductImage, ProductName, ProductCategory, ProductDescription, ProductPrice,
            Latitude, Longitude, Country, State, City, Location, Date, timestamp, UserId,UserName, Uid, Sender, Receiver, Message;

    public ModelProduct()
    {

    }

    public ModelProduct(String name, String email, String password, String productId, String productImage,
                        String productName, String productCategory, String productDescription, String productPrice,
                        String latitude, String longitude, String country, String state, String city, String location,
                        String date, String timestamp, String userId, String userName, String uid, String sender, String receiver, String message)
    {
        Name = name;
        Email = email;
        Password = password;
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
        UserName = userName;
        Uid = uid;
        Sender = sender;
        Receiver = receiver;
        Message = message;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        this.Uid = uid;
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

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}

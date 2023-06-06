package com.example.googlemap;

public class Model {

    String Address, AddressTwo, capacity, discription, Size;
    Double latitude;
    Double longitude;


    public Model() {
    }

    public Model(String address, String addressTwo, String capacity, String discription, String size, Double latitude, Double longitude) {
        Address = address;
        AddressTwo = addressTwo;
        this.capacity = capacity;
        this.discription = discription;
        Size = size;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getAddressTwo() {
        return AddressTwo;
    }

    public void setAddressTwo(String addressTwo) {
        AddressTwo = addressTwo;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
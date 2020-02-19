package com.example.productmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private String encodedPhoto;
    private String name;
    private String code;
    private String description;
    private float price;

    public Product(String encodedPhoto, String name, String code, String description, float price) {
        this.encodedPhoto = encodedPhoto;
        this.name = name;
        this.code = code;
        this.description = description;
        this.price = price;
    }

    public Product() { }

    public String getEncodedPhoto() {
        return encodedPhoto;
    }

    public void setEncodedPhoto(String encodedPhoto) {
        this.encodedPhoto = encodedPhoto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.encodedPhoto);
        dest.writeString(this.name);
        dest.writeString(this.code);
        dest.writeString(this.description);
        dest.writeFloat(this.price);
    }

    protected Product(Parcel in) {
        this.encodedPhoto = in.readString();
        this.name = in.readString();
        this.code = in.readString();
        this.description = in.readString();
        this.price = in.readFloat();
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}

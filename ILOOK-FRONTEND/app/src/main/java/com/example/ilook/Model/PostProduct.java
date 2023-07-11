package com.example.ilook.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class PostProduct implements Parcelable {

    String category;
    String brand;
    String name;
    String size;

    public PostProduct(String category, String brand, String name, String size){
        this.category = category;
        this.brand = brand;
        this.name = name;
        this.size = size;
    }

    protected PostProduct(Parcel in) {
        category = in.readString();
        brand = in.readString();
        name = in.readString();
        size = in.readString();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public static final Creator<PostProduct> CREATOR = new Creator<PostProduct>() {
        @Override
        public PostProduct createFromParcel(Parcel in) {
            return new PostProduct(in);
        }

        @Override
        public PostProduct[] newArray(int size) {
            return new PostProduct[size];
        }
    };

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(category);
        parcel.writeString(brand);
        parcel.writeString(name);
        parcel.writeString(size);
    }
}

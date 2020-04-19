package com.example.serwe.Model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressLint("ParcelCreator")
public class Category implements Parcelable {
    private String Name, Image, address, Description, Phone, Email;
    private long Table;
    private  double Lat;
    private  double Long, Rating;


    public Category() {
    }

    public Category(String name, String image, String address, String description, String phone, String email, long table, double lat, double aLong, double rating) {
        Name = name;
        Image = image;
        this.address = address;
        Description = description;
        Phone = phone;
        Email = email;
        Table = table;
        Lat = lat;
        Long = aLong;
        Rating = rating;
    }

    protected Category(Parcel in) {
        Name = in.readString();
        Image = in.readString();
        address = in.readString();
        Description = in.readString();
        Phone = in.readString();
        Email = in.readString();
        Table = in.readLong();
        Lat = in.readDouble();
        Long = in.readDouble();
        Rating = in.readDouble();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public double getRating() {
        return Rating;
    }

    public void setRating(double rating) {
        Rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public long getTable() {
        return Table;
    }

    public void setTable(long table) {
        Table = table;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLong() {
        return Long;
    }

    public void setLong(double aLong) {
        Long = aLong;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(Image);
        dest.writeString(address);
        dest.writeString(Description);
        dest.writeString(Phone);
        dest.writeString(Email);
        dest.writeLong(Table);
        dest.writeDouble(Lat);
        dest.writeDouble(Long);
        dest.writeDouble(Rating);
    }
}

package com.example.serwe.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {
    private String Name;
    private String Image;
    private long Table;
    private  double Lat;
    private  double Long;

    public Category() {
    }

    protected Category(Parcel in) {
        Name = in.readString();
        Image = in.readString();
        Table = in.readLong();
        Lat = in.readDouble();
        Long = in.readDouble();
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

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        this.Lat = lat;
    }

    public double getLong() {
        return Long;
    }

    public void setLong(double aLong) {
        this.Long = aLong;
    }

    public Category(String name, String image, long table, double Lat, double Long) {
        Name = name;
        Image = image;
        Table = table;
        this.Lat = Lat;
        this.Long = Long;
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

    public long getTable() {
        return Table;
    }

    public void setTable(long table) {
        Table = table;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(Image);
        dest.writeLong(Table);
        dest.writeDouble(Lat);
        dest.writeDouble(Long);
    }
}

package com.example.serwe.Model;

public class Category {
    private String Name;
    private String Image;
    private long Table;
    private  double Lat;
    private  double Long;

    public Category() {
    }

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
}

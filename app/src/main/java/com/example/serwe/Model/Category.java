package com.example.serwe.Model;

public class Category {
    private String Name;
    private String Image;
    private long Table;
    private  double latitude;
    private  double longitude;

    public Category() {
    }

    public Category(String name, String image, long table, double lat) {
        Name = name;
        Image = image;
        Table = table;
        this.latitude = lat;
    }

    public String getName() {
        return Name;
    }

    public double getLattitude() {
        return latitude;
    }

    public void setLattitude(double lattitude) {
        this.latitude = lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

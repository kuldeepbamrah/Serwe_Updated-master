package com.example.serwe.Model;

public class Category {
    private String Name;
    private String Image;
    private long Table;

    public Category() {
    }

    public Category(String name, String image,long table) {
        Name = name;
        Image = image;
        Table = table;
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

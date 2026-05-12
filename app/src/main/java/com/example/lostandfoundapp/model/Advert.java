package com.example.lostandfoundapp.model;

// Class representing one lost or found advert.
public class Advert {
    private int id;
    private String postType, name, phone, description, location, category, imageUri, dateTime;

    public Advert(int id, String postType, String name, String phone, String description, String location, String category, String imageUri, String dateTime) {
        this.id = id;
        this.postType = postType;
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.location = location;
        this.category = category;
        this.imageUri = imageUri;
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public String getPostType() {
        return postType;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getDateTime() {
        return dateTime;
    }

    // A user-friendly title for the advert.
    public String getTitle() {
        return postType + ": " + name;
    }
}

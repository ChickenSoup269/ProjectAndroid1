package com.example.movieapp;

public class User {
    public String id;
    public String username;
    public String email;
    public String password;
    public String userBackground;
    public String userImg;
    public String phone;
    public String sex;
    public String dateCreated;

    public User() {

    }

    public User(String id, String username, String email, String password ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserBackground() {
        return userBackground;
    }

    public String getUserImg() {
        return userImg;
    }

    public String getPhone() {
        return phone;
    }

    public String getSex() {
        return sex;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserBackground(String userBackground) {
        this.userBackground = userBackground;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}

package com.example.mycontacts.model;

public class User {
    String user_id;
    String user_name;
    String user_email;
    String user_phone;
    String user_password;
    String user_profile;

    public User() {
    }

    public User(String user_id, String user_name, String user_email, String user_phone, String user_password, String user_profile) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_phone = user_phone;
        this.user_password = user_password;
        this.user_profile = user_profile;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public String getUser_password() {
        return user_password;
    }

    public String getUser_profile() {
        return user_profile;
    }
}

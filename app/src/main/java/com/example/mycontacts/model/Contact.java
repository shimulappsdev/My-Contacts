package com.example.mycontacts.model;

public class Contact {

    String contact_id;
    String contact_name;
    String contact_email;
    String contact_phone;
    String contact_des;
    String contact_image;

    public Contact(String contact_id, String contact_name, String contact_email, String contact_phone, String contact_des, String contact_image) {
        this.contact_id = contact_id;
        this.contact_name = contact_name;
        this.contact_email = contact_email;
        this.contact_phone = contact_phone;
        this.contact_des = contact_des;
        this.contact_image = contact_image;
    }

    public Contact() {

    }

    public String getContact_id() {
        return contact_id;
    }

    public String getContact_name() {
        return contact_name;
    }

    public String getContact_email() {
        return contact_email;
    }

    public String getContact_phone() {
        return contact_phone;
    }

    public String getContact_des() {
        return contact_des;
    }

    public String getContact_image() {
        return contact_image;
    }
}

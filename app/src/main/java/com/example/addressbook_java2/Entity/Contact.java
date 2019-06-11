package com.example.addressbook_java2.Entity;

import java.io.Serializable;

public class Contact implements Serializable {

    private static final long serialVersionUID=1L;

    private String Name;
    private String Phonenumber;
    private String Address;
    private String BelongUser;

    public Contact(String name, String phonenumber, String address, String belongUser) {
        Name = name;
        Phonenumber = phonenumber;
        Address = address;
        BelongUser = belongUser;
    }

    public Contact() {
    }

    public String getName() {
        return Name;
    }

    public String getPhonenumber() {
        return Phonenumber;
    }

    public String getAddress() {
        return Address;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setPhonenumber(String phonenumber) {
        Phonenumber = phonenumber;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getBelongUser() {
        return BelongUser;
    }

    public void setBelongUser(String belongUser) {
        BelongUser = belongUser;
    }
}

package com.injent.miscalls.data;

public class User {

    public User(String name, String lastName, String middleName, String position) {
        this.name = name;
        this.lastName = lastName;
        this.middleName = middleName;
        this.position = position;
    }

    private String name;

    private String lastName;

    private String middleName;

    private String position;

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getFullName() {
        return lastName + " " + name + " " + middleName;
    }

    public String getPosition() {
        return position;
    }

}

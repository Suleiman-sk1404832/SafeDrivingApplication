package qa.edu.qu.cmps312.safedrivingapplication.models;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Driver {
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String userName;
    private String password;
    private Car userCar;
    private Trip trip;
    private ArrayList<String> contacts;
    private LatLng position;
    private String type;


    public Driver() {
    }

    public Driver(String firstName, String lastName, String dateOfBirth, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.userName = userName;
        this.password = password;
        this.contacts = new ArrayList<String>();
        this.contacts.add("Test1");
        this.contacts.add("Test2");
        this.contacts.add("Test3");
        this.position = new LatLng(0.1, 0.1);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Car getUserCar() {
        return userCar;
    }

    public void setUserCar(Car car) {
        this.userCar = car;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }


    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<String> contacts) {
        this.contacts = contacts;
    }
}

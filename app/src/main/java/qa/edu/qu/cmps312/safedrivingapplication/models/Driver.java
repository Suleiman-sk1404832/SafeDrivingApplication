package qa.edu.qu.cmps312.safedrivingapplication.models;

import java.util.ArrayList;

public class Driver {
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String userName;
    private String password;
    private Car userCar;
    private ArrayList<String> Contacts;
    private Double Latitude;
    private Double Longitude;
    private Trip trip;

    public Driver() {
    }

    public Driver(String firstName, String lastName, String dateOfBirth, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.userName = userName;
        this.password = password;
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

    public ArrayList<String> getContacts() {
        return Contacts;
    }

    public void setContacts(ArrayList<String> contacts) {
        Contacts = contacts;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }
}

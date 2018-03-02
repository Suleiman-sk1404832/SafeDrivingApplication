package qa.edu.qu.cmps312.safedrivingapplication.models;

import java.util.ArrayList;

public class Driver {
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String userName;
    private String password;
    private ArrayList<Car> userCarsList;

    public Driver() {
    }

    public Driver(String firstName, String lastName, String dateOfBirth, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.userName = userName;
        this.password = password;
        this.userCarsList = new ArrayList<Car>();
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

    public ArrayList<Car> getUserCarsList() {
        return userCarsList;
    }

    public void setUserCarsList(ArrayList<Car> userCarsList) {
        this.userCarsList = userCarsList;
    }
}

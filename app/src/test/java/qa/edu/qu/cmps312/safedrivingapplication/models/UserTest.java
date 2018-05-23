package qa.edu.qu.cmps312.safedrivingapplication.models;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by Solyman on 5/20/2018.
 */
public class UserTest {
    @Test
    public void getFirstName() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        String expected = "Suleiman";
        String output = user.getFirstName();

        assertEquals(expected, output);
    }

    @Test
    public void setFirstName() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        String expected = "S";
        user.setFirstName("S");
        String output = user.getFirstName();

        assertEquals(expected, output);
    }

    @Test
    public void get666yyLastName() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        String expected = "Kayed";
        String output = user.getLastName();

        assertEquals(expected, output);
    }

    @Test
    public void setLastName() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        String expected = "K";
        user.setLastName("K");
        String output = user.getLastName();

        assertEquals(expected, output);
    }

    @Test
    public void getDateOfBirth() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        String expected = "20/08/2018";
        String output = user.getDateOfBirth();

        assertEquals(expected, output);
    }

    @Test
    public void setDateOfBirth() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        String expected = "01/01/2018";
        user.setDateOfBirth("01/01/2018");
        String output = user.getDateOfBirth();

        assertEquals(expected, output);

    }

    @Test
    public void getUserName() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        String expected = "soly";
        String output = user.getUserName();

        assertEquals(expected, output);
    }

    @Test
    public void setUserName() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        String expected = "s";
        user.setUserName("s");
        String output = user.getUserName();

        assertEquals(expected, output);
    }

    @Test
    public void getPassword() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        String expected = "123";
        String output = user.getPassword();

        assertEquals(expected, output);

    }

    @Test
    public void setPassword() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        String expected = "555";
        user.setPassword("555");
        String output = user.getPassword();

        assertEquals(expected, output);

    }

    @Test
    public void getUserCar() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        Car car = new Car("Toyota", "cruiser", "2016", 500);
        user.setUserCar(car);
        Car expected = car;
        Car output = user.getUserCar();

        assertEquals(expected, output);
    }

    @Test
    public void setUserCar() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        Car car = new Car("Toyota", "cruiser", "2016", 500);
        car.setYear("1999");
        user.setUserCar(car);
        Car expected = car;
        Car output = user.getUserCar();

        assertEquals(expected, output);
    }

    @Test
    public void getTrip() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        Trip trip = new Trip(30, 25, 500, 60);
        user.setTrip(trip);
        Trip expected = trip;
        Trip output = user.getTrip();

        assertEquals(expected, output);
    }

    @Test
    public void setTrip() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        Trip trip = new Trip(30, 25, 500, 60);
        trip.setTotDistanceTraveled(100);
        user.setTrip(trip);
        Trip expected = trip;
        Trip output = user.getTrip();

        assertEquals(expected, output);
    }

    @Test
    public void getLongitude() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        Double expected = 0.1;
        Double output = user.getLongitude();

        assertEquals(expected, output);
    }

    @Test
    public void setLongitude() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        Double expected = 5.5;
        user.setLongitude(5.5);
        Double output = user.getLongitude();

        assertEquals(expected, output);
    }

    @Test
    public void getLatitude() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        Double expected = 0.1;
        Double output = user.getLatitude();

        assertEquals(expected, output);
    }

    @Test
    public void setLatitude() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        Double expected = 10.7;
        user.setLatitude(10.7);
        Double output = user.getLatitude();

        assertEquals(expected, output);
    }

    @Test
    public void getType() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        String expected = "Driver";
        String output = user.getType();

        assertEquals(expected, output);
    }

    @Test
    public void setType() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        String expected = "Boss";
        user.setType("Boss");
        String output = user.getType();

        assertEquals(expected, output);
    }

    @Test
    public void getContacts() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        ArrayList<String> contacts = new ArrayList<>();
        contacts.add("1");
        contacts.add("2");
        user.setContacts(contacts);

        ArrayList<String> expected = contacts;
        ArrayList<String> output = user.getContacts();

        assertEquals(expected, output);
    }

    @Test
    public void setContacts() throws Exception {
        User user = new User("Suleiman", "Kayed", "20/08/2018", "soly", "123");
        ArrayList<String> contacts = new ArrayList<>();
        contacts.add("1");
        contacts.add("2");
        contacts.add("3");
        contacts.add("4");
        user.setContacts(contacts);

        ArrayList<String> expected = contacts;
        ArrayList<String> output = user.getContacts();

        assertEquals(expected, output);
    }

}
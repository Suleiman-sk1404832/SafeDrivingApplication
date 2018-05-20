package qa.edu.qu.cmps312.safedrivingapplication.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Solyman on 5/20/2018.
 */
public class CarTest {

    @Test
    public void getMake() throws Exception {
        Car car = new Car("Toyota", "kia", "2016", 500);
        String expected = "Toyota";
        String output = car.getMake();
        assertEquals(expected, output);
    }

    @Test
    public void setMake() throws Exception {
        Car car = new Car("Toyota", "kia", "2016", 500);
        String expected = "new kia";
        car.setMake("new kia");
        String output = car.getMake();
        assertEquals(expected, output);
    }

    @Test
    public void getModel() throws Exception {
        Car car = new Car("Toyota", "kia", "2016", 500);
        String expected = "kia";
        String output = car.getModel();
        assertEquals(expected, output);
    }

    @Test
    public void setModel() throws Exception {
        Car car = new Car("Toyota", "kia", "2016", 500);
        String expected = "cerato";
        car.setModel("cerato");
        String output = car.getModel();
        assertEquals(expected, output);
    }

    @Test
    public void getYear() throws Exception {
        Car car = new Car("Toyota", "kia", "2016", 500);
        String expected = "2016";
        String output = car.getYear();
        assertEquals(expected, output);
    }

    @Test
    public void setYear() throws Exception {
        Car car = new Car("Toyota", "kia", "2016", 500);
        String expected = "2018";
        car.setYear("2018");
        String output = car.getYear();
        assertEquals(expected, output);
    }

    @Test
    public void getMilage() throws Exception {
        Car car = new Car("Toyota", "kia", "2016", 500);
        int expected = 500;
        int output = car.getMilage();
        assertEquals(expected, output);
    }

    @Test
    public void setMilage() throws Exception {
        Car car = new Car("Toyota", "kia", "2016", 500);
        int expected = 1500;
        car.setMilage(1500);
        int output = car.getMilage();
        assertEquals(expected, output);
    }

}
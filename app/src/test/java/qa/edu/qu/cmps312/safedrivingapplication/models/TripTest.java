package qa.edu.qu.cmps312.safedrivingapplication.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Solyman on 5/20/2018.
 */
public class TripTest {
    @Test
    public void getNoOfTrips() throws Exception {
        Trip trip = new Trip(30, 10, 500, 60);
        int expected = 1;
        int output = trip.getNoOfTrips();

        assertEquals(expected, output);
    }

    @Test
    public void setNoOfTrips() throws Exception {
        Trip trip = new Trip(30, 10, 500, 60);
        int expected = 5;
        trip.setNoOfTrips(5);
        int output = trip.getNoOfTrips();

        assertEquals(expected, output);
    }


}
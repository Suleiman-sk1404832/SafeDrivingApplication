package qa.edu.qu.cmps312.safedrivingapplication.activities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Solyman on 5/20/2018.
 */
public class MainActivityTest {
    @Test
    public void isNotEmpty() throws Exception {
        boolean output;
        boolean expected = false;
        String input = "";
        MainActivity mainActivity = new MainActivity();

        output = mainActivity.isNotEmpty(input);

        assertEquals(expected, output);
    }

}
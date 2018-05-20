package qa.edu.qu.cmps312.safedrivingapplication.fragments;

import org.junit.Test;

import qa.edu.qu.cmps312.safedrivingapplication.activities.MainActivity;

import static org.junit.Assert.assertEquals;

/**
 * Created by Solyman on 5/20/2018.
 */
public class ManageCarFragmentTest {
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
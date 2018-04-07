package qa.edu.qu.cmps312.safedrivingapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import qa.edu.qu.cmps312.safedrivingapplication.R;

public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 4000;
    String skyDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView icon = findViewById(R.id.splashscreen);
        skyDescription = find_weather();
        setContentView(R.layout.activity_splash_screen);

        switch (skyDescription) {
            case "Clear":
                icon.setImageResource(R.drawable.clearskysplash);
                break;
            case "Rain":
                icon.setImageResource(R.drawable.rainsplashscreen);
                break;

        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                SplashScreenActivity.this.startActivity(mainIntent);
                SplashScreenActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }


    public String find_weather() {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=Doha&appid=67bc52ba2b975486cd69912aba06019c&units=Metric";

        Log.w("helpMePlease", "Reached the function");

        final String[] tempreture = new String[1];
        final String[] sky_status = new String[1];

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_Object = response.getJSONObject("main");
                    JSONArray weather = response.getJSONArray("weather");
                    tempreture[0] = String.valueOf(main_Object.getDouble("temp"));
                    sky_status[0] = String.valueOf(weather.getJSONObject(1));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        RequestQueue queue = Volley.newRequestQueue(SplashScreenActivity.this);
        queue.add(jor);
        queue.start();

        Log.w("helpMePlease", "My temp : " + tempreture[0]);
        Log.w("helpMePlease", "My sky status is : " + sky_status[0]);

        return sky_status[0];


    }
}

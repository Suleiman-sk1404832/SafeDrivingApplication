package qa.edu.qu.cmps312.safedrivingapplication.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

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
    SharedPreferences sharedPreferences;
    private ImageView icon;
    private TextView disc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        icon = findViewById(R.id.splashscreenImage);
        disc = findViewById(R.id.disc);
        sharedPreferences = this.getSharedPreferences("MySharedPrefs", MODE_PRIVATE);

        find_weather();

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
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject Object = array.getJSONObject(0);
                    String dis = Object.getString("main");

                    SharedPreferences.Editor e = sharedPreferences.edit();


                    switch (dis) {
                        case "Clear":
                            disc.setText("Weather is Clear and Dry roads a head!");
                            icon.setImageResource(R.drawable.clearskysplash);
                            e.putString("sky", "Clear");
                            break;
                        case "Rain":
                            disc.setText("Weather is Rainy, sloppy roads a head!\n Be careful and drive safe! ");
                            icon.setImageResource(R.drawable.rainsplashscreen);
                            e.putString("sky", "Rain");
                            break;
                        case "Dust":
                            disc.setText("Weather is Dusty, low visibility ahead!\n Be careful and drive safe! ");
                            icon.setImageResource(R.drawable.dust);
                            e.putString("sky", "Dust");
                            break;


                    }


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

        return "finish";


    }
}

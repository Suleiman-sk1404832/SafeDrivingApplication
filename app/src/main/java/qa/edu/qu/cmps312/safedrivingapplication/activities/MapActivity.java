package qa.edu.qu.cmps312.safedrivingapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import qa.edu.qu.cmps312.safedrivingapplication.R;
import qa.edu.qu.cmps312.safedrivingapplication.services.GPSService;

/**
 * Created by Mohamad Alsokromy on 3/22/2018.
 */

public class MapActivity extends AppCompatActivity{

    private SupportMapFragment mMapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_layout);

        if (mMapFragment == null){
            mMapFragment = SupportMapFragment.newInstance();
            mMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    LatLng startingLatLng = new LatLng(MainActivity.mStartingLocation.getLatitude(),
                            MainActivity.mStartingLocation.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(startingLatLng)
                            .title("User Starting Location"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(startingLatLng));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
            });
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.map, mMapFragment).commit();

        Button stop_btn = findViewById(R.id.stop_btn);
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //stop GPS service
                stopService(new Intent(MapActivity.this, GPSService.class));

                //go back to MainActivity
                startActivity(new Intent(MapActivity.this, MainActivity.class));
            }
        });

    }
}

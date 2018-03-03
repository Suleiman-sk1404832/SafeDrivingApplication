package qa.edu.qu.cmps312.safedrivingapplication.fragments;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import qa.edu.qu.cmps312.safedrivingapplication.R;
import qa.edu.qu.cmps312.safedrivingapplication.activities.MainActivity;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Mohamad Alsokromy on 3/1/2018.
 */

public class GMapFragment extends Fragment {

    private SupportMapFragment mapFragment;

    public GMapFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.map_fragment_layout, container, false);

       if (mapFragment == null){
           mapFragment = SupportMapFragment.newInstance();
           mapFragment.getMapAsync(new OnMapReadyCallback() {
               @Override
               public void onMapReady(GoogleMap googleMap) {
                   //temp code
                   LatLng sydney = new LatLng(-33.852, 151.211);
                   googleMap.addMarker(new MarkerOptions().position(sydney)
                           .title("Marker in Sydney"));
                   googleMap.animateCamera(CameraUpdateFactory.newLatLng(sydney));
                   //TODO: Show current driver location as the center.
               }
           });
       }
        getFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        return rootView;
    }

    public interface mapInterface{
        void stopGPSService();
    }

}

package qa.edu.qu.cmps312.safedrivingapplication.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import qa.edu.qu.cmps312.safedrivingapplication.R;
import qa.edu.qu.cmps312.safedrivingapplication.activities.MainActivity;

/**
 * Created by Mohamad Alsokromy on 3/1/2018.
 */

public class GMapFragment extends Fragment {

    private SupportMapFragment mMapFragment;
    MapInterface mMapInterface;
    GoogleMap gMap;
    LatLng mCurrentPosition;
    TextView mSpeedLimit;
    Marker mUserMaker;

    public GMapFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.map_activity_layout, container, false);

       Button stop_btn = rootView.findViewById(R.id.stop_btn);
       mSpeedLimit = rootView.findViewById(R.id.speed_limit);
       stop_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               mMapInterface.stopGPSService();
           }
       });

       if (mMapFragment == null){
           mMapFragment = SupportMapFragment.newInstance();
           mMapFragment.getMapAsync(new OnMapReadyCallback() {
               @Override
               public void onMapReady(GoogleMap googleMap) {
                   gMap = googleMap;
                   LatLng startingLatLng = new LatLng(MainActivity.mStartingLocation.getLatitude(),
                           MainActivity.mStartingLocation.getLongitude());
                   mUserMaker = gMap.addMarker(new MarkerOptions().position(startingLatLng)
                           .title("Current Location"));
                   gMap.moveCamera(CameraUpdateFactory.newLatLng(startingLatLng));
                   gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
               }
           });
       }
       getFragmentManager().beginTransaction().replace(R.id.map, mMapFragment).commit();

       //Log.i("Dummy",""+mParentActivity.dummyint);
       return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mMapInterface = (GMapFragment.MapInterface) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement MainScreenInterface");
        }
    }

    public interface MapInterface {
        void stopGPSService();
    }

    public void updateCurrentPosition(LatLng currentPosition) {
        if(gMap!= null) {
            this.mCurrentPosition = currentPosition;
            mUserMaker.setPosition(currentPosition);
            gMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
        }
    }

    public void updateRoadSpeed(float roadSpeed) {
        mSpeedLimit.setText(String.format(Locale.ENGLISH,"%d",(int)roadSpeed));
    }


}

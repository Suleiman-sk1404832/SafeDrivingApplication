package qa.edu.qu.cmps312.safedrivingapplication.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
    Marker mUserMarker;

    public GMapFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mCurrentPosition = new LatLng(MainActivity.mStartingLocation.getLatitude(), // set current position to user starting position
                MainActivity.mStartingLocation.getLongitude());
       View rootView = inflater.inflate(R.layout.map_activity_layout, container, false);

       Button stop_btn = rootView.findViewById(R.id.stop_btn);
       Button relocate_btn = rootView.findViewById(R.id.relocate_btn);
       mSpeedLimit = rootView.findViewById(R.id.speed_limit);
       stop_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               mMapInterface.stopGPSService();
           }
       });
       relocate_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               gMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentPosition));
               gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
           }
       });

       if (mMapFragment == null){
           mMapFragment = SupportMapFragment.newInstance();
           mMapFragment.getMapAsync(new OnMapReadyCallback() {
               @Override
               public void onMapReady(GoogleMap googleMap) {
                   gMap = googleMap;

                   mUserMarker = gMap.addMarker(new MarkerOptions().position(mCurrentPosition)
                           .title("Current Location")
                           .icon(BitmapDescriptorFactory.fromResource(R.drawable.car2)));
                   gMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentPosition));
                   gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
               }
           });
       }
       getFragmentManager().beginTransaction().replace(R.id.map, mMapFragment).commit();

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
            mUserMarker.setPosition(currentPosition);
        }
    }

    public void updateRoadSpeed(float roadSpeed) {
        //TODO: in this method we're supposed to be receiving the road speed but we are actually receiving the user speed,
        //mSpeedLimit.setText(String.format(Locale.ENGLISH,"%d",(int)roadSpeed)); // Actually the userSpeed in KM/H
    }


}

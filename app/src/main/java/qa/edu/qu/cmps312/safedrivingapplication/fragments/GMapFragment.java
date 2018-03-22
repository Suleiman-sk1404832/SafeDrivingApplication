package qa.edu.qu.cmps312.safedrivingapplication.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import qa.edu.qu.cmps312.safedrivingapplication.R;
import qa.edu.qu.cmps312.safedrivingapplication.activities.MainActivity;

/**
 * Created by Mohamad Alsokromy on 3/1/2018.
 */

public class GMapFragment extends Fragment {

    private SupportMapFragment mMapFragment;
    MapInterface mMapInterface;
    MainActivity mParentActivity;

    public GMapFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.map_activity_layout, container, false);

       Button stop_btn = rootView.findViewById(R.id.stop_btn);
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
                   LatLng startingLatLng = new LatLng(MainActivity.mStartingLocation.getLatitude(),
                           MainActivity.mStartingLocation.getLongitude());
                   googleMap.addMarker(new MarkerOptions().position(startingLatLng)
                           .title("User Starting Location"));
                   googleMap.moveCamera(CameraUpdateFactory.newLatLng(startingLatLng));
                   googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
               }
           });
       }
       getFragmentManager().beginTransaction().replace(R.id.map, mMapFragment).commit();

       mParentActivity = (MainActivity) getActivity();
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

}

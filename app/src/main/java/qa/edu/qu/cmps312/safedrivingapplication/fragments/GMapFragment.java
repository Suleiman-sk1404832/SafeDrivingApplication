package qa.edu.qu.cmps312.safedrivingapplication.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;

import qa.edu.qu.cmps312.safedrivingapplication.R;
import qa.edu.qu.cmps312.safedrivingapplication.activities.MainActivity;

import static android.content.Context.MODE_PRIVATE;

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
    private boolean mIsDefaultPosition = false;
    SharedPreferences sharedPreferences;
    ArrayList<Marker> mDriversMarkers;
    int mCurrentIndex = 0;
    boolean mIsBoss = false;


    public GMapFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = getContext().getSharedPreferences("MySharedPrefs", MODE_PRIVATE);
        mIsBoss = sharedPreferences.getString("type", "NA").equals("Boss");
        if (MainActivity.mStartingLocation != null) {
            mCurrentPosition = new LatLng(MainActivity.mStartingLocation.getLatitude(), // set current position to user starting position
                    MainActivity.mStartingLocation.getLongitude());
        } else {
            mCurrentPosition = new LatLng(25.3028, 51.489);
            mIsDefaultPosition = true;
        }
        View rootView = inflater.inflate(R.layout.map_activity_layout, container, false);

        Button stop_btn = rootView.findViewById(R.id.stop_btn);
        Button relocate_btn = rootView.findViewById(R.id.relocate_btn);
        Button next_btn = rootView.findViewById(R.id.next_btn);
        Button prev_btn = rootView.findViewById(R.id.prev_btn);
        mSpeedLimit = rootView.findViewById(R.id.speed_limit);
        if (!mIsBoss){
            next_btn.setVisibility(View.GONE);
            prev_btn.setVisibility(View.GONE);
        }
        else
            relocate_btn.setVisibility(View.GONE);
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMapInterface.stopGPSService(0);
            }
        });
        relocate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentPosition));
                gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        });
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentIndex != mDriversMarkers.size()-1) {
                    gMap.moveCamera(CameraUpdateFactory.newLatLng(MainActivity.mDriversPositions.get(++mCurrentIndex)));
//                    gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
            }
        });
        prev_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentIndex != 0) {
                    gMap.moveCamera(CameraUpdateFactory.newLatLng(MainActivity.mDriversPositions.get(--mCurrentIndex)));
//                    gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
            }
        });

        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            mMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    gMap = googleMap;
                    if (mIsBoss) {
                        mDriversMarkers = new ArrayList<>();
                        for (int i = 0; i < MainActivity.mDriversPositions.size(); i++) { // all drivers positions
                            mDriversMarkers.add(gMap.addMarker(new MarkerOptions().position(MainActivity.mDriversPositions.get(i))
                                    .title(MainActivity.mDriversFullNames.get(i))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car2))));
                        }
                        gMap.moveCamera(CameraUpdateFactory.newLatLng(MainActivity.mDriversPositions.get(0)));
                        gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    } else { // a driver
                        mUserMarker = gMap.addMarker(new MarkerOptions().position(mCurrentPosition)
                                .title("Current Location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car2)));
                        gMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentPosition));
                        gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    }
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
        void stopGPSService(int flag);
    }

    public void updateCurrentPosition(LatLng currentPosition) {
        if (gMap != null) {
            this.mCurrentPosition = currentPosition;
            mUserMarker.setPosition(currentPosition);
            if (mIsDefaultPosition) {
                gMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentPosition));
                gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        }
    }

    public void updateDriversPosition(){
        for (int i = 0; i < MainActivity.mDriversPositions.size(); i++) { // all drivers positions
            mDriversMarkers.get(i).setPosition(MainActivity.mDriversPositions.get(i));
        }
    }

    public void updateRoadSpeed(float roadSpeed) {
        //mSpeedLimit.setText(String.format(Locale.ENGLISH,"%d",(int)roadSpeed)); // Actually the userSpeed in KM/H
    }


}

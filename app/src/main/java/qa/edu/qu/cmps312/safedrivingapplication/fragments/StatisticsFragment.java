package qa.edu.qu.cmps312.safedrivingapplication.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import qa.edu.qu.cmps312.safedrivingapplication.R;
import qa.edu.qu.cmps312.safedrivingapplication.activities.MainActivity;
import qa.edu.qu.cmps312.safedrivingapplication.models.User;
import qa.edu.qu.cmps312.safedrivingapplication.models.Trip;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mohdf on 5/13/2018.
 */

public class StatisticsFragment extends Fragment {

    StatisticsFragmentInterface statisticsFragmentInterface;

    DatabaseReference mDatabase;

    SharedPreferences sharedPreferences;


    Trip trip;

    TextView usernameTV, noOfTripTV, totalDistanceTV, totalDangerTV, totalTimeTV,
            averageDistanceTV,averageDangerTV, averageTimeTV,averageSpeedTV;
    Button backBtn, nxtBtn, prevBtn;
    public StatisticsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.statistics_fragment_layout, container, false);

        nxtBtn = rootView.findViewById(R.id.statsNextBtn);
        nxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // implement moving between users
            }
        });

        prevBtn = rootView.findViewById(R.id.statsPrevBtn);
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // implement moving between users
            }
        });

        usernameTV = rootView.findViewById(R.id.usernameTV);
        noOfTripTV = rootView.findViewById(R.id.noOfTripsTV);
        totalDistanceTV = rootView.findViewById(R.id.totalDistanceTravelledTV);
        totalDangerTV = rootView.findViewById(R.id.totalDangerousTimeTV);
        totalTimeTV = rootView.findViewById(R.id.totalTimeTV);
        averageSpeedTV = rootView.findViewById(R.id.averageSpeedTV);
        averageDistanceTV = rootView.findViewById(R.id.AverageTravelledDistancePerTripTV);
        averageDangerTV = rootView.findViewById(R.id.AverageDangerTimePerTripTV);
        averageTimeTV = rootView.findViewById(R.id.AverageTimePerTripTV);

        sharedPreferences = getContext().getSharedPreferences("MySharedPrefs", MODE_PRIVATE);
        Boolean isBoss = sharedPreferences.getString("type","0").equals("Boss");
        if(!isBoss) {
            nxtBtn.setVisibility(View.GONE);
            prevBtn.setVisibility(View.GONE);
        }
//        Log.i("SHOW_ME", "key: "+sharedPreferences.getString("key", "-1"));

        trip = new Trip();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Drivers");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.getChildren()) { // all users in db
                    if (ds.getKey().equals(sharedPreferences.getString("key", "-1"))) { // current user
                        if (ds.getValue(User.class).getType().equals("Driver")) { // if current user is a driver, show his data and hide next button
                            trip.setNoOfTrips(ds.getValue(User.class).getTrip().getNoOfTrips());
                            trip.setAvgSpeed(ds.getValue(User.class).getTrip().getAvgSpeed());
                            trip.setTotDangerTimeInMin(ds.getValue(User.class).getTrip().getTotDangerTimeInMin());
                            trip.setTotDistanceTraveled(ds.getValue(User.class).getTrip().getTotDistanceTraveled());
                            trip.setTotTimeInMin(ds.getValue(User.class).getTrip().getTotTimeInMin());
                            usernameTV.setText(ds.getValue(User.class).getUserName());
                            noOfTripTV.setText(String.format(Locale.ENGLISH, "%d", trip.getNoOfTrips()));
                            totalDistanceTV.setText(String.format(Locale.ENGLISH, "%.2f", trip.getTotDistanceTraveled()));
                            totalDangerTV.setText(String.format(Locale.ENGLISH, "%.2f", trip.getTotDangerTimeInMin()));
                            totalTimeTV.setText(String.format(Locale.ENGLISH, "%.2f", trip.getTotTimeInMin()));
                            averageSpeedTV.setText(String.format(Locale.ENGLISH, "%.2f", trip.getAverageSpeed()));
                            averageTimeTV.setText(String.format(Locale.ENGLISH, "%.2f", trip.getAverageTimeInMin()));
                            averageDistanceTV.setText(String.format(Locale.ENGLISH, "%.2f", trip.getAverageDistanceTraveled()));
                            averageDangerTV.setText(String.format(Locale.ENGLISH, "%.2f", trip.getAverageDangerousTimeInMin()));
                            //Log.e("trip info",Float.toString(trip.getAverageDangerousTimeInMin()));
                            //Log.e("trip info",Float.toString(trip.getAverageDistanceTraveled()));
                            //Log.e("trip info",Float.toString(trip.getAverageTimeInMin()));

                        }
                        else { // a Boss type user

                        }
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        if (trip != null) {


           // Log.e("trip info",Float.toString(trip.getAverageDangerousTimeInMin()));
            //Log.e("trip info",Float.toString(trip.getAverageDistanceTraveled()));
            //Log.e("trip info",Float.toString(trip.getAverageTimeInMin()));
        }
        else {
            noOfTripTV.setText("0");
            totalDistanceTV.setText("0");
            totalDangerTV.setText("0");
            totalTimeTV.setText("0");

            averageSpeedTV.setText("0");
            averageTimeTV.setText("0");
            averageDistanceTV.setText("0");
            averageDangerTV.setText("0");
        }
        backBtn = rootView.findViewById(R.id.statsBackBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statisticsFragmentInterface.back();
            }
        });


        return rootView;
    }

    public void nextDriver(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            statisticsFragmentInterface = (StatisticsFragment.StatisticsFragmentInterface) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement AddCarInterface");
        }
    }

    public interface StatisticsFragmentInterface{
        void back();
    }
}

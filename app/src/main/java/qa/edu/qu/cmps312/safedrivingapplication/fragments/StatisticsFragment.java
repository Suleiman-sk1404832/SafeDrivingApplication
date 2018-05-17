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

    TextView noOfTripTV, totalDistanceTV, totalDangerTV, totalTimeTV,
            averageDistanceTV,averageDangerTV, averageTimeTV,averageSpeedTV;
    Button backBtn;
    public StatisticsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.statistics_fragment_layout, container, false);


        noOfTripTV = rootView.findViewById(R.id.noOfTripsTV);
        totalDistanceTV = rootView.findViewById(R.id.totalDistanceTravelledTV);
        totalDangerTV = rootView.findViewById(R.id.totalDangerousTimeTV);
        totalTimeTV = rootView.findViewById(R.id.totalTimeTV);
        averageSpeedTV = rootView.findViewById(R.id.averageSpeedTV);
        averageDistanceTV = rootView.findViewById(R.id.AverageTravelledDistancePerTripTV);
        averageDangerTV = rootView.findViewById(R.id.AverageDangerTimePerTripTV);
        averageTimeTV = rootView.findViewById(R.id.AverageTimePerTripTV);

        sharedPreferences = getContext().getSharedPreferences("MySharedPrefs", MODE_PRIVATE);

        trip = new Trip();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Drivers");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.getValue(User.class).getTrip() != null) {
                        trip.setNoOfTrips(ds.getValue(User.class).getTrip().getNoOfTrips());
                        trip.setAvgSpeed(ds.getValue(User.class).getTrip().getAvgSpeed());
                        trip.setTotDangerTimeInMin(ds.getValue(User.class).getTrip().getTotDangerTimeInMin());
                        trip.setTotDistanceTraveled(ds.getValue(User.class).getTrip().getTotDistanceTraveled());
                        trip.setTotTimeInMin(ds.getValue(User.class).getTrip().getTotTimeInMin());
                        noOfTripTV.setText(Integer.toString(trip.getNoOfTrips()));
                        totalDistanceTV.setText(Float.toString(trip.getTotDistanceTraveled()));
                        totalDangerTV.setText(Float.toString(trip.getTotDangerTimeInMin()));
                        totalTimeTV.setText(Float.toString(trip.getTotTimeInMin()));

                        averageSpeedTV.setText(Float.toString(trip.getAvgSpeed()));
                        averageTimeTV.setText(Float.toString(trip.getAverageTimeInMin()));
                        averageDistanceTV.setText(Float.toString(trip.getAverageDistanceTraveled()));
                        averageDangerTV.setText(Float.toString(trip.getTotDangerTimeInMin()));
                        //Log.e("trip info",Float.toString(trip.getAverageDangerousTimeInMin()));
                        //Log.e("trip info",Float.toString(trip.getAverageDistanceTraveled()));
                        //Log.e("trip info",Float.toString(trip.getAverageTimeInMin()));
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

package qa.edu.qu.cmps312.safedrivingapplication.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import qa.edu.qu.cmps312.safedrivingapplication.R;
import qa.edu.qu.cmps312.safedrivingapplication.models.User;
import qa.edu.qu.cmps312.safedrivingapplication.models.Trip;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mohdf on 5/13/2018.
 */

public class StatisticsFragment extends Fragment {

    StatisticsFragmentInterface statisticsFragmentInterface;


    SharedPreferences sharedPreferences;

    String mCurrentUserFName;
    ArrayList<String> mDriversFName;
    int mCurrentIndex = 0;
    Trip mTrip;
    ArrayList<Trip> mDriversTrips;

    TextView userFNameTV, noOfTripTV, totalDistanceTV, totalDangerTV, totalTimeTV,
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
                showNextStats();
            }
        });

        prevBtn = rootView.findViewById(R.id.statsPrevBtn);
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPreviousStats();
            }
        });

        userFNameTV = rootView.findViewById(R.id.usernameTV);
        noOfTripTV = rootView.findViewById(R.id.noOfTripsTV);
        totalDistanceTV = rootView.findViewById(R.id.totalDistanceTravelledTV);
        totalDangerTV = rootView.findViewById(R.id.totalDangerousTimeTV);
        totalTimeTV = rootView.findViewById(R.id.totalTimeTV);
        averageSpeedTV = rootView.findViewById(R.id.averageSpeedTV);
        averageDistanceTV = rootView.findViewById(R.id.AverageTravelledDistancePerTripTV);
        averageDangerTV = rootView.findViewById(R.id.AverageDangerTimePerTripTV);
        averageTimeTV = rootView.findViewById(R.id.AverageTimePerTripTV);

        sharedPreferences = getContext().getSharedPreferences("MySharedPrefs", MODE_PRIVATE);
        final Boolean isBoss = sharedPreferences.getString("type","NA").equals("Boss");
        if(!isBoss) { // if a driver
            nxtBtn.setVisibility(View.GONE);
            prevBtn.setVisibility(View.GONE);
        }

        mTrip = new Trip();
        mDriversTrips = new ArrayList<>();
        mDriversFName = new ArrayList<>();
        final boolean[] isShown = {false};
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Drivers");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDriversTrips.clear();
                mDriversFName.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) { // all users in db
                    if (ds.getKey().equals(sharedPreferences.getString("key", "-1"))) { // current user
                        if (ds.getValue(User.class).getType().equals("Driver")) { // current user is a driver, save his trip
                            if (ds.getValue(User.class).getTrip() != null) { // driver has a trip data
                                mTrip = new Trip(ds.getValue(User.class).getTrip().getTotTimeInMin()
                                        , ds.getValue(User.class).getTrip().getTotDangerTimeInMin()
                                        , ds.getValue(User.class).getTrip().getTotDistanceTraveled()
                                        , ds.getValue(User.class).getTrip().getAvgSpeed());
                                mTrip.setNoOfTrips(ds.getValue(User.class).getTrip().getNoOfTrips());
                                mCurrentUserFName = ds.getValue(User.class).getFirstName().concat(" "+ds.getValue(User.class).getLastName());
                                userFNameTV.setText(mCurrentUserFName);
                                noOfTripTV.setText(String.format(Locale.ENGLISH, "%d", mTrip.getNoOfTrips()));
                                totalDistanceTV.setText(String.format(Locale.ENGLISH, "%.2f KM", mTrip.getTotDistanceTraveled()));
                                totalDangerTV.setText(String.format(Locale.ENGLISH, "%.2f Min", mTrip.getTotDangerTimeInMin()));
                                totalTimeTV.setText(String.format(Locale.ENGLISH, "%.2f Min", mTrip.getTotTimeInMin()));
                                averageSpeedTV.setText(String.format(Locale.ENGLISH, "%.2f KM/H", mTrip.getTotAverageSpeed()));
                                averageTimeTV.setText(String.format(Locale.ENGLISH, "%.2f Min", mTrip.getAverageTimeInMin()));
                                averageDistanceTV.setText(String.format(Locale.ENGLISH, "%.2f KM", mTrip.getAverageDistanceTraveled()));
                                averageDangerTV.setText(String.format(Locale.ENGLISH, "%.2f Min", mTrip.getAverageDangerousTimeInMin()));
                            }
                        }
                    }
                    else if(isBoss) { // not current user, but a boss is logged in, so we need others trip data
                        if (ds.getValue(User.class).getTrip() != null) { // driver has a trip data
                            Trip driverTrip = new Trip(ds.getValue(User.class).getTrip().getTotTimeInMin()
                                    , ds.getValue(User.class).getTrip().getTotDangerTimeInMin()
                                    , ds.getValue(User.class).getTrip().getTotDistanceTraveled()
                                    , ds.getValue(User.class).getTrip().getAvgSpeed());
                            driverTrip.setNoOfTrips(ds.getValue(User.class).getTrip().getNoOfTrips());
                            mDriversTrips.add(driverTrip);
                            mDriversFName.add(ds.getValue(User.class).getFirstName().concat(" "+ds.getValue(User.class).getLastName()));
                            if (!isShown[0]) { // show first driver data
                                userFNameTV.setText(mDriversFName.get(0));
                                noOfTripTV.setText(String.format(Locale.ENGLISH, "%d", mDriversTrips.get(0).getNoOfTrips()));
                                totalDistanceTV.setText(String.format(Locale.ENGLISH, "%.2f KM", mDriversTrips.get(0).getTotDistanceTraveled()));
                                totalDangerTV.setText(String.format(Locale.ENGLISH, "%.2f Min", mDriversTrips.get(0).getTotDangerTimeInMin()));
                                totalTimeTV.setText(String.format(Locale.ENGLISH, "%.2f Min", mDriversTrips.get(0).getTotTimeInMin()));
                                averageSpeedTV.setText(String.format(Locale.ENGLISH, "%.2f KM/H", mDriversTrips.get(0).getTotAverageSpeed()));
                                averageTimeTV.setText(String.format(Locale.ENGLISH, "%.2f Min", mDriversTrips.get(0).getAverageTimeInMin()));
                                averageDistanceTV.setText(String.format(Locale.ENGLISH, "%.2f KM", mDriversTrips.get(0).getAverageDistanceTraveled()));
                                averageDangerTV.setText(String.format(Locale.ENGLISH, "%.2f Min", mDriversTrips.get(0).getAverageDangerousTimeInMin()));
                                isShown[0] = true;
                            }
                        }
                    }
                }
                myRef.removeEventListener(this);
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        backBtn = rootView.findViewById(R.id.statsBackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statisticsFragmentInterface.back();
            }
        });


        return rootView;
    }

    public void showNextStats(){
        if (mCurrentIndex != mDriversFName.size()-1) {
            ++mCurrentIndex;
            userFNameTV.setText(mDriversFName.get(mCurrentIndex));
            noOfTripTV.setText(String.format(Locale.ENGLISH, "%d", mDriversTrips.get(mCurrentIndex).getNoOfTrips()));
            totalDistanceTV.setText(String.format(Locale.ENGLISH, "%.2f KM", mDriversTrips.get(mCurrentIndex).getTotDistanceTraveled()));
            totalDangerTV.setText(String.format(Locale.ENGLISH, "%.2f Min", mDriversTrips.get(mCurrentIndex).getTotDangerTimeInMin()));
            totalTimeTV.setText(String.format(Locale.ENGLISH, "%.2f Min", mDriversTrips.get(mCurrentIndex).getTotTimeInMin()));
            averageSpeedTV.setText(String.format(Locale.ENGLISH, "%.2f KM/H", mDriversTrips.get(mCurrentIndex).getTotAverageSpeed()));
            averageTimeTV.setText(String.format(Locale.ENGLISH, "%.2f Min", mDriversTrips.get(mCurrentIndex).getAverageTimeInMin()));
            averageDistanceTV.setText(String.format(Locale.ENGLISH, "%.2f KM", mDriversTrips.get(mCurrentIndex).getAverageDistanceTraveled()));
            averageDangerTV.setText(String.format(Locale.ENGLISH, "%.2f Min", mDriversTrips.get(mCurrentIndex).getAverageDangerousTimeInMin()));
        }
    }

    public void showPreviousStats(){
        if (mCurrentIndex != 0) {
            --mCurrentIndex;
            userFNameTV.setText(mDriversFName.get(mCurrentIndex));
            noOfTripTV.setText(String.format(Locale.ENGLISH, "%d", mDriversTrips.get(mCurrentIndex).getNoOfTrips()));
            totalDistanceTV.setText(String.format(Locale.ENGLISH, "%.2f KM", mDriversTrips.get(mCurrentIndex).getTotDistanceTraveled()));
            totalDangerTV.setText(String.format(Locale.ENGLISH, "%.2f Min", mDriversTrips.get(mCurrentIndex).getTotDangerTimeInMin()));
            totalTimeTV.setText(String.format(Locale.ENGLISH, "%.2f Min", mDriversTrips.get(mCurrentIndex).getTotTimeInMin()));
            averageSpeedTV.setText(String.format(Locale.ENGLISH, "%.2f KM/H", mDriversTrips.get(mCurrentIndex).getTotAverageSpeed()));
            averageTimeTV.setText(String.format(Locale.ENGLISH, "%.2f Min", mDriversTrips.get(mCurrentIndex).getAverageTimeInMin()));
            averageDistanceTV.setText(String.format(Locale.ENGLISH, "%.2f KM", mDriversTrips.get(mCurrentIndex).getAverageDistanceTraveled()));
            averageDangerTV.setText(String.format(Locale.ENGLISH, "%.2f Min", mDriversTrips.get(mCurrentIndex).getAverageDangerousTimeInMin()));
        }
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

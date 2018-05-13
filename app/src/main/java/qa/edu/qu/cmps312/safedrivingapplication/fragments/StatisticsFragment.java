package qa.edu.qu.cmps312.safedrivingapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import qa.edu.qu.cmps312.safedrivingapplication.R;

/**
 * Created by mohdf on 5/13/2018.
 */

public class StatisticsFragment extends Fragment {

    StatisticsFragmentInterface statisticsFragmentInterface;

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

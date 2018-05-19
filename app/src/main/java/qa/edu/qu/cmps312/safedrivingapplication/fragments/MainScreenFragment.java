package qa.edu.qu.cmps312.safedrivingapplication.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import qa.edu.qu.cmps312.safedrivingapplication.R;


public class MainScreenFragment extends Fragment {

    MainScreenInterface mainScreenInterface;
    Button mapsBtn, addCarBtn, manageCarBtn, showStatsBtn, logoutBtn;

    public MainScreenFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menu_screen_fragment_layout, container, false);

        mapsBtn = rootView.findViewById(R.id.MgoogleMapsBtn);
        addCarBtn = rootView.findViewById(R.id.MaddCarBtn);
        manageCarBtn = rootView.findViewById(R.id.manageCar);
        showStatsBtn = rootView.findViewById(R.id.viewStats);
        logoutBtn = rootView.findViewById(R.id.logOutBtn);

        mapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainScreenInterface.openMaps();
            }
        });
        addCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainScreenInterface.openAddCars();
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // FIRE ZE MISSILES!
                                mainScreenInterface.logOut();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                // Create the AlertDialog object and return it
                builder.create().show();
            }
        });

        manageCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainScreenInterface.openManageCar();
            }
        });

        showStatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainScreenInterface.showStats();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mainScreenInterface = (MainScreenFragment.MainScreenInterface) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement MainScreenInterface");
        }
    }

    public interface MainScreenInterface {
        void openMaps();

        void openAddCars();

        void openManageCar();

        void showStats();

        void logOut();
    }
}

package qa.edu.qu.cmps312.safedrivingapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import qa.edu.qu.cmps312.safedrivingapplication.R;
import qa.edu.qu.cmps312.safedrivingapplication.adapters.CarAdapter;
import qa.edu.qu.cmps312.safedrivingapplication.models.Car;
import qa.edu.qu.cmps312.safedrivingapplication.models.Driver;

public class AddCarFragment extends Fragment {

    AddCarInterface addCarInterface;
    ArrayList<Car> userCars;
    Button addBtn;
    ListView carsList;
    CarAdapter adapter;

    public AddCarFragment() {
    }

    public static AddCarFragment newInstance(ArrayList<Car> car) {

        Bundle args = new Bundle();

        AddCarFragment fragment = new AddCarFragment();

        args.putParcelableArrayList("car", car);

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_car_fragment_layout, container, false);

        carsList = rootView.findViewById(R.id.CarList);


        if (getArguments() != null) {
            getTheCars(addCarInterface.getUserName());
            Log.wtf("kill", "when i am in the arguments");
            userCars = getArguments().getParcelableArrayList("car");

            this.addCarToUser(addCarInterface.getUserName(), getArguments().<Car>getParcelableArrayList("car"));
        } else {
            Log.wtf("kill", "i am here");
            getTheCars(addCarInterface.getUserName());
        }


        addBtn = rootView.findViewById(R.id.addCarBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCarInterface.addUserCar(userCars);
            }
        });


        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            addCarInterface = (AddCarFragment.AddCarInterface) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement AddCarInterface");
        }
    }

    public void getTheCars(String username) {
        userCars = new ArrayList();
        final String[] compareUser = {""};
        final Boolean[] flag = {false};
        final String mUsername = username;

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Drivers");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    compareUser[0] = ds.getValue(Driver.class).getUserName();
                    if (mUsername.equals(compareUser[0].toString())) {
                        if (ds.getValue(Driver.class).getUserCarsList() != null) {
                            if (ds.getValue(Driver.class).getUserCarsList().size() > 0) {
                                for (int i = 0; i < ds.getValue(Driver.class).getUserCarsList().size(); i++) {
                                    userCars.add(ds.getValue(Driver.class).getUserCarsList().get(i));
//                                    Log.wtf("Test",ds.getValue(Driver.class).getUserCarsList().get(i).getMake());
//                                    Log.wtf("Test",ds.getValue(Driver.class).getUserCarsList().get(i).getModel());
//                                    Log.wtf("Test",ds.getValue(Driver.class).getUserCarsList().get(i).getYear());
//                                    Log.wtf("Test",ds.getValue(Driver.class).getUserCarsList().get(i).getMilage() +"");
//                                    Log.wtf("test", userCars.size() + " new size of the array");
                                }
                                flag[0] = true;
                            }
                        }
                    }
                }
                if (flag[0]) {

                    if (userCars == null) {
                        adapter = new CarAdapter(getContext(), userCars);
                        carsList.setAdapter(adapter);
                    }

                } else {
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


    }

    public void addCarToUser(String username, final ArrayList<Car> carList) {

        final String[] compareUser = {""};
        final Boolean[] flag = {false};
        final String mUsername = username;

        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Drivers");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    compareUser[0] = ds.getValue(Driver.class).getUserName();
                    if (mUsername.equals(compareUser[0].toString())) {

                        if (ds.getValue(Driver.class).getUserCarsList() != null)

                            myRef.child(ds.getKey()).child("userCarsList").setValue(carList);

                        flag[0] = true;


                    }
                }
                if (flag[0]) {

                    adapter = new CarAdapter(getContext(), userCars);
                    carsList.setAdapter(adapter);

                } else {
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


    }

    public interface AddCarInterface {
        String getUserName();

        void addUserCar(ArrayList<Car> list);
    }
}

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
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import qa.edu.qu.cmps312.safedrivingapplication.R;
import qa.edu.qu.cmps312.safedrivingapplication.models.Car;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mohdf on 5/8/2018.
 */

public class ManageCarFragment extends Fragment{

    ManageCarInterface manageCarInterface;

    DatabaseReference mDatabase;
    SharedPreferences sharedPreferences;

    EditText make, model, year, milage;
    Car userCar;
    Button clearFieldsBtn, submitBtn, cancelBtn;

    public ManageCarFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.manage_car_fragment_layout, container, false);

        sharedPreferences = getContext().getSharedPreferences("MySharedPrefs", MODE_PRIVATE);


        make = rootView.findViewById(R.id.mCarMake);
        model = rootView.findViewById(R.id.mModel);
        year = rootView.findViewById(R.id.mYear);
        milage = rootView.findViewById(R.id.mMileage);


        milage.setText(sharedPreferences.getInt("mileage", 0) + "");
        make.setText(sharedPreferences.getString("make", ""));
        year.setText(sharedPreferences.getString("year", ""));
        model.setText(sharedPreferences.getString("model", ""));
        if (userCar != null) {
            make.setText(userCar.getMake() + "");
            model.setText(userCar.getModel() + "");
            year.setText(userCar.getYear() + "");
            milage.setText(userCar.getMilage() + "");
        }

        clearFieldsBtn = rootView.findViewById(R.id.mClearFields);
        submitBtn = rootView.findViewById(R.id.mSubmitButton);
        cancelBtn = rootView.findViewById(R.id.mCanecelButton);


        clearFieldsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make.setText("");
                model.setText("");
                year.setText("");
                milage.setText("");
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageCarInterface.cancelManageCar();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mMake = make.getText().toString();
                String mModel = model.getText().toString();
                String mYear = year.getText().toString();
                String mMilage = milage.getText().toString();
                if (isNotEmpty(mMake) && isNotEmpty(mModel) && isNotEmpty(mYear) && isNotEmpty(mMilage)) {
                    if(Integer.parseInt(year.getText().toString())< 1940 || Integer.parseInt(year.getText().toString())> 2018)
                        year.setError("the year should be between 1940 and 2018");
                    else if(Integer.parseInt(milage.getText().toString())< 0 || Integer.parseInt(milage.getText().toString())> 300000)
                        milage.setError("the mileage should be between 0 and 300000");
                    else
                        manageCarInterface.submitManagedCar(mMake, mModel, mYear, mMilage);
                } else
                    Toast.makeText(getContext(), "Please Fill all the fields", Toast.LENGTH_SHORT).show();
            }
        });



        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            manageCarInterface = (ManageCarFragment.ManageCarInterface) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ManageCarInterface");
        }
    }

    public boolean isNotEmpty(String s) {
        if (s.trim().length() > 0)
            return true;
        else
            return false;
    }

    public interface ManageCarInterface{
        void cancelManageCar();

        void submitManagedCar(String make, String model, String year, String milage);
    }
}

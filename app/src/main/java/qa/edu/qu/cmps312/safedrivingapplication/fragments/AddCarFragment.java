package qa.edu.qu.cmps312.safedrivingapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import qa.edu.qu.cmps312.safedrivingapplication.R;

public class AddCarFragment extends Fragment {

    AddCarInterface addCarInterface;

    EditText make, model, year, milage;
    Button clearBtn, submitBtn, cancelBtn;


    public AddCarFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_car_fragment_layout, container, false);

        make = rootView.findViewById(R.id.CRmakeTv);
        model = rootView.findViewById(R.id.CRmodelTv);
        year = rootView.findViewById(R.id.CRyearTv);
        milage = rootView.findViewById(R.id.CRmilageTv);

        clearBtn = rootView.findViewById(R.id.CRclearBtn);
        submitBtn = rootView.findViewById(R.id.CRsubmitBtn);
        cancelBtn = rootView.findViewById(R.id.CRcancelBtn);


        clearBtn.setOnClickListener(new View.OnClickListener() {
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
            public void onClick(View v) {
                addCarInterface.cancelAddCar();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mMake = make.getText().toString();
                String mModel = model.getText().toString();
                String mYear = year.getText().toString();
                String mMilage = milage.getText().toString();
                if (isNotEmpty(mMake) && isNotEmpty(mModel) && isNotEmpty(mYear) && isNotEmpty(mMilage)) {
                    addCarInterface.submitCar(mMake, mModel, mYear, mMilage);
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
            addCarInterface = (AddCarFragment.AddCarInterface) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement AddCarInterface");
        }
    }

    public boolean isNotEmpty(String s) {
        if (s.trim().length() > 0)
            return true;
        else
            return false;
    }


    public interface AddCarInterface {
        void cancelAddCar();

        void submitCar(String make, String model, String year, String milage);
    }

}

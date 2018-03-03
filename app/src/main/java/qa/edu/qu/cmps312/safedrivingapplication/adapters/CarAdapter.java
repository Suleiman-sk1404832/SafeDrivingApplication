package qa.edu.qu.cmps312.safedrivingapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import qa.edu.qu.cmps312.safedrivingapplication.R;
import qa.edu.qu.cmps312.safedrivingapplication.models.Car;


public class CarAdapter extends ArrayAdapter<Car> {

    TextView make, model, year, carNum, mileage;
    ArrayList<Car> cars;

    public CarAdapter(Context c, ArrayList<Car> cars) {
        super(c, R.layout.car_list_row_layout, cars);

        this.cars = cars;

    }

    public View getView(int p, View v, ViewGroup parent) {


        if (v == null) {
            LayoutInflater in = LayoutInflater.from(getContext());
            v = in.inflate(R.layout.car_list_row_layout, parent, false);
        }


        make = v.findViewById(R.id.CmakeTv);
        model = v.findViewById(R.id.CmodelTv);
        year = v.findViewById(R.id.CyearTv);
        carNum = v.findViewById(R.id.CcarNumberTv);
        mileage = v.findViewById(R.id.CmileageTv);


        make.setText(getItem(p).getMake());
        model.setText(getItem(p).getModel());
        year.setText(getItem(p).getYear());
        mileage.setText(getItem(p).getMilage() + "");
        carNum.setText(p + 1 + "");


        return v;
    }
}
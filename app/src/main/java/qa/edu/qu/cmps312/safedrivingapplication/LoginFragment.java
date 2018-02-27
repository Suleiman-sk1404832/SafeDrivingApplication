package qa.edu.qu.cmps312.safedrivingapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class LoginFragment extends Fragment {


    public LoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.activity_main,container,false);

        return rootView;
    }
}

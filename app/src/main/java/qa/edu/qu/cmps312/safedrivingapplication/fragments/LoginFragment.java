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

import qa.edu.qu.cmps312.safedrivingapplication.R;


public class LoginFragment extends Fragment {

    SuccessfulLogin successfulLogin;
    Button login, register;
    EditText username, password;
    SharedPreferences sharedPreferences;

    public LoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment_layout, container, false);


        login = rootView.findViewById(R.id.loginButton);
        register = rootView.findViewById(R.id.registerButton);

        username = rootView.findViewById(R.id.LusernameTV);
        password = rootView.findViewById(R.id.LpasswordTV);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successfulLogin.login(username.getText().toString(), password.getText().toString());

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successfulLogin.register();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            successfulLogin = (SuccessfulLogin) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement successfull Login");
        }
    }

    public interface SuccessfulLogin {
        void login(String user, String pass);

        void register();
    }


}

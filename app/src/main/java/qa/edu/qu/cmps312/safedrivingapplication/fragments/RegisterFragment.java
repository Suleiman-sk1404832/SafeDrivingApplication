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
import android.widget.Spinner;
import android.widget.Toast;

import qa.edu.qu.cmps312.safedrivingapplication.R;


public class RegisterFragment extends Fragment {

    Button clearBtn, cancelBtn, registerBtn;
    EditText fname, lname, dateOfBirth, username, password, confirmPass;
    Spinner spinner;
    RegisterInterface registerInterface;


    public RegisterFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.register_fragment_layout, container, false);

        //Defining edit texts
        fname = rootView.findViewById(R.id.RfirstNameTv);
        lname = rootView.findViewById(R.id.RlastNameTv);
        dateOfBirth = rootView.findViewById(R.id.RdateofBirthTv);
        username = rootView.findViewById(R.id.RusernameTv);
        password = rootView.findViewById(R.id.RpasswordTv);
        confirmPass = rootView.findViewById(R.id.RconfirmPassTv);

        //Defining buttons and their logic
        clearBtn = rootView.findViewById(R.id.RclearBtn);
        cancelBtn = rootView.findViewById(R.id.RcancelBtn);
        registerBtn = rootView.findViewById(R.id.RsubmitBtn);
        spinner = rootView.findViewById(R.id.spinner);


        //Buttons logic
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname.setText("");
                lname.setText("");
                dateOfBirth.setText("");
                username.setText("");
                password.setText("");
                confirmPass.setText("");
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerInterface.cancel();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNotEmpty(fname.getText().toString()) &&
                        isNotEmpty(lname.getText().toString()) &&
                        isNotEmpty(dateOfBirth.getText().toString()) &&
                        isNotEmpty(username.getText().toString()) &&
                        isNotEmpty(password.getText().toString()) &&
                        isNotEmpty(confirmPass.getText().toString())) {
                    if (password.getText().toString().equals(confirmPass.getText().toString())) {

                        registerInterface.submit(fname.getText().toString(), lname.getText().toString(),
                                dateOfBirth.getText().toString(), username.getText().toString(),
                                password.getText().toString(), spinner.getSelectedItem().toString());
                    } else
                        Toast.makeText(getContext(), "Passwords Don't match", Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });


        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            registerInterface = (RegisterInterface) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement RegisterInterface");
        }
    }

    public boolean isNotEmpty(String s) {
        if (s.trim().length() > 0)
            return true;
        else
            return false;
    }

    public interface RegisterInterface {
        void submit(String fname, String lname, String dateOfBirth, String username, String password, String type);

        void cancel();
    }
}

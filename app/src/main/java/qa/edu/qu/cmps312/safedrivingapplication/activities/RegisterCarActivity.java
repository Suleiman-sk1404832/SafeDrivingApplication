package qa.edu.qu.cmps312.safedrivingapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import qa.edu.qu.cmps312.safedrivingapplication.R;

public class RegisterCarActivity extends AppCompatActivity {

    EditText make, model, year, milage;
    Button clearBtn, submitBtn, cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_car);

        make = findViewById(R.id.CRmakeTv);
        model = findViewById(R.id.CRmodelTv);
        year = findViewById(R.id.CRyearTv);
        milage = findViewById(R.id.CRmilageTv);

        clearBtn = findViewById(R.id.CRclearBtn);
        submitBtn = findViewById(R.id.CRsubmitBtn);
        cancelBtn = findViewById(R.id.CRcancelBtn);


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
                setResult(RESULT_CANCELED);
                finish();
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
                    Intent intent = new Intent();
                    intent.putExtra("make", mMake);
                    intent.putExtra("model", mModel);
                    intent.putExtra("year", mYear);
                    intent.putExtra("milage", Integer.parseInt(mMilage));
                    setResult(RESULT_OK, intent);
                    finish();
                } else
                    Toast.makeText(RegisterCarActivity.this, "Please Fill all the fields", Toast.LENGTH_SHORT).show();
            }
        });


    }


    public boolean isNotEmpty(String s) {
        if (s.trim().length() > 0)
            return true;
        else
            return false;
    }
}

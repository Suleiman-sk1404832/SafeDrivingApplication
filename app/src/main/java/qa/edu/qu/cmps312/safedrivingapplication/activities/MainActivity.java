package qa.edu.qu.cmps312.safedrivingapplication.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import qa.edu.qu.cmps312.safedrivingapplication.R;
import qa.edu.qu.cmps312.safedrivingapplication.fragments.AddCarFragment;
import qa.edu.qu.cmps312.safedrivingapplication.fragments.GMapFragment;
import qa.edu.qu.cmps312.safedrivingapplication.fragments.LoginFragment;
import qa.edu.qu.cmps312.safedrivingapplication.fragments.MainScreenFragment;
import qa.edu.qu.cmps312.safedrivingapplication.fragments.RegisterFragment;
import qa.edu.qu.cmps312.safedrivingapplication.models.Car;
import qa.edu.qu.cmps312.safedrivingapplication.models.Driver;
import qa.edu.qu.cmps312.safedrivingapplication.services.GPSService;

public class MainActivity extends AppCompatActivity implements LoginFragment.SuccessfulLogin,
        MainScreenFragment.MainScreenInterface, RegisterFragment.RegisterInterface,
        AddCarFragment.AddCarInterface, GMapFragment.MapInterface {


    static final int REGISTER_CAR_REQUEST_CODE = 301;
    static final int PERMISSIONS_REQUEST_CODE = 22;

    LoginFragment loginFragment;
    ArrayList<Car> tempList;
    DatabaseReference mDatabase;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = this.getSharedPreferences("MySharedPrefs", MODE_PRIVATE);

        tempList = new ArrayList<>();


        //TODO: Don't add these two again they are already in the firebase i will make the register do the work of adding tomorrow
        //Random key
        String key = FirebaseDatabase.getInstance().getReference("Drivers").push().getKey();
        //Just assigning two drivers to test
//        Driver d1 = new Driver("Suleiman","Kharroub","02/04/1997","soly","1234");
//        mDatabase.child("Drivers").child(key).setValue(d1);
//        key = FirebaseDatabase.getInstance().getReference("Drivers").push().getKey();
//        Driver d2 = new Driver("Mohammad","Mohammad","18/03/1996","moh","1234");
//        mDatabase.child("Drivers").child(key).setValue(d2);


        loginFragment = new LoginFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_Activity_frame_layout, loginFragment)
                .commit();


    }


    //Done with this -  Login Button Logic type user: soly - pass: 1234
    //Test
    @Override
    public void login(String user, String pass) {
        if (isNotEmpty(user) && isNotEmpty(pass)) {
            final Boolean[] flag = {false};
            final String mUsername = user;
            final String mPassword = pass;
            final String[] compareUser = {""};
            final String[] comparePass = {""};
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Drivers");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    for (DataSnapshot ds : dataSnapshot.getChildren()
                            ) {
                        compareUser[0] = ds.getValue(Driver.class).getUserName();
                        comparePass[0] = ds.getValue(Driver.class).getPassword();
                        if (mUsername.equals(compareUser[0].toString()) && mPassword.equals(comparePass[0].toString())) {
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor e = sharedPreferences.edit();
                            e.putString("fname", ds.getValue(Driver.class).getFirstName());
                            e.putString("lname", ds.getValue(Driver.class).getLastName());
                            e.putString("username", ds.getValue(Driver.class).getUserName());
                            e.putString("key", ds.getKey());
                            e.commit();
                            flag[0] = true;
                        }
                    }
                    if (flag[0]) {
                        MainScreenFragment mainScreenFragment = new MainScreenFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_Activity_frame_layout, mainScreenFragment)
                                .commit();
                    } else {
                        Toast.makeText(MainActivity.this, "Cannot find user", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                }
            });


        } else
            Toast.makeText(this, "Fill the fields", Toast.LENGTH_SHORT).show();
        //TODO: For testing purpose - To be removed later.x
        MainScreenFragment mainScreenFragment = new MainScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_Activity_frame_layout, mainScreenFragment)
                .commit();


    }

    @Override
    public void register() {
        RegisterFragment registerFragment = new RegisterFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_Activity_frame_layout, registerFragment)
                .commit();

    }


    @Override
    public void openMaps() {
        if (!requestRuntimePermissions()) {

            startService(new Intent(this, GPSService.class));

            GMapFragment mapFragment = new GMapFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_Activity_frame_layout, mapFragment)
                    .commit();
        }
    }

    @Override
    public void stopGPSService() {

        //stop service
        stopService(new Intent(this, GPSService.class));

        //go back to mainScreenFragment
        MainScreenFragment mainScreenFragment = new MainScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_Activity_frame_layout, mainScreenFragment)
                .commit();

    }

    private boolean requestRuntimePermissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSIONS_REQUEST_CODE &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED)
            openMaps();
        else
            requestRuntimePermissions();
    }



    @Override
    public void openAddCars() {
        AddCarFragment addCarFragment = new AddCarFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_Activity_frame_layout, addCarFragment)
                .commit();
    }


    @Override
    public void submit(String fname, String lname, String dateOfBirth, String username, String password) {
        Driver newUser = new Driver(fname, lname, dateOfBirth, username, password);
        String key = FirebaseDatabase.getInstance().getReference("Drivers").push().getKey();
        mDatabase.child("Drivers").child(key).setValue(newUser);

        LoginFragment loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_Activity_frame_layout, loginFragment)
                .commit();

        Toast.makeText(this, "User Created ! Login now.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancel() {
        Toast.makeText(this, "You canceled registration! ", Toast.LENGTH_SHORT).show();
        LoginFragment loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_Activity_frame_layout, loginFragment)
                .commit();

    }


    public boolean isNotEmpty(String s) {
        if (s.trim().length() > 0)
            return true;
        else
            return false;
    }

    @Override
    protected void onDestroy() {
        //Log.i("SHOW", "OnDestroy() was called");
        //TODO: handle the problem of fragments when rotating the view.
        super.onDestroy();
    }

    @Override
    public void cancelAddCar() {
        MainScreenFragment mainScreenFragment = new MainScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_Activity_frame_layout, mainScreenFragment)
                .commit();

        Toast.makeText(this, "You cancelled adding car", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void submitCar(String make, String model, String year, String milage) {
        Car newCar = new Car(make, model, year, Integer.parseInt(milage));
        mDatabase.child("Drivers").child(sharedPreferences.getString("key", "-1")).child("userCar").setValue(newCar);


    }
}

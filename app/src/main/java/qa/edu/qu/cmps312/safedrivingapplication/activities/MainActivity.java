package qa.edu.qu.cmps312.safedrivingapplication.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

    static final int REQUEST_CHECK_SETTINGS = 32;
    static final int REGISTER_CAR_REQUEST_CODE = 301;
    static final int PERMISSIONS_REQUEST_CODE = 22;
    static final int POST_UPDATE = 322;

    GPSService mServer;
    GPSService.GPSBinder mGPSBinder;
    boolean mBounded;
    LoginFragment loginFragment;
    ArrayList<Car> tempList;
    DatabaseReference mDatabase;
    SharedPreferences sharedPreferences;
    FusedLocationProviderClient mFusedLocationProviderClient;
    public static Location mStartingLocation;
    LocationRequest mLocationRequest;


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


    @SuppressLint("MissingPermission")
    @Override
    public void openMaps() {
        if (!requestRuntimePermissions()) {

            //open GPS service if not available
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            createLocationRequest();
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            mStartingLocation = location;
                            //Toast.makeText(MainActivity.this, location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MainActivity.this, GPSService.class);
                            startService(intent);
                            if(!mBounded)
                                bindService(intent, mConnection, 0);

                            GMapFragment mapFragment = new GMapFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_Activity_frame_layout, mapFragment)
                                    .commit();
                            //  startActivity(new Intent(MainActivity.this, MapActivity.class));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //get User to enable location settings
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });




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

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGPSBinder = (GPSService.GPSBinder)service;
            mServer = mGPSBinder.getServerInstance();
            mBounded = true;

            //TODO: use mServer to reflect data live on map.

            //Log.d("Binding", "Bounded to the service");
            Toast.makeText(MainActivity.this, "Bounded to service", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServer = null;
            mBounded = false;
            //Log.d("Binding", "Unbounded from service");
            Toast.makeText(MainActivity.this, "Unbounded from service", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onStart() {
        Intent intent = new Intent(this, GPSService.class);
        bindService(intent, mConnection, 0);
        Log.i("AutoBinding", "Binding");
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("AutoBinding", "On stop unbinding");
        mBounded = false;
        unbindService(mConnection);
        Log.i("AutoBinding", "UnBinding");
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //TODO: save the data sent through message object and create 'get' methods for those values, and call those methods from fragment to sow them on map.
            /*switch (msg.what) {
                case POST_UPDATE:
                    if (progressDialog.isShowing()) {
                        progressDialog.setProgress((int) msg.obj);
                    }
                    break;
            }*/
            super.handleMessage(msg);
        }
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

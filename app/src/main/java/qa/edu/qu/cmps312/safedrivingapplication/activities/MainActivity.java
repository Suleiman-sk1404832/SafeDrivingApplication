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
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
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
import qa.edu.qu.cmps312.safedrivingapplication.fragments.ManageCarFragment;
import qa.edu.qu.cmps312.safedrivingapplication.fragments.RegisterFragment;
import qa.edu.qu.cmps312.safedrivingapplication.fragments.StatisticsFragment;
import qa.edu.qu.cmps312.safedrivingapplication.models.Car;
import qa.edu.qu.cmps312.safedrivingapplication.models.User;
import qa.edu.qu.cmps312.safedrivingapplication.services.GPSService;

public class MainActivity extends AppCompatActivity implements LoginFragment.SuccessfulLogin,
        MainScreenFragment.MainScreenInterface, RegisterFragment.RegisterInterface,
        AddCarFragment.AddCarInterface, GMapFragment.MapInterface, ManageCarFragment.ManageCarInterface, StatisticsFragment.StatisticsFragmentInterface {

    public static final int UPDATE_LOCATION = 122;
    public static final int UPDATE_SPEED = 123;
    static final int REQUEST_CHECK_SETTINGS = 12;
    static final int REGISTER_CAR_REQUEST_CODE = 301;
    static final int PERMISSIONS_REQUEST_CODE = 22;
    public static Location mStartingLocation;
    int mCurrentFragment;
    LoginFragment loginFragment;
    ArrayList<Car> tempList;
    DatabaseReference mDatabase;
    SharedPreferences sharedPreferences;
    GPSService mServer;
    GPSService.GPSBinder mGPSBinder;
    boolean mBounded;
    String mBossKey;
    ArrayList<String> mBossContacts;
    ArrayList<String> mBossName;
    FusedLocationProviderClient mFusedLocationProviderClient;
    LocationRequest mLocationRequest;
    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGPSBinder = (GPSService.GPSBinder) service;
            mServer = mGPSBinder.getServerInstance();
            mServer.setMessenger(new Messenger(new MyHandler()));
            mBounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServer = null;
            mBounded = false;
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("currentFragment", mCurrentFragment);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mCurrentFragment = savedInstanceState.getInt("currentFragment", 0);
        // remove the lingering login fragment, solution to login fragment showing behind other fragments
        getSupportFragmentManager().beginTransaction().remove(this.loginFragment).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentFragment = savedInstanceState.getInt("currentFragment", 0);

            switch (mCurrentFragment) {
                case 0:
                    LoginFragment loginFragment = new LoginFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_Activity_frame_layout, loginFragment)
                            .commit();
                    break;
                case 1:
                    MainScreenFragment mainScreenFragment = new MainScreenFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_Activity_frame_layout, mainScreenFragment)
                            .commit();
                    break;
                case 2:
                    RegisterFragment registerFragment = new RegisterFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_Activity_frame_layout, registerFragment)
                            .commit();
                    break;
                case 3:
                    GMapFragment mapFragment = new GMapFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_Activity_frame_layout, mapFragment)
                            .commit();
                    break;
                case 4:
                    AddCarFragment addCarFragment = new AddCarFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_Activity_frame_layout, addCarFragment)
                            .commit();
                    break;
                case 5:
                    ManageCarFragment manageCarFragment = new ManageCarFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_Activity_frame_layout, manageCarFragment)
                            .commit();
                    break;
                case 6:
                    StatisticsFragment statFragment = new StatisticsFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_Activity_frame_layout, statFragment)
                            .commit();
                    break;
            }
        }
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = this.getSharedPreferences("MySharedPrefs", MODE_PRIVATE);
        mBossContacts = new ArrayList<>();
        mBossName = new ArrayList<>();
        refreshContacts();
        tempList = new ArrayList<>();

        loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_Activity_frame_layout, loginFragment)
                .commit();
        mCurrentFragment = 0;


    }

    @Override
    public void login(String user, String pass) {
        if (isNotEmpty(user) && isNotEmpty(pass)) {
            final Boolean[] flag = {false};
            final String mUsername = user;
            final String mPassword = pass;
            final String[] compareUser = {""};
            final String[] comparePass = {""};
            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Drivers");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        compareUser[0] = ds.getValue(User.class).getUserName();
                        comparePass[0] = ds.getValue(User.class).getPassword();
                        if (comparePass[0] != null && compareUser[0] != null) {
                            if (mUsername.equals(compareUser[0]) && mPassword.equals(comparePass[0])) {
                                SharedPreferences.Editor e = sharedPreferences.edit();
                                e.putString("fname", ds.getValue(User.class).getFirstName());
                                e.putString("lname", ds.getValue(User.class).getLastName());
                                e.putString("username", ds.getValue(User.class).getUserName());
                                e.putString("type", ds.getValue(User.class).getType());
                                e.putString("key", ds.getKey());
                                if (ds.getValue(User.class).getUserCar() != null)
                                    e.putInt("mileage", ds.getValue(User.class).getUserCar().getMilage());
                                e.apply();
                                flag[0] = true;
                                mBossKey = sharedPreferences.getString("BossKey","NotBossKey");
                                if(!mBossContacts.isEmpty()) // set boss contacts (drivers)
                                    myRef.child(mBossKey).child("contacts").setValue(mBossContacts);
                                if(!mBossName.isEmpty() && !ds.getKey().equals(mBossKey)) // set current user contact (boss)
                                    myRef.child(sharedPreferences.getString("key","-1")).child("contacts").setValue(mBossName);
                            }

                            if (flag[0] && mCurrentFragment != 1) {

                                MainScreenFragment mainScreenFragment = new MainScreenFragment();
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.main_Activity_frame_layout, mainScreenFragment)
                                        .commit();
                                mCurrentFragment = 1;
                            } else {
                                // Toast.makeText(MainActivity.this, "Cannot find user", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                }

            });


        } else
            Toast.makeText(this, "Fill the fields", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void register() {
        RegisterFragment registerFragment = new RegisterFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_Activity_frame_layout, registerFragment)
                .commit();
        mCurrentFragment = 2;

    }

    @SuppressLint("MissingPermission")
    @Override
    public void openMaps() {
        boolean hasPermission = requestRuntimePermissions();
        if (!hasPermission) {

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            createLocationRequest();
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            mStartingLocation = location;
                            Intent intent = new Intent(MainActivity.this, GPSService.class);
                            startService(intent);
                            if (!mBounded)
                                bindService(intent, mConnection, 0);

                            GMapFragment mapFragment = new GMapFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_Activity_frame_layout, mapFragment)
                                    .commit();
                            mCurrentFragment = 3;

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

        }
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
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

            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                    }
                }
            }
        });


    }

    @Override
    public void stopGPSService(int flag) {

        //stop service
        stopService(new Intent(this, GPSService.class));
        //if service is stopped with an error
        if (flag == -1)
            return;

        //go back to mainScreenFragment
        MainScreenFragment mainScreenFragment = new MainScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_Activity_frame_layout, mainScreenFragment)
                .commit();
        mCurrentFragment = 1;

    }

    private boolean requestRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED)
            openMaps();
        else
            requestRuntimePermissions();
    }

    @Override
    protected void onStart() {
        Intent intent = new Intent(this, GPSService.class);
        bindService(intent, mConnection, 0);
//        Log.i("SHOW", "onStart() was called");
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBounded = false;
        unbindService(mConnection);
//        Log.i("SHOW", "onStop() was called");
    }

    @Override
    public void openAddCars() {
        AddCarFragment addCarFragment = new AddCarFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_Activity_frame_layout, addCarFragment)
                .commit();
        mCurrentFragment = 4;
    }

    @Override
    public void openManageCar() {
        ManageCarFragment manageCarFragment = new ManageCarFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_Activity_frame_layout, manageCarFragment)
                .commit();
        mCurrentFragment = 5;
    }

    @Override
    public void showStats() {
        StatisticsFragment statisticsFragment = new StatisticsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_Activity_frame_layout, statisticsFragment)
                .commit();
        mCurrentFragment = 6;
    }

    @Override
    public void logOut() {
        LoginFragment loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_Activity_frame_layout, loginFragment)
                .commit();
    }


    @Override
    public void submit(String fname, String lname, String dateOfBirth, String username, String password, String type) {
        User newUser = new User(fname, lname, dateOfBirth, username, password);
        newUser.setType(type);
        String key = FirebaseDatabase.getInstance().getReference("Drivers").push().getKey();
        mDatabase.child("Drivers").child(key).setValue(newUser);

        LoginFragment loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_Activity_frame_layout, loginFragment)
                .commit();
        mCurrentFragment = 0;

        Toast.makeText(this, "User Created ! Login now.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancel() {
        Toast.makeText(this, "You canceled registration! ", Toast.LENGTH_SHORT).show();
        LoginFragment loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_Activity_frame_layout, loginFragment)
                .commit();
        mCurrentFragment = 0;

    }

    public void refreshContacts(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Drivers");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SharedPreferences.Editor e = sharedPreferences.edit();
                mBossContacts.clear();
                mBossName.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.getKey().length() > 10)
                    if(ds.getValue(User.class).getType().equals("Driver")){ // if a user is driver
                        mBossContacts.add(ds.getValue(User.class).getUserName());
                    }
                    else {// a boss
                        mBossName.add(ds.getValue(User.class).getUserName());
                        e.putString("BossKey",ds.getKey()); // save his key
                        e.apply();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean isNotEmpty(String s) {
        if (s.trim().length() > 0)
            return true;
        else
            return false;
    }

    @Override
    protected void onDestroy() {
//        Log.i("SHOW", "OnDestroy() was called");
        stopGPSService(-1); // if service is running, then service was forced to stop from activity, stop service with error flag
        super.onDestroy();
    }

    @Override
    public void cancelAddCar() {
        MainScreenFragment mainScreenFragment = new MainScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_Activity_frame_layout, mainScreenFragment)
                .commit();
        mCurrentFragment = 1;

        Toast.makeText(this, "You cancelled adding car", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void submitCar(String make, String model, String year, String milage) {
        Car newCar = new Car(make, model, year, Integer.parseInt(milage));
        mDatabase.child("Drivers").child(sharedPreferences.getString("key", "-1")).child("userCar").setValue(newCar);

    }


    @Override
    public void cancelManageCar() {
        MainScreenFragment mainScreenFragment = new MainScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_Activity_frame_layout, mainScreenFragment)
                .commit();
        mCurrentFragment = 1;

        Toast.makeText(this, "You cancelled managing car", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void submitManagedCar(String make, String model, String year, String milage) {
        Car newCar = new Car(make, model, year, Integer.parseInt(milage));
        mDatabase.child("Drivers").child(sharedPreferences.getString("key", "-1")).child("userCar").setValue(newCar);
    }

    @Override
    public void back() {
        MainScreenFragment mainScreenFragment = new MainScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_Activity_frame_layout, mainScreenFragment)
                .commit();
        mCurrentFragment = 1;
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            GMapFragment mapFragment = (GMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null)
                switch (msg.what) {
                    case UPDATE_LOCATION: {
                        Location location = ((Location) msg.obj);
                        if(mCurrentFragment == 3)
                        mapFragment.updateCurrentPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                        break;
                    }
                    case UPDATE_SPEED: {

                        mapFragment.updateRoadSpeed((float) msg.obj);
                        break;
                    }
                }
            super.handleMessage(msg);
        }
    }


}

package qa.edu.qu.cmps312.safedrivingapplication.services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import qa.edu.qu.cmps312.safedrivingapplication.R;
import qa.edu.qu.cmps312.safedrivingapplication.activities.MainActivity;
import qa.edu.qu.cmps312.safedrivingapplication.models.Trip;
import qa.edu.qu.cmps312.safedrivingapplication.models.User;

/**
 * Created by Mohamad Alsokromy on 3/2/2018.
 */

public class GPSService extends Service {

    public static final int ONE_SEC = 1000;
    public static final int ONE_MIN = 60 * ONE_SEC;
    final static int NOTIFICATION_ID = 23;
    final static int SPEEDING1_NOTIFICATION_ID = 24;
    final static int SPEEDING2_NOTIFICATION_ID = 25;
    final static String CHANNEL_ID = "notify_001";
    final static float KM_HOURS = 3.6f;
    private static final String TAG = "Client";
    private static final int SAFE_SPEED_LIMIT = 20; //km/h
    private static final int ABNORMAL_SPEED_LIMIT = 40; //km/h
    private static final int RISKY_SPEED_LIMIT = 60; //km/h
    private static final int TOP_SPEED_LIMIT = 80; //km/h
    LocationManager mLocationManager;
    LocationListener mLocationListener;
    DatabaseReference mDatabase;
    SharedPreferences sharedPreferences;
    float mTotSpeed = 0;
    float mSpeedCount = 0;
    float mTotDangerTime = 0;
    float mTotTripTime = 0;
    float mTotDistance = 0;
    BroadcastReceiver mScreenOffStateReceiver;
    BroadcastReceiver mScreenOnStateReceiver;
    boolean mScreenOn = true;
    //private ArrayList<Location> mLocations;
    GPSBinder mBinder = new GPSBinder();
    private Messenger mClientMessenger;
    //public static final String URL1 = "https://nominatim.openstreetmap.org/reverse?format=json";
    //public static final String URL2 = "http://overpass-api.de/api/interpreter?data=[out:json];";


    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        //mLocations = new ArrayList<>();

        sharedPreferences = this.getSharedPreferences("MySharedPrefs", MODE_PRIVATE);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mScreenOffStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mScreenOn = false;
            }
        };
        mScreenOnStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mScreenOn = true;
            }
        };
        registerReceiver(mScreenOffStateReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
        registerReceiver(mScreenOnStateReceiver, new IntentFilter("android.intent.action.SCREEN_ON"));

        mLocationListener = new LocationListener() {
            long startTime = 0;
            long endTime = 0;
            boolean isFirstTimeAboveLimit = true;
            //int locationCounter = 0;
            int friendlyCounter = 0; // a counter that increments only if user is steadily driving between 10-40 KM/H to give some friendly Toasts
            Location prevLocation = null;
            @Override
            public void onLocationChanged(Location location) {
                //float driverSpeed= ((Math.abs(new Random().nextFloat()%2)+20)*3.6f); //Simulation Code
                float driverSpeed = location.getSpeed()*KM_HOURS;

                //TODO: Updating location of the current user.
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Drivers");
                myRef.child(sharedPreferences.getString("key", "-1")).child("latitude").setValue(location.getLatitude());
                myRef.child(sharedPreferences.getString("key", "-1")).child("longitude").setValue(location.getLongitude());


                //uncomment to test..
                //playSoundNotification(getApplicationContext());
                //notifyUser("80");
                //notifyUser("100");

                mTotSpeed += driverSpeed;
                if(driverSpeed!= 0) // driver is not moving
                    mSpeedCount+=1;

                //send location to map fragment to use on map
                try {
                    if (mClientMessenger!=null) {
                        mClientMessenger.send(Message.obtain(null, MainActivity.UPDATE_LOCATION,
                                location));
                        mClientMessenger.send(Message.obtain(null, MainActivity.UPDATE_SPEED,
                                driverSpeed));
                    }
                } catch (RemoteException e) {
                    Log.e(TAG, "REMOTE EXCEPTION");
                }

                /*locationCounter++;
                if(locationCounter == 5) { // add location every 5 location updates
                    //mLocations.add(location);
                    locationCounter = 0;
                    //TODO: execute async task every 5 location updates to obtain speed limit.
                    //new OSMIdTask().execute(URL1+"&lat="+location.getLatitude()+"&lon="+location.getLongitude());
                }*/

                if(driverSpeed > TOP_SPEED_LIMIT && mScreenOn ) {
                    // above 80 KM/H and screen is on, VERY DANGEROUS, play sound intensively
                    if(isFirstTimeAboveLimit) { // if first time going above speed limit
                        startTime = location.getTime(); // record starting time
                        isFirstTimeAboveLimit = false;
                    }
                    Toast.makeText(getApplicationContext(), "DANGEROUS DRIVING!!!", Toast.LENGTH_SHORT).show();
                    playSoundNotification(getApplicationContext());
                    playSoundNotification(getApplicationContext());
                    playSoundNotification(getApplicationContext());
                    friendlyCounter = 0;
                }

                else if (driverSpeed > (TOP_SPEED_LIMIT+20) && !mScreenOn){
                    // above 100 KM/H and screen is off, NOT SAFE, make Alarming sound
                    if(isFirstTimeAboveLimit) { // if first time going above speed limit
                        startTime = location.getTime(); // record starting time
                        isFirstTimeAboveLimit = false;
                    }
                    playSoundNotification(getApplicationContext());
                    notifyUser("100");
                    friendlyCounter = 0;
                }

                else if (driverSpeed > TOP_SPEED_LIMIT && !mScreenOn) {
                    // above 80 KM/H and screen is off, NOT SAFE, notify
                    if (isFirstTimeAboveLimit) { // if first time going above speed limit
                        startTime = location.getTime(); // record starting time
                        isFirstTimeAboveLimit = false;
                    }
                    notifyUser("80");
                    friendlyCounter = 0;
                }

                else if (driverSpeed > (RISKY_SPEED_LIMIT) && mScreenOn){
                    // above 60 KM/H and screen is on, NOT SAFE, make alarming sound
                    if(isFirstTimeAboveLimit) { // if first time going above speed limit
                        startTime = location.getTime(); // record starting time
                        isFirstTimeAboveLimit = false;
                    }
                    playSoundNotification(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "You Are Not Driving Safely!!!", Toast.LENGTH_SHORT).show();
                    friendlyCounter = 0;
                }

                else if (driverSpeed > (RISKY_SPEED_LIMIT) && !mScreenOn){
                    // above 60 KM/H and screen is off, SAFE
                    if(!isFirstTimeAboveLimit){
                        endTime = location.getTime();
                        mTotDangerTime += ((endTime - startTime)/ONE_SEC);
                        isFirstTimeAboveLimit = true;
                    }
                    friendlyCounter = 0;
                }

                else if (driverSpeed > (ABNORMAL_SPEED_LIMIT) && mScreenOn){
                    // above 40 KM/H and screen is on, NOT SAFE, notify
                    if(isFirstTimeAboveLimit) { // if first time going above speed limit
                        startTime = location.getTime(); // record starting time
                        isFirstTimeAboveLimit = false;
                    }
                    //notifyUser();
                    Toast.makeText(getApplicationContext(), "Eyes On The Road, Please Stop Using Your Phone", Toast.LENGTH_LONG).show();
                    friendlyCounter = 0;
                }

                else if (driverSpeed > (ABNORMAL_SPEED_LIMIT) && !mScreenOn){
                    // above 40 KM/H and screen is off, SAFE
                    if(!isFirstTimeAboveLimit){
                        endTime = location.getTime();
                        mTotDangerTime += ((endTime - startTime)/ONE_SEC);
                        isFirstTimeAboveLimit = true;
                    }
                    friendlyCounter = 0;
                }

                else if (driverSpeed > SAFE_SPEED_LIMIT && mScreenOn){
                    // above 20 KM/H and screen is on, NOT SAFE, less friendly Toast
                    if(isFirstTimeAboveLimit) {
                        startTime = location.getTime();
                        isFirstTimeAboveLimit = false;
                    }
                    friendlyCounter++;
                    if(friendlyCounter == 10) {
                        Toast.makeText(getApplicationContext(), "Using Your Phone While Driving Can Become The Last Thing You Do!", Toast.LENGTH_LONG).show();
                        friendlyCounter = 0;
                    }
                }

                else if (driverSpeed > 10 && driverSpeed < SAFE_SPEED_LIMIT && mScreenOn){
                    // above 10 KM/H and below 20 KM/H and screen is on, SEMI SAFE, friendly Toast
                    if(isFirstTimeAboveLimit) { // if first time driving dangerously
                        startTime = location.getTime(); // record starting time
                        isFirstTimeAboveLimit = false;
                    }
                    friendlyCounter++;
                    if(friendlyCounter == 10) {
                        Toast.makeText(getApplicationContext(), "Please Refrain From Phone Usage While Driving :]", Toast.LENGTH_LONG).show();
                        friendlyCounter = 0;
                    }
                }

                else if (driverSpeed < SAFE_SPEED_LIMIT && !mScreenOn) {
                    // below 20 KM/H and screen is off, SAFE
                    if(!isFirstTimeAboveLimit){ //went above speed limit before,
                                                //i.e. not already below,
                                                //i.e. ended a dangerous driving interval
                        endTime = location.getTime();
                        mTotDangerTime += ((endTime - startTime)/ONE_SEC); // save total time in seconds
                        isFirstTimeAboveLimit = true; // reset boolean so we can calculate if driver passes speed limit again
                    }
                    friendlyCounter = 0;
                }

                if(prevLocation!=null) {
                    mTotTripTime = prevLocation.getTime() - location.getTime();
                }
                else {
                    prevLocation = location;
                    mTotTripTime = 0;
                }
                // mTotDistance == Mileage,  in Kilo Meters
                mTotDistance += driverSpeed* mTotTripTime;


/*
                mTotSpeed += driverSpeed; //Simulation Code
                mSpeedCount+=1;
                mTotDangerTime+= (Math.abs(new Random().nextLong()%2000))+8000; //Simulation Code, add 10 seconds.
                Log.i("Results", "Latest dangerous driving time in seconds: "
                        +mTotDangerTime*0.001+'\n'+"Average speed in Km/H: "+(mTotSpeed/mSpeedCount));*/
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {}
            @Override
            public void onProviderDisabled(String s) {
                //get User to enable location settings
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };


        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (mLocationManager != null) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5*ONE_SEC, 0, mLocationListener);
        }
        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.car2)
                .setOngoing(true)
                .setContentTitle("Safe Driving is ON")
                .setContentText("Click to access Safe Driving App")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);
        startForeground(NOTIFICATION_ID, notification.build());

    }

    public void playSoundNotification(Context context) {
        boolean found = false;
        RingtoneManager manager = new RingtoneManager(context);
        manager.setType(RingtoneManager.TYPE_NOTIFICATION);
        Cursor cursor = manager.getCursor();
        while (cursor.moveToNext()) { // testing to see what are the tones in the current device
            String name = cursor.getString((RingtoneManager.TITLE_COLUMN_INDEX));
            if(name.equals("Tinkerbell")) {
                found = true;
                break;
            }
        }
        if(!found)
            cursor.moveToFirst();
        String id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX);
        String uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);
        Ringtone r = RingtoneManager.getRingtone(context, Uri.parse(uri+"/"+id));
        r.play();
        while(r.isPlaying());
    }

    public void notifyUser(String speed){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.car2)
                .setContentTitle("Speeding Notification")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        createNotificationChannel();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        switch (speed){
            case "80":
                mBuilder.setContentText("Your speed has exceeded "+speed+"KM/H! Please be careful.");
                notificationManager.notify(SPEEDING1_NOTIFICATION_ID, mBuilder.build());
                break;
            case "100":
                mBuilder.setContentText("Your speed has exceeded "+speed+"KM/H! Please slow down.");
                notificationManager.notify(SPEEDING2_NOTIFICATION_ID, mBuilder.build());
            break;
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Speeding Channel";
            String description = "Channel human readable title";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public float getTotDangerTimeInMin() {
        return mTotDangerTime /ONE_MIN;
    }

    public float getTotTripTimeInMin() {
        return mTotTripTime /ONE_MIN;
    }

    public float getTripAvgSpeed() {
        if(mSpeedCount == 0) // to avoid exception
            return 0;
        return ((mTotSpeed/mSpeedCount));
    }

    public float getMileage(){
        return mTotDistance;
    }

    @Override
    public void onDestroy() {
        //noinspection MissingPermission
        mLocationManager.removeUpdates(mLocationListener);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
        unregisterReceiver(mScreenOffStateReceiver);
        unregisterReceiver(mScreenOnStateReceiver);

        if (getTotTripTimeInMin() >= 1) { // there was actually a trip!
            Toast.makeText(getApplicationContext(), "Driving Session Data Saved", Toast.LENGTH_LONG).show();
            final Trip trip = new Trip(getTotTripTimeInMin(), getTotDangerTimeInMin(), getMileage(), getTripAvgSpeed());
            //TODO: Add the statistics gathered in the trip class to the data already available in FireBase.

            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Drivers");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(User.class).getTrip() == null) {
                        Trip trip = new Trip(getTotTripTimeInMin(), getTotDangerTimeInMin(), getMileage(), getTripAvgSpeed());
                        trip.setNoOfTrips(1);
                        myRef.child(sharedPreferences.getString("key", "-1")).child("Trip").setValue(trip);
                    } else
                        trip.setNoOfTrips(dataSnapshot.getValue(User.class).getTrip().getNoOfTrips() + 1);
                        myRef.child(sharedPreferences.getString("key", "-1")).child("Trip").setValue(trip);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

            Log.i("RESULTS", "Total Trip Time: " +getTotTripTimeInMin()+" Min \n"
                    +"Total Distance Traveled: "+getMileage()+" KM \n"
                    +"Average Speed: "+getTripAvgSpeed()+" KM/H \n"
            +"Total Dangerous Driving Time: "+getTotDangerTimeInMin()+" Min");
        }
    }

    @Override
    public IBinder onBind(Intent intent){return mBinder;}

    @Override
    public boolean onUnbind(Intent intent) {
        mClientMessenger = null;
        return super.onUnbind(intent);
    }

    public void setMessenger(Messenger messenger) {
        mClientMessenger = messenger;
    }

    public class GPSBinder extends Binder {
        public GPSService getServerInstance() {
            return GPSService.this;
        }
    }

}

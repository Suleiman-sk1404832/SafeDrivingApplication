package qa.edu.qu.cmps312.safedrivingapplication.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Random;

import qa.edu.qu.cmps312.safedrivingapplication.R;
import qa.edu.qu.cmps312.safedrivingapplication.activities.MainActivity;

/**
 * Created by Mohamad Alsokromy on 3/2/2018.
 */

public class GPSService extends Service {

    LocationManager mLocationManager;
    LocationListener mLocationListener;
    float mTotSpeed = 0;
    float mSpeedCount = 0;
    float mTotTime = 0;
    NotificationManager mNotificationManager;
    Notification mNotification;
    final static int NOTIFICATION_ID = 23;
    public static final int ONE_SEC = 1000;
    public static final int ONE_MIN = 60;
    private static final int SPEED_LIMIT = 20; //km/h
    final static float KM_HOURS = 3.6f;
    BroadcastReceiver mScreenOffStateReceiver;
    BroadcastReceiver mScreenOnStateReceiver;
    boolean mScreenOn = true;
    private ArrayList<Location> mLocations;
    GPSBinder mBinder = new GPSBinder();
    //TODO: use mClientMessenger to send the data to myHandler in the main activity.
    private Messenger mClientMessenger;


    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        mLocations = new ArrayList<>();

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
            int locationCounter = 0;
            @Override
            public void onLocationChanged(Location location) {
                //TODO: Calculate the total time and average speed differently as the update interval is not constant.
                //float speed= ((Math.abs(new Random().nextFloat()%2)+20)*3.6f); //Simulation Code

                float speed = location.getSpeed()*KM_HOURS;

                locationCounter++;
                if(locationCounter == 5) { // add location every 5 location updates
                    mLocations.add(location);
                    locationCounter = 0;
                }

                if(speed > SPEED_LIMIT && mScreenOn ) { // if higher than speed limit and screen is on
                    if(isFirstTimeAboveLimit) {// if first time going above speed limit
                        startTime = location.getTime(); // record starting time
                        isFirstTimeAboveLimit = false;
                    }
                    mTotSpeed += speed;
                    mSpeedCount+=1;
                }

                else if (speed > SPEED_LIMIT && !mScreenOn){
                    // if higher than speed limit but screen is off
                }

                else if (speed < SPEED_LIMIT && mScreenOn){
                    // if lower than speed limit and screen is on
                }

                else if (speed < SPEED_LIMIT && !mScreenOn) {// below speed limit and screen is off (safely driving)
                    if(!isFirstTimeAboveLimit){ //went above speed limit before,
                                                //i.e. not already below,
                                                //i.e. ended a dangerous driving interval
                        endTime = location.getTime();
                        mTotTime += ((endTime - startTime)/ONE_SEC); // save total time in seconds
                        isFirstTimeAboveLimit = true; // reset boolean so we can calculate if driver passes speed limit again
                    }
                }


                /*mTotSpeed += speed; //Simulation Code
                mSpeedCount+=1;
                mTotTime+= (Math.abs(new Random().nextLong()%2000))+8000; //Simulation Code, add 10 seconds.
                Log.i("Results", "Latest dangerous driving time in seconds: "
                        +mTotTime*0.001+'\n'+"Average speed in Km/H: "+(mTotSpeed/mSpeedCount));*/
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
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, ONE_SEC, 0, mLocationListener);
        }

        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mNotification = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setOngoing(true)
                .setContentTitle("Safe Driving is ON")
                .setContentText("Click to access Safe Driving App")
                .setContentIntent(pendingIntent).build();
        startForeground(NOTIFICATION_ID, mNotification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public float getTotalTimeInMinutes() {
        return mTotTime/ONE_MIN;
    }

    public float getAverageSpeed() {
        if(mTotTime == 0) // to avoid exception
            return 0;
        return ((mTotSpeed/mSpeedCount));
    }

    @Override
    public void onDestroy() {
        //noinspection MissingPermission
        mLocationManager.removeUpdates(mLocationListener);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.cancel(NOTIFICATION_ID);
        }
        unregisterReceiver(mScreenOffStateReceiver);
        unregisterReceiver(mScreenOnStateReceiver);

        float timeInMinutes = getTotalTimeInMinutes();
        float avgSpeed = getAverageSpeed();
        if (timeInMinutes != 0) {
            //TODO: Save mLocations array list and both average speed and total time to FireBase.
            Toast.makeText(getApplicationContext(), "Driving Session Data Saved", Toast.LENGTH_LONG).show();
            Log.i("Results", "Total Dangerous Driving Time in minutes: "
                    +timeInMinutes+'\n'+"Average Speed in KM/H: "+avgSpeed);
        }
    }

    @Override
    public IBinder onBind(Intent intent){return mBinder;}

    @Override
    public boolean onUnbind(Intent intent) {
        //TODO: set whatever communication object between activity and service (ex: Messenger) to null;
        return super.onUnbind(intent);
    }

    public class GPSBinder extends Binder {
        public GPSService getServerInstance() {
            return GPSService.this;
        }
    }

}

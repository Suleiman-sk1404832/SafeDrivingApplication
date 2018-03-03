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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import java.util.ArrayList;

import qa.edu.qu.cmps312.safedrivingapplication.R;
import qa.edu.qu.cmps312.safedrivingapplication.activities.MainActivity;

/**
 * Created by Mohamad Alsokromy on 3/2/2018.
 */

public class GPSService extends Service {

    LocationManager mLManager;
    LocationListener mLListener;
    float totSpeed = 0;
    float mTotTime = 0;
    NotificationManager mNM;
    Notification mNotify;
    final static int NOTIFICATION_ID = 23;
    public static final int ONE_SEC = 1000;
    public static final int ONE_MIN = 60*ONE_SEC;
    private static final int SPEED_LIMIT = 20; //km/h
    final static float KM_HOURS = (float)3.6;
    BroadcastReceiver mScreenOffStateReceiver;
    BroadcastReceiver mScreenOnStateReceiver;
    boolean mScreenOn = true;
    private ArrayList<Location> mLocations;

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

        mLListener = new LocationListener() {
            long startTime = 0;
            long endTime = 0;
            boolean isFirstTimeAboveLimit = true;
            int counter = 0;

            @Override
            public void onLocationChanged(Location location) {
                float speed=120; //Simulation Code, adds 40KM/h*10

                /*float speed = location.getSpeed();

                if(speed > (SPEED_LIMIT/KM_HOURS) && mScreenOn ) { // if higher than speed limit and screen is on
                    if(isFirstTimeAboveLimit) {// if first time going above speed limit
                        startTime = location.getTime(); // record starting time
                        isFirstTimeAboveLimit = false;
                    }
                    totSpeed += speed;
                }

                else if (speed > (SPEED_LIMIT/KM_HOURS) && !mScreenOn){ // if higher than speed limit but screen is off

                }

                else if (speed < (SPEED_LIMIT/KM_HOURS) && mScreenOn){ // if lower than speed limit and screen is on

                }

                else {// below speed limit and screen is off (safely driving)
                    if(!isFirstTimeAboveLimit){ //went above speed limit before,
                                                //i.e. not already below,
                                                //i.e. ended a dangerous driving interval
                        endTime = (location.getTime() - ONE_SEC);
                        mTotTime += (endTime - startTime);
                        isFirstTimeAboveLimit = true; // reset boolean so we can calculate if driver passes speed limit again
                    }
                }
                if(counter >= 10) { // add location every 10 or more seconds
                    mLocations.add(location);
                    counter = 0;
                }
                counter++;
                */
                totSpeed += speed; //Simulation Code
                mTotTime+=10000; //Simulation Code, add 10 seconds.
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

        mLManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //noinspection MissingPermission
        mLManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, ONE_SEC, 0, mLListener);

        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mNotify = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setOngoing(true)
                .setContentTitle("Safe Driving is ON")
                .setContentText("Click to access Safe Driving App")
                .setContentIntent(pendingIntent).build();
        startForeground(NOTIFICATION_ID, mNotify);

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
        return ((totSpeed*KM_HOURS)/(mTotTime/ONE_SEC));
    }

    @Override
    public void onDestroy() {
        //noinspection MissingPermission
        mLManager.removeUpdates(mLListener);
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNM.cancel(NOTIFICATION_ID);
        unregisterReceiver(mScreenOffStateReceiver);
        unregisterReceiver(mScreenOnStateReceiver);

        float newTotalTime = getTotalTimeInMinutes();
        float newAverageSpeed = getAverageSpeed();
        if (newTotalTime != 0) {
            //TODO: Save mLocations array list and both average speed and total time.
            Toast.makeText(getApplicationContext(), "Driving Session Data Saved", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public IBinder onBind(Intent intent){return null;}
}

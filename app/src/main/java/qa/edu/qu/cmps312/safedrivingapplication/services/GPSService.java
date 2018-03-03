package qa.edu.qu.cmps312.safedrivingapplication.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Mohamad Alsokromy on 3/2/2018.
 */

public class GPSService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

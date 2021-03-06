package com.mitra.abhik.humansoftheworld.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by abmitra on 6/28/2015.
 */
public class PostAuthenticatorService extends Service {
    private AbstractAccountAuthenticator mAuthenticator;
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

    @Override
    public void onCreate() {
        mAuthenticator = new PostsAuthenticator(getApplicationContext());
        super.onCreate();
    }
}

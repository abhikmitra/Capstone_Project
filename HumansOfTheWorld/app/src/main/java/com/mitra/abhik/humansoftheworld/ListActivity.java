package com.mitra.abhik.humansoftheworld;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           getWindow().setSharedElementEnterTransition(reenterTransition());
            getWindow().setSharedElementExitTransition(exitTransition());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }
    private Transition exitTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ChangeBounds bounds = new ChangeBounds();
            bounds.setInterpolator(new BounceInterpolator());
            bounds.setDuration(10000);
            return bounds;
        }
        return null;
    }

    private Transition reenterTransition() {
        ChangeBounds bounds = new ChangeBounds();
        bounds.setInterpolator(new OvershootInterpolator());
        bounds.setDuration(10000);

        return bounds;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

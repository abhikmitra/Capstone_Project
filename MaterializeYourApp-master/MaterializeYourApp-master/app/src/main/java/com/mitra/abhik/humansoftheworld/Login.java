package com.mitra.abhik.humansoftheworld;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Login extends AppCompatActivity {
    Uri animated_logo_uri = Uri.parse("res:///" + R.drawable.human_animation);
    //Uri animated_logo_uri = Uri.parse("file:///android_asset/human_animation");
    float fbIconScale = 1.45F;
    private String FBTAG = "FACEBOOK";
    CallbackManager callbackManager;
    @Bind(R.id.humans_logo_login)
    SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.humanText)
    TextView humanText;
    @Bind(R.id.login_button)
    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Typeface tf=Typeface.createFromAsset( getAssets(),"fonts/athletic.ttf");
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(animated_logo_uri)
                .setAutoPlayAnimations(true)
                .build();
        mSimpleDraweeView.setController(controller);
        humanText.setTypeface(tf);
//        Glide.with(this)
//                .load(animated_logo_uri)
//                .into(mSimpleDraweeView);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            setUpFacebookLogin();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will

        return super.onOptionsItemSelected(item);
    }

    public void customizeFacebookButton(){
        Activity hostActivity =this;
        Drawable drawable = hostActivity.getResources().getDrawable(
                com.facebook.R.drawable.com_facebook_button_icon);
        drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth()*fbIconScale),
                (int)(drawable.getIntrinsicHeight()*fbIconScale));
        loginButton.setCompoundDrawables(drawable, null, null, null);
        loginButton.setCompoundDrawablePadding(hostActivity.getResources().
                getDimensionPixelSize(R.dimen.fb_margin_override_textpadding));
        loginButton.setPadding(
                hostActivity.getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_lr),
                hostActivity.getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_top),
                0,
                hostActivity.getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_bottom));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        customizeFacebookButton();
    }
    protected void setUpFacebookLogin(){
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(FBTAG, "Login to facebook successful");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {
                Log.d(FBTAG, "Login to facebook has been cancelled by the user.");
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Facebook Login Failure")
                        .setMessage("You need to login with Facebook to proceed.This app does not post to your wall")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e(FBTAG, "Login to facebook FAILED with : " + exception);
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Facebook Login Failure")
                        .setMessage("Something went wrong while logging in through facebook")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }
}

package com.aa.quote.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import com.aa.quote.R;
import com.aa.quote.callBack.ServerCall;
import com.aa.quote.databinding.ActivitySplashscreenBinding;
import com.aa.quote.model.Quote;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashscreenActivity extends AppCompatActivity {

    private final static int RC_SIGN_IN = 1;
    private FirebaseAuth fbAuth;
    private ActivitySplashscreenBinding B;
    private GoogleApiClient googleApiClient;
    private ArrayList<Quote> QuotesList = null;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        B = DataBindingUtil.setContentView(this, R.layout.activity_splashscreen);

        init_Views();
        manageGoogleApiClient();

    }

    @Override
    public void onStart() {
        super.onStart();
        fbAuth.addAuthStateListener(authStateListener);
    }

    //this function animate the splashscreen view objects
    private void init_Views() {

        try {
            B.videoView.setRawData(R.raw.vid);
            B.videoView.setLooping(true);
            B.videoView.prepare(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    B.videoView.start();
                }
            });
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        B.videoView.animate().alpha(.3f).setDuration(1200).withEndAction(new Runnable() {
            @Override
            public void run() {


                Animation animate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
                animate.reset();

                B.logo.clearAnimation();
                B.logo.startAnimation(animate);
                B.logo.animate().alpha(1).setDuration(1350);

                animate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move2);
                animate.reset();

                B.logoName.clearAnimation();
                B.logoName.startAnimation(animate);
                B.logoName.animate().alpha(1).setDuration(1600);

                B.powerdBy.animate().alpha(1f).setDuration(3300);

            }
        });



    }

    //region GoogleSignIn

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {

                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, getResources().getString(R.string.Toast_something_wrong), Toast.LENGTH_SHORT).show();

            }


        }

    }

    private void manageGoogleApiClient() {

        fbAuth = FirebaseAuth.getInstance();


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {

                    B.progressBar.animate().alpha(.9f).setDuration(1800);
                    callRetrofit();

                } else {

                    initiate_Signin();

                }

            }
        };


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.Toast_something_wrong), Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


    }

    //this function initialize the sign in button
    private void initiate_Signin() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                B.signInButton.animate().alpha(1f).setDuration(1000);
            }
        }, 3000);

        B.googleButtonRippleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });


    }

    private void googleSignIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.Toast_sign_in_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    //endregion

    //api call
    private void callRetrofit() {

        new ServerCall(new Callback<ArrayList<Quote>>() {

            @Override
            public void onResponse(@NonNull Call<ArrayList<Quote>> call, @NonNull Response<ArrayList<Quote>> response) {

                QuotesList = response.body();

                setUserData();

                B.progressBar.animate().alpha(0f).setDuration(4000).withEndAction(new Runnable() {
                    @Override
                    public void run() {

                        String KEY = getResources().getString(R.string.QUOTE_FEED_ARGUMENTS_KEY);

                        Intent i = new Intent(SplashscreenActivity.this, MainActivity.class);
                        i.putParcelableArrayListExtra(KEY, QuotesList);
                        startActivity(i);


                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 6000);

            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Quote>> call, @NonNull Throwable t) {

                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.Toast_check_connection), Toast.LENGTH_SHORT).show();

                B.progressBar.animate().alpha(0f).setDuration(4000).withEndAction(new Runnable() {
                    @Override
                    public void run() {

                        Intent i = new Intent(SplashscreenActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                });


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 6000);



            }
        });

    }

    private void setUserData() {

        String userNameKey = getResources().getString(R.string.USER_NAME_KEY);
        String userImgKey = getResources().getString(R.string.USER_IMG_KEY);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        pref.edit().putString(userNameKey, Objects.requireNonNull(fbAuth.getCurrentUser()).getDisplayName()).apply();
        pref.edit().putString(userImgKey, Objects.requireNonNull(fbAuth.getCurrentUser().getPhotoUrl()).toString()).apply();

    }

}
package com.aa.quote.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.aa.quote.R;
import com.aa.quote.databinding.FragmentSettingsBinding;
import com.aa.quote.viewmodels.QuotesListViewModel;
import com.aa.quote.widgets.QuoteWidget;
import com.google.android.gms.ads.AdRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.mayurrokade.minibar.UserMessage;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkEventListener;
import lib.kingja.switchbutton.SwitchMultiButton;


public class SettingsFragment extends Fragment {

    private Context context;
    private Resources r;
    private FragmentSettingsBinding B;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String userNameKey,userImgKey,userName,userImg;
    private String RECYCLER_SETTING_KEY,RECYCLER_DEFAULT_SETTING,RECYCLER_SETTING_HIDE,RECYCLER_SETTING_SHOW;
    private String WIDGET_SETTING_KEY,WIDGET_DEFAULT_SETTING,WIDGET_SETTING_FAV, WIDGET_SETTING_RANDOM;
    private QuotesListViewModel viewModel;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth fbAuth;
    private Activity activity;
    private Handler handler;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context=context;
        activity=getActivity();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        B = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        viewModel = ViewModelProviders.of(this).get(QuotesListViewModel.class);

        initPrefrence();
        initViews();
        initListners();
        handler=new Handler();

        return B.getRoot();

    }

    @Override
    public void onStart() {
        super.onStart();

        fbAuth=FirebaseAuth.getInstance();
        fbAuth.addAuthStateListener(authStateListener);

    }

    @SuppressLint("CommitPrefEdits")
    private void initPrefrence() {

        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
        r=getResources();


        userNameKey=getResources().getString(R.string.USER_NAME_KEY);
        userImgKey=getResources().getString(R.string.USER_IMG_KEY);

        userName= pref.getString(userNameKey," ");
        userImg= pref.getString(userImgKey,r.getString(R.string.USER_DEFAULT_IMG));


        RECYCLER_SETTING_KEY = r.getString(R.string.RECYCLER_SETTING_KEY);
        RECYCLER_DEFAULT_SETTING = r.getString(R.string.RECYCLER_DEFAULT_SETTING);
        RECYCLER_SETTING_HIDE = r.getString(R.string.RECYCLER_SETTING_HIDE);
        RECYCLER_SETTING_SHOW = r.getString(R.string.RECYCLER_SETTING_SHOW);

        WIDGET_SETTING_KEY = r.getString(R.string.WIDGET_SETTING_KEY);
        WIDGET_DEFAULT_SETTING = r.getString(R.string.WIDGET_DEFAULT_SETTING);
        WIDGET_SETTING_FAV = r.getString(R.string.WIDGET_SETTING_FAV);
        WIDGET_SETTING_RANDOM = r.getString(R.string.WIDGET_SETTING_RANDOM);


        if (!pref.getString(RECYCLER_SETTING_KEY, RECYCLER_DEFAULT_SETTING).equals(RECYCLER_DEFAULT_SETTING)) {
            B.swtichRecylcer.setSelectedTab(1);
        }

        if (!pref.getString(WIDGET_SETTING_KEY, WIDGET_DEFAULT_SETTING).equals(WIDGET_DEFAULT_SETTING)) {
            B.swtichWidget.setSelectedTab(1);
        }

    }

    private void initListners() {

        B.swtichRecylcer.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {

                if (pref.getString(RECYCLER_SETTING_KEY, RECYCLER_DEFAULT_SETTING).equals(RECYCLER_SETTING_SHOW)) {

                    updateSetting(RECYCLER_SETTING_KEY,RECYCLER_SETTING_HIDE);

                }else{

                    updateSetting(RECYCLER_SETTING_KEY,RECYCLER_SETTING_SHOW);

                }


            }
        });

        B.swtichWidget.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {

                if (pref.getString(WIDGET_SETTING_KEY, WIDGET_DEFAULT_SETTING).equals(WIDGET_SETTING_RANDOM)) {

                    updateSetting(WIDGET_SETTING_KEY, WIDGET_SETTING_FAV);
                    triggerWidgetUpdate();

                }else{

                    updateSetting(WIDGET_SETTING_KEY,WIDGET_SETTING_RANDOM);
                    triggerWidgetUpdate();
                }

            }


        });


        B.signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmSignOut();
            }
        });

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (fbAuth.getCurrentUser() == null) {

                    Intent i = context.getPackageManager()
                                .getLaunchIntentForPackage(context.getPackageName());
                        assert i != null;
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(i);
                        activity.finish();

                }

            }


        };
    }

    private void initViews() {

        //user data
        B.userName.setText( userName );
        try {
            Picasso.get()
                    .load(userImg)
                    .error(R.drawable.splash)
                    .into(B.userImg);
        } catch (Exception e) { e.printStackTrace(); }

        viewModel.getLiveCount().observe(this, new Observer<Integer>() {

            @Override
            public void onChanged(@Nullable Integer integer) {
                B.favouritesNumber.setText(String.valueOf(integer));
            }

        });


    }

    final Runnable animator = new Runnable() {
        @Override
        public void run() {

            try{

                B.heart1.playAnimation();
                B.heart2.playAnimation();

            }finally {

                handler.postDelayed(animator,2000);

            }

        }
    };



    private void updateSetting(String key, String value){


        String message=r.getString(R.string.MINBIBAR_TEXT);

        UserMessage userMessage = new UserMessage.Builder()
                .with(context)
                .setBackgroundColor(R.color.colorSuccess)
                .setTextColor(android.R.color.white)
                .setMessage(message)
                .setDuration(900)
                .setShowInterpolator(new AccelerateInterpolator())
                .setDismissInterpolator(new AccelerateInterpolator())
                .build();

        B.Toast.show(userMessage);
        editor.putString(key,value);
        editor.apply();

    }

    //create the popup window
    private void confirmSignOut() {

        String fName=userName.split(" ")[0];


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") final View popupView = inflater.inflate(R.layout.popup_window, null);

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final PopupWindow popupWindow = new PopupWindow(popupView, popupView.getMeasuredWidth(), popupView.getMeasuredHeight(), true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        popupWindow.setIgnoreCheekPress();
        popupView.animate().alpha(1f).setDuration(800);


        TextView warnning=popupView.findViewById(R.id.warning);
        Button confirm=popupView.findViewById(R.id.confirm);
        Button cancle=popupView.findViewById(R.id.cancle);

        String w= fName+this.getResources().getString(R.string.WARNING_MESSAGE);
        warnning.setText(w);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupView.animate().alpha(0f).setDuration(800).withEndAction(new Runnable() {
                    @Override
                    public void run() {


                        viewModel.dropQuotes();
                        popupWindow.dismiss();
                        removeUserData();
                        fbAuth.signOut();

                    }
                });

            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupView.animate().alpha(0f).setDuration(800).withEndAction(new Runnable() {
                    @Override
                    public void run() {

                   popupWindow.dismiss();

                    }
                });
            }
        });



    }

    //triggers the home widget Update function
    private void triggerWidgetUpdate(){
        Intent intent = new Intent(context, QuoteWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, QuoteWidget.class));

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);

        }

    private void removeUserData(){

        editor.remove(userNameKey);
        editor.remove(userImgKey);
        editor.remove(RECYCLER_SETTING_KEY);
        editor.remove(WIDGET_SETTING_KEY);
        editor.apply();

    }


    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(animator);
    }


    @Override
    public void onResume() {
        super.onResume();
        animator.run();

    }
}
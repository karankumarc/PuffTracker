package com.projects.karan.pufftracker.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.projects.karan.pufftracker.R;
import com.projects.karan.pufftracker.model.Cigarette;
import com.projects.karan.pufftracker.utils.Constants;
import com.projects.karan.pufftracker.utils.Utils;

import java.util.ArrayList;
import java.util.Date;

public class Dashboard extends AppCompatActivity{

    private static final String TAG = Dashboard.class.getSimpleName();

    private Firebase baseSmokingUrl;
    private String DATE = Utils.SIMPLE_DATE_FORMAT.format(new Date());
    private static int count, maxCount, totalCount;
    private ArrayList<Firebase> pushIds = new ArrayList<Firebase>();

    private TextView textViewWelcomeMessage, textViewCount, textViewTipOfTheDay;
    private ProgressBar progressBar;

    private ValueEventListener tipOfTheDayValueEvent, welcomeMessageValueEvent, countValueEvent,
            maxCountValueEvent, totalCountValueEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        baseSmokingUrl = new Firebase(Constants.FIREBASE_URL_SMOKING);
        initializeUi();

    }

    private void initializeUi() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textViewWelcomeMessage = (TextView) findViewById(R.id.textViewWelcome);
        textViewCount = (TextView) findViewById(R.id.textViewCount);
        textViewTipOfTheDay = (TextView) findViewById(R.id.textViewTipOfTheDay);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    /**
     * Attaching all value event listeners
     */
    @Override
    protected void onStart() {
        super.onStart();

        //region Total Count ValueEventListener
        totalCountValueEvent = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Integer.class)!=null || dataSnapshot.getValue(Integer.class)!= 0){
                    totalCount = dataSnapshot.getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        baseSmokingUrl.child(Constants.FIREBASE_LOCATION_TOTAL_COUNT).addValueEventListener(totalCountValueEvent);
        //endregion

        //region Max Count ValueEventListener
        maxCountValueEvent = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Integer.class)!=null){
                    maxCount = dataSnapshot.getValue(Integer.class);
                    updateUIProgressBarCountView();
                }
                else{
                    maxCount = 20;
                    updateUIProgressBarCountView();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        baseSmokingUrl.child(Constants.FIREBASE_LOCATION_MAX_COUNT).addValueEventListener(maxCountValueEvent);
        //endregion

        //region Tip Of The Day ValueEventListener
        tipOfTheDayValueEvent =new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String newTipOfTheDay = dataSnapshot.getValue(String.class);
                if(newTipOfTheDay.isEmpty()){
                    textViewTipOfTheDay.setText("");
                } else {
                    textViewTipOfTheDay.setText(newTipOfTheDay);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        baseSmokingUrl.child(Constants.FIREBASE_LOCATION_TIP_OF_THE_DAY).addValueEventListener(tipOfTheDayValueEvent);
        //endregion

        //region Welcome Message ValueEventListener
        welcomeMessageValueEvent = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String welcomeMessage = dataSnapshot.getValue(String.class);
                if(welcomeMessage.isEmpty()){
                    textViewWelcomeMessage.setText(getResources().getString(R.string.default_welcome_message));
                } else {
                    textViewWelcomeMessage.setText(welcomeMessage);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        baseSmokingUrl.child(Constants.FIREBASE_LOCATION_WELCOME_MESSAGE).addValueEventListener(welcomeMessageValueEvent);
        //endregion

        //region Count ValueEventListener
        countValueEvent = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Integer.class)!=null){
                    count = dataSnapshot.getValue(Integer.class);
                    updateUIProgressBarCountView();
                }
                else{
                    count = 0;
                    updateUIProgressBarCountView();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        baseSmokingUrl.child(Constants.FIREBASE_LOCATION_TRACKER).child(DATE).child(Constants.FIREBASE_LOCATION_COUNT).addValueEventListener(countValueEvent);
        //endregion

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean handled=false;

        if(Utils.isNetworkAvailable(this)){
            if(item.getItemId() == R.id.action_smoked){
                handled = true;
                addCigaretteUpdateFirebaseAndUi();
            } else if(item.getItemId() == R.id.action_cancel_last_added){
                handled = true;
                removeCigaretteUpdateFirebaseAndUi();
            }
        } else {
            showNoNetworkDialog();
        }

        return handled;
    }

    private void removeCigaretteUpdateFirebaseAndUi() {

        if(pushIds.size() > 0){
            //region Firebase updates
            Firebase dateRef = new Firebase(Constants.FIREBASE_URL_TRACKER+"/"+ DATE);
            count--;
            totalCount--;
            dateRef.child(Constants.FIREBASE_LOCATION_COUNT).setValue(count);
            baseSmokingUrl.child(Constants.FIREBASE_LOCATION_TOTAL_COUNT).setValue(totalCount);

            pushIds.get(pushIds.size()-1).removeValue();
            pushIds.remove(pushIds.size()-1);
            //endregion
        } else {
            Toast.makeText(Dashboard.this, "No cigarettes to cancel in this session", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!Utils.isNetworkAvailable(Dashboard.this)){
            showNoNetworkDialog();
        }
    }

    private void showNoNetworkDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.dialog_not_connected_message);
        alertDialogBuilder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if(!Utils.isNetworkAvailable(Dashboard.this)){
                    showNoNetworkDialog();
                }
            }
        }).setTitle(R.string.dialog_not_connected_title).setIcon(R.drawable.ic_internet_white_24dp);
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void addCigaretteUpdateFirebaseAndUi() {

        // To get unique device id i.e. IMEI number
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        //region Firebase updates
        Firebase dateRef = new Firebase(Constants.FIREBASE_URL_TRACKER+"/"+ DATE);
        count++;
        totalCount++;
        dateRef.child(Constants.FIREBASE_LOCATION_COUNT).setValue(count);
        baseSmokingUrl.child(Constants.FIREBASE_LOCATION_TOTAL_COUNT).setValue(totalCount);
        Cigarette cigarette = new Cigarette(telephonyManager.getDeviceId());
        pushIds.add(dateRef.push());
        pushIds.get(pushIds.size()-1).setValue(cigarette);
        //endregion
    }

    private void updateUIProgressBarCountView() {
        progressBar.setMax(maxCount);
        progressBar.setProgress(count);
        textViewCount.setText(""+count+"/"+maxCount);

        if(maxCount > count){
            textViewCount.setTextColor(getResources().getColor(R.color.green));
            progressBar.setBackgroundColor(getResources().getColor(R.color.green));
        }
        else {
            textViewCount.setTextColor(getResources().getColor(R.color.red));
            progressBar.setBackgroundColor(getResources().getColor(R.color.red));
        }
    }

    /**
     * Freeing up listeners used
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //region Remove event listeners (cleaning memory)
        baseSmokingUrl.removeEventListener(countValueEvent);
        baseSmokingUrl.removeEventListener(welcomeMessageValueEvent);
        baseSmokingUrl.removeEventListener(tipOfTheDayValueEvent);
        baseSmokingUrl.removeEventListener(maxCountValueEvent);
        baseSmokingUrl.removeEventListener(totalCountValueEvent);
        //endregion
    }


}

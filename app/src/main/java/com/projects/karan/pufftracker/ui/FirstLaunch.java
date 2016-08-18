package com.projects.karan.pufftracker.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.projects.karan.pufftracker.R;

public class FirstLaunch extends AppCompatActivity implements View.OnClickListener {

    Button buttonNotReady, buttonPromise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);

        SharedPreferences sharedPreferences = getSharedPreferences("PuffTracker", Context.MODE_PRIVATE);
        boolean promise = sharedPreferences.getBoolean("promised", false);

        if (promise) {
            Intent intent1 = new Intent(this, Dashboard.class);
            startActivity(intent1);
        }

        buttonNotReady = (Button) findViewById(R.id.buttonNotReady);
        buttonPromise = (Button) findViewById(R.id.buttonPromise);

        buttonNotReady.setOnClickListener(this);
        buttonPromise.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonNotReady) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.disappointed_dialog_title);
            alertDialogBuilder.setMessage(R.string.disappointed_dialog);
            alertDialogBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    sendSmsMessage();
                    finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(FirstLaunch.this, R.string.disappointed_toast, Toast.LENGTH_SHORT).show();
                }
            }).setIcon(android.R.drawable.ic_menu_close_clear_cancel);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        } else if (view.getId() == R.id.buttonPromise) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.congratulations_dialog_title);
            alertDialogBuilder.setMessage(R.string.congratulations_dialog);
            alertDialogBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    SharedPreferences.Editor editor = getSharedPreferences("PuffTracker", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("promised", true).apply();
                    Intent intent = new Intent(FirstLaunch.this, Dashboard.class);
                    startActivity(intent);
                    finish();
                }
            });
            alertDialogBuilder.setIcon(R.drawable.cigarette_icon);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void sendSmsMessage() {
        String phoneNo = getString(R.string.sms_phone_number);
        String message = getString(R.string.sms_message);

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent. Please check your outbox!", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}

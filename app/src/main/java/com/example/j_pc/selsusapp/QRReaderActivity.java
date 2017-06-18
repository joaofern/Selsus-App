package com.example.j_pc.selsusapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class QRReaderActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    public static final int PERMISSION_REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;
    private Intent received;
    private String selected_key,selected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        received = getIntent();
        if (!haveCameraPermission())
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        else
            startCamera();
    }

    public void startCamera()
    {
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    public void stopCamera()
    {
        mScannerView.stopCamera();
    }

    private boolean haveCameraPermission()
    {
        if (Build.VERSION.SDK_INT < 23)
            return true;
        return checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        // This is because the dialog was cancelled when we recreated the activity.
        if (permissions.length == 0 || grantResults.length == 0)
            return;

        switch (requestCode)
        {
            case PERMISSION_REQUEST_CAMERA:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    startCamera();
                }
                else
                {
                    finish();
                }
            }
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {

        try {
            JSONObject selcomps = new JSONObject(received.getStringExtra("selcomps"));
            Iterator<?> keys = selcomps.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                if ( selcomps.get(key) instanceof JSONObject ) {
                    System.out.println(((JSONObject) selcomps.get(key)).get("selcompID").toString());
                    System.out.println(rawResult.getText());
                    if(((JSONObject) selcomps.get(key)).get("selcompID").toString().equals(rawResult.getText())){
                        selected = ((JSONObject)selcomps.get(key)).toString();
                        selected_key = key;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // show the scanner result into dialog box.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SelComp:");
        builder.setMessage(selected_key);
        AlertDialog alert1 = builder.create();
        alert1.show();

        new Timer().schedule(new TimerTask(){
            public void run() {
                QRReaderActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Intent next = new Intent(getApplicationContext(), ModeSelectActivity.class);
                        next.putExtra("key",selected_key);
                        next.putExtra("selcomp",selected);
                        startActivity(next);
                    }
                });
            }
        }, 1500);

    }

}

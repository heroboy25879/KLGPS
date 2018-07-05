package com.hitachi.com.klgps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {



    MasterServiceFunction masterServiceFunction = new MasterServiceFunction();
    DeviceInfo deviceInfo =new DeviceInfo(this);
    private GoogleApiClient googleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkIfAlreadyhavePermission()) {
            requestForSpecificPermission();
        }

        Log.d("KLGPS","Started");

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(mCallbacks)
                .addOnConnectionFailedListener(mOnFailed)
                .build();

        Button button = findViewById(R.id.btnStart);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleApiClient = new GoogleApiClient.Builder(v.getContext())
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(mCallbacks)
                        .addOnConnectionFailedListener(mOnFailed)
                        .build();
                onStart();
                Log.d("KLGPS","Started");



            }
        });
    }



    // GPS
    @Override
    protected void onStart() {
        if(googleApiClient != null) {
            googleApiClient.connect();

        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(googleApiClient != null && googleApiClient.isConnected())
            googleApiClient.disconnect();
        super.onStop();
    }

    private  GoogleApiClient.ConnectionCallbacks mCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
//            Location location = null;
//////            try {
//////                location = LocationServices.FusedLocationApi
//////                        .getLastLocation(googleApiClient);
//////            } catch (SecurityException ex) {
//////                Log.d("KLGPS","lat" + ex.getMessage());
//////            }
//////
//////            double lat = location.getLatitude();
//////            double lon = location.getLongitude();
//////
//////            Log.d("KLGPS","lat" + lat + "lon" + lon);

            int timeSet = 20000; // 2 min
            LocationRequest locationRequest = new LocationRequest()
                    .setInterval(timeSet)
                    .setFastestInterval(timeSet)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    ;
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient
                ,locationRequest
                ,mLocationListener);
                Button button = findViewById(R.id.btnStart);
                button.setEnabled(false);
            } catch (Exception e) {
                Log.d("KLGPS","Exception" + e.getMessage());
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    };

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();

            TextView lastTimeTextView = findViewById(R.id.txtLastTime);
            TextView latitudeTextView = findViewById(R.id.txtLatitude);
            TextView longitudeTextView = findViewById(R.id.txtLongitude);
            TextView lastUpdateTextView = findViewById(R.id.txtLastUpdate);

            String timeStamp = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(Calendar.getInstance().getTime());
            latitudeTextView.setText("Last Latitude : " + String.valueOf(lat));
            longitudeTextView.setText("Last Longitude : " + String.valueOf(lon));
            lastTimeTextView.setText("Last time : " + timeStamp);
            Log.d("KLGPS","lat" + lat + "  lon" + lon );

            // insert Lat Long
            JSONArray jsonArray = WebserviceExecute(masterServiceFunction.getInsertTrVehiclesMonitor()
                    +"/"+ deviceInfo.IMEI()
                    +"/"+ lat
                    +"/"+ lon
            );
            Log.d("KLGPS","jsonArray == > " + jsonArray);
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                if( Boolean.valueOf(jsonObject.getString("Result")))
                {
                    lastUpdateTextView.setText("Last Update : Success");
                }
                else
                {
                    lastUpdateTextView.setText("Last Update : Unsuccess");
                }
            } catch (Exception e) {
                Log.d("KLGPS","Exception" + e.getMessage());
                e.printStackTrace();
            }

        }
    };

    private GoogleApiClient.OnConnectionFailedListener mOnFailed =
            new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                }
            };
// End GPS
    private JSONArray WebserviceExecute(String UrlFunc)
    {

        try {
            Log.d("KLGPS","UrlFunc == > " + UrlFunc);
            FuncDBAccess funcDBAccess = new FuncDBAccess(this);
            funcDBAccess.execute(UrlFunc);
            Log.d("KLGPS","resultJSON == > " + funcDBAccess.get());
            String resultJSON = funcDBAccess.SetJSONResult(funcDBAccess.get());

            JSONArray jsonArray = new JSONArray(resultJSON);

            return  jsonArray;
        } catch (Exception e)
        {
            Log.d("KLGPS","Exception" + e.getMessage());
            e.printStackTrace();
            return  null;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //What is permission be request
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS
        }, 101);

    }

    //Check the permission is already have
    private boolean checkIfAlreadyhavePermission() {

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }




}

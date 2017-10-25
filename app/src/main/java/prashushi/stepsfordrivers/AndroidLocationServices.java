package prashushi.stepsfordrivers;

/**
 * Created by Dell User on 2/17/2017.
 */

import org.json.JSONArray;
import org.json.JSONObject;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class AndroidLocationServices extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private Location mLastLocation;
    GoogleApiClient mGoogleApiClient = null;

    String busId="11";
    String phone="";
    long time=0;

    private LocationManager locationManager;

    public AndroidLocationServices() {
        // TODO Auto-generated constructor stub
    }


    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        Log.e("Google", "Service Created");

    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        busId=intent.getStringExtra(Constants.BUS_ID);
        phone=intent.getStringExtra(Constants.PHONE);
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            System.out.println("*5*");

        }


        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Constants.printToast(getApplicationContext(), "Started with bus:"+busId);
        displayLocation();
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // TODO Auto-generated method stub
                //displayLocation();
                long current=System.currentTimeMillis()/1000;
                if(current-time>=5)
                {
                    displayLocation();
                    time=current;
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void displayLocation() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Constants.printToast(getApplicationContext(), "Permission not granted!");
        }

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }else
            Constants.printToast(getApplicationContext(),"Google services not available!");

        if(mGoogleApiClient!=null)
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        else
            Constants.printToast(getApplicationContext(),"Google API null!");

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            float bearings=mLastLocation.getBearing();
            String data="{\"bus_id\":"+busId+",\"longitude\":"+longitude+",\"latitude\":"+latitude+",\"bearings\":"+bearings+"}";
            ArrayList<String> params=new ArrayList<>();
            params.add("location");
            params.add(Constants.BUS_ID);
            params.add(Constants.PHONE);
            ArrayList<String> values=new ArrayList<>();
            values.add(data);
            values.add(busId);
            values.add(phone);
            new BackgroundTaskPost(Constants.TRACK_URL+"update_location", params, values, new BackgroundTaskPost.AsyncResponse() {
                @Override
                public void processFinish(String output, int code) {
                }
            }).execute();
        }else{
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        displayLocation();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }



    @Override
    public void onDestroy() {
//        wakeLock.release();
        // TODO Auto-generated method stub
        super.onDestroy();
        //  Constants.printToast(getApplicationContext(), "14");

//        new ToggleGPS(getApplicationContext()).turnGPSOff();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

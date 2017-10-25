package prashushi.stepsfordrivers;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Dell User on 3/9/2017.
 */
public class LoginDriver  extends AppCompatActivity implements View.OnClickListener {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient=null;
    EditText input, phone;
    TextView beginTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_driver);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (checkPlayServices()) {
            buildGoogleApiClient();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }else  Constants.printToast(getApplicationContext(),"Google services not available!");

        initLayouts();
        checkStatus();
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION , android.Manifest.permission.ACCESS_FINE_LOCATION },
                    MY_PERMISSIONS_REQUEST_LOCATION );
        }else
        findViewById(R.id.toggle_trip).setOnClickListener(this);
        //    findViewById(R.id.track).setOnClickListener(this);
    }

    private void initLayouts() {
    input= (EditText) findViewById(R.id.bus_id);
    phone= (EditText) findViewById(R.id.phone);
    beginTv= (TextView) findViewById(R.id.toggle_tv);
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).build();
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

    private boolean isMyServiceRunning() {
        Class<?> serviceClass=AndroidLocationServices.class;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toggleService(true);
                }
                else
                {
                    if (!canGetLocation()) {
                        showSettingsAlert();
                    }
                }
                return;
            }
        }
    }

    void checkStatus(){
        if(isMyServiceRunning())
        {
            beginTv.setText("Stop");
            input.setVisibility(View.INVISIBLE);
            phone.setVisibility(View.INVISIBLE);

        }
    }

    private void toggleService(boolean b) {
        Intent service=new Intent(this, AndroidLocationServices.class);
        if(b){
            String inputID=input.getText().toString();
            service.putExtra(Constants.BUS_ID, inputID);
            String inputPhone=phone.getText().toString();
            service.putExtra(Constants.PHONE, inputPhone);
            startService(service);
            beginTv.setText("Stop");
            input.setVisibility(View.INVISIBLE);
            phone.setVisibility(View.INVISIBLE);
        }else{
            stopService(service);
            beginTv.setText("Drive");
            input.setVisibility(View.VISIBLE);
            phone.setVisibility(View.VISIBLE);
        }
    }

    public boolean canGetLocation() {
        boolean result = true;
        LocationManager lm = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if (lm == null)
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        } catch (Exception ex) {
        }
        try {
            network_enabled = lm
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        } catch (Exception ex) {
        }
        if (gps_enabled == false || network_enabled == false) {
            result = false;
        } else {
            result = true;
        }

        return result;
    }

    public void showSettingsAlert() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Location Settings");

        // Setting Dialog Message
        alertDialog.setMessage("Share Location!");

        // On pressing Settings button
        alertDialog.setPositiveButton("Allow",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });


        alertDialog.show();
    }

//    int[] busIds=new int[]{11, 22, 33, 44};
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toggle_trip:

                if (!isMyServiceRunning()) {
                    String in=input.getText().toString();
                    if(in.length()==0)
                        break;
                    int inputID=Integer.parseInt(in);
                            toggleService(true);

                }else
                toggleService(false);
                break;
        }
    }
}

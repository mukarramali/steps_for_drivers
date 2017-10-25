package prashushi.stepsfordrivers;

        import android.content.Intent;
        import android.content.res.Resources;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Point;
        import android.location.Location;
        import android.net.Uri;
        import android.os.Handler;
        import android.os.SystemClock;
        import android.support.v4.app.FragmentActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.view.animation.Interpolator;
        import android.view.animation.LinearInterpolator;

        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.Projection;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MapStyleOptions;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;

        import static prashushi.stepsfordrivers.Constants.RESPONSE_DATA;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    Marker marker;
    Handler handler;
    Runnable runnable;
    int busId;
    String phone;
    double latitude, longitude;
    long bearings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        busId=getIntent().getIntExtra(Constants.BUS_ID, 0);
        phone=getIntent().getStringExtra(Constants.PHONE);
        latitude=getIntent().getDoubleExtra(Constants.LATITUDE, 0);
        longitude=getIntent().getDoubleExtra(Constants.LONGITUDE, 0);
        bearings=getIntent().getLongExtra(Constants.BEARINGS, 0);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        findViewById(R.id.contact).setOnClickListener(this);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ) );
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("XXX", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("XXX", "Can't find style. Error: ", e);
        }
        LatLng current = new LatLng(latitude, longitude);
        MarkerOptions markerOptions=new MarkerOptions().title("Your Bus").position(current).rotation(bearings)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("school_bus_icon", 50, 50)));

        marker=mMap.addMarker(markerOptions);


        handler=new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 5000);
                moveMarker();
                System.out.println("Running");
            }
        };
        handler.postDelayed(runnable, 5000);


        Constants.printToast(this, "Maps Loaded!");


    }
    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);

        return resizedBitmap;
    }

    void moveMarker(){
        ArrayList<String> params=new ArrayList<>();
        params.add(Constants.BUS_ID);
        ArrayList<String> values=new ArrayList<>();
        values.add(busId+"");

        new BackgroundTask(Constants.TRACK_URL+"track", params, values, new BackgroundTask.AsyncResponse() {

            @Override
            public void processFinish(String output, int code) {

                if(Constants.resposeCode(code)){
                    double lat=28.6398, lon=77.3384;
                    float bearings=0;
                    try {
                        JSONObject temp = new JSONObject(output);
                        JSONObject obj=temp.optJSONObject(RESPONSE_DATA);
                        lat=obj.optDouble("latitude");
                        lon=obj.optDouble("longitude");
                        bearings=obj.optLong("bearings");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LatLng current = new LatLng(lat, lon);
                    animateMarker(marker, current, false);
                    marker.setPosition(current);
                    marker.setRotation(bearings);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
                }
            }
        }).execute();        //zoom
    }

    void handle(int i){
        if(handler==null||runnable==null)
            return;
        switch (i){
            case 0://pause
                handler.removeCallbacks(runnable);
                break;//resume
            case 1:
                handler.postDelayed(runnable, 5000);
                break;
            case 2://destroy
                handler.removeCallbacks(runnable);
                break;

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        handle(0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handle(0);

    }

    @Override
    protected void onResume() {
        super.onResume();

        handle(2);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        handle(2);
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.contact&&phone!=null){
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);
        }
    }
}
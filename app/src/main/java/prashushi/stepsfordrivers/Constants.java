package prashushi.stepsfordrivers;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Dell User on 3/10/2017.
 */

public class Constants {
    public static String BUS_ID="bus_id";
    public static String PHONE="phone";
    public static String LATITUDE="latitude";
    public static String LONGITUDE="longitude";
    public static String BEARINGS="bearings";

    public Constants() {
        // TODO Auto-generated constructor stub
    }
    public static final String TRACK_URL = "http://farcon.in/location/location.php";
//  public static final String TRACK_URL = "http://192.168.43.236/sample/index.php";
//  public static final String TRACK_URL = "http://10.0.3.2/sample/index.php";
    public static final String MAPS_API = "AIzaSyAAHC8Zl_3fwPyqEUuBYzoIk4yu_x0GBOU";
    static public void printToast(Context context, String tag){
       // Toast.makeText(context, tag, Toast.LENGTH_SHORT).show();
    }
    public static final boolean resposeCode(int code){
        return code==200||code==202;
    }
}
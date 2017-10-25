package prashushi.stepsfordrivers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Dell User on 3/9/2017.
 */
public class ListBusActivity extends AppCompatActivity {
    ListView listView;
    int[] busIds=new int[]{11, 22, 33, 44};
    JSONArray array=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_bus);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView= (ListView) findViewById(R.id.list_bus);

        new BackgroundTask(Constants.TRACK_URL, new ArrayList<String>(), new ArrayList<String>(), new BackgroundTask.AsyncResponse() {
            @Override
            public void processFinish(String output, int code) {

                if(Constants.resposeCode(code)){
                    try {
                        array=new JSONArray(output);
                        String[] list=new String[array.length()];
                        for (int i=0;i<list.length;i++){
                            list[i]="Bus "+(i+1);
                        }
                        ArrayAdapter adapter=new ArrayAdapter(ListBusActivity.this,
                                R.layout.card_bus, R.id.tv_bus, list);
                        listView.setAdapter(adapter);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(ListBusActivity.this, MapsActivity.class);
                JSONObject obj=array.optJSONObject(position);
                double lat=obj.optDouble(Constants.LATITUDE);
                double lon=obj.optDouble(Constants.LONGITUDE);
                long bearings=obj.optLong(Constants.BEARINGS);
                String phone=obj.optString(Constants.PHONE);
                int busId=obj.optInt(Constants.BUS_ID);
                intent.putExtra(Constants.BUS_ID, busId);
                intent.putExtra(Constants.PHONE, phone);
                intent.putExtra(Constants.LATITUDE, lat);
                intent.putExtra(Constants.LONGITUDE, lon);
                intent.putExtra(Constants.BEARINGS, bearings);

                startActivity(intent);
            }
        });
    }
}

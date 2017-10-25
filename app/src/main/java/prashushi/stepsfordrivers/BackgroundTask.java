package prashushi.stepsfordrivers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;


class BackgroundTask extends AsyncTask<Void, Void, Boolean> {
    public AsyncResponse delegate = null;
    String url="";
    ArrayList<String> params, values;
    String result="";
    ProgressDialog pDialog;
    Context context;
    View parent;
    BackgroundTask THIS;
    int response_code=404;  //no_internet:1, not_found:404,400 , success:200
    BackgroundTask(String url, ArrayList<String> params, ArrayList<String> values, AsyncResponse delegate){
        this.url=url;
        this.params=params;
        this.values=values;
        this.delegate=delegate;

    }

    BackgroundTask(Context context, String url, ArrayList<String> params, ArrayList<String> values, AsyncResponse delegate){
        this.url=url;
        this.params=params;
        this.values=values;
        this.delegate=delegate;
        this.context=context;
        this.parent=parent;
        THIS=this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Boolean doInBackground(Void... param) {
        //________________load image

        HttpURLConnection conn=null;
        StringBuilder sb = new StringBuilder();
        try {
            if(params.size()>0)
                url+="?"+params.get(0)+"="+values.get(0);
            for(int i=1;i<values.size();i++) {
                url+="&"+params.get(i)+"="+values.get(i);
            }
            System.out.println(url);
            URL Url = new URL(url);
            conn = (HttpURLConnection) Url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            response_code=conn.getResponseCode();
            String line ;// Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            result=sb.toString();
        }catch(UnknownHostException e){
            response_code=1;
        }
        catch (IOException e) {
            e.printStackTrace();
            response_code=404;
            result="falsexxx";
        }
        return false;
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
        response_code=1;
        result = "falsexxx";
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        System.out.println(result);
        if(context!=null)
            delegate.processFinish(result, response_code);
        delegate.processFinish(result, response_code);
    }

    public interface AsyncResponse {
        void processFinish(String output, int code);

    }

}

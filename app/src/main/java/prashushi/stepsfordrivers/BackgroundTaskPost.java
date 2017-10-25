package prashushi.stepsfordrivers;


        import android.app.AlertDialog;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.os.AsyncTask;
        import android.util.Log;
        import android.widget.Toast;

//        import org.apache.http.client.ClientProtocolException;

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
        import java.util.Date;



class BackgroundTaskPost extends AsyncTask<Void, Void, Boolean> {
    public AsyncResponse delegate = null;
    String url="";
    ArrayList<String> params, values;
    String result="";
    Boolean loader;
    Context context;
    long time;
    AlertDialog alertDialog;
    String msg;
    ProgressDialog progressDialog;
    int response_code=404;  //no_internet:1, not_found:404,400 , success:200
    BackgroundTaskPost(String url, ArrayList<String> params, ArrayList<String> values, AsyncResponse delegate){
        this.url=url;
        this.params=params;
        this.values=values;
        this.delegate=delegate;
        this.loader = false;
        time = (new Date().getTime()) % 100000;
        System.out.println("Constructor1 time:" + time);
    }

    BackgroundTaskPost(Context context, String url, ArrayList<String> params, ArrayList<String> values, AsyncResponse delegate) {
        this.url = url;
        this.params = params;
        this.values = values;
        this.delegate = delegate;
        this.loader = true;
        this.context = context;
        time = (new Date().getTime()) % 100000;
        System.out.println("Constructor2 time:" + time);
    }

    BackgroundTaskPost(Context context, String msg, String url, ArrayList<String> params, ArrayList<String> values, AsyncResponse delegate) {
        this.url = url;
        this.params = params;
        this.values = values;
        this.delegate = delegate;
        this.loader = true;
        this.context = context;
        time = (new Date().getTime()) % 100000;
        this.msg = msg;
        System.out.println("Constructor2 time:" + time);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        time = (new Date().getTime()) % 100000;
        System.out.println("PreExecute Loader:" + time);

        if (loader) {
            progressDialog = new ProgressDialog(context);
            String title = "Loading...";
            if (msg != null)
                title = msg;
            progressDialog.setMessage(title);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }
        time = (new Date().getTime()) % 100000;
        System.out.println("PreExecute Loader2:" + time);
    }


    @Override
    protected Boolean doInBackground(Void... param) {
//        time=(new Date().getTime())%100000;
        //      System.out.println("doInBackground1:"+time);

        StringBuilder sb = new StringBuilder();
        try {
            String data = "";
            if (params.size()>0&&values.size()>0) {
//                System.out.println(params.get(0)+":"+values.get(0));
                data = URLEncoder.encode(params.get(0), "UTF-8") + "=" + URLEncoder.encode(values.get(0), "UTF-8");
                System.out.print(params.get(0) + ":" + values.get(0) + "  ");
            }
            for (int i = 1; i < values.size() && i < params.size(); i++)
            {    data += "&" + URLEncoder.encode(params.get(i), "UTF-8") + "=" + URLEncoder.encode(values.get(i), "UTF-8");
                System.out.print(params.get(i) + ":" + values.get(i) + "  ,");
            }
            System.out.println();
            System.out.println(url);
            URL Url = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            response_code=conn.getResponseCode();
            String line ;// Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            result=sb.toString();
            time = (new Date().getTime()) % 100000;
            System.out.println("doInBackground4:" + time);
        } catch(UnknownHostException e){
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
        Toast.makeText(context, context.getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        System.out.println(result);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        delegate.processFinish(result, response_code);
       // delegate.processFinish(result);
    }

    public interface AsyncResponse {
        //void processFinish(String output);
        void processFinish(String output, int code);

    }
}

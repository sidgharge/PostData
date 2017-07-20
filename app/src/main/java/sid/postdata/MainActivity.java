package sid.postdata;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;
    double lat = 0;
    double lng = 0;
    TextView t , n;
    EditText et;
    ProgressDialog pd;
    boolean getloc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t = (TextView) findViewById(R.id.cp);
        n = (TextView) findViewById(R.id.ncp);
        et = (EditText) findViewById(R.id.et);

        pd = new ProgressDialog(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "No acceess", Toast.LENGTH_LONG).show();
            askLocPer();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        pd.setMessage("Getting Location");
        pd.setTitle("Wait");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
        pd.show();
        getloc = true;

    }


    @Override
    public void onLocationChanged(Location location) {

        if (pd.isShowing() && getloc){
            getloc = false;
            pd.dismiss();
        }

        lat = location.getLatitude();
        lng = location.getLongitude();

        Log.d("latitude", String.valueOf(lat));
        Log.d("Longitude", String.valueOf(lng));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void onClick(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           askLocPer();
            return;
        }

        pd = new ProgressDialog(this);
        pd.setMessage("Uploading Location");
        pd.setTitle("Wait");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
        pd.show();

        sendData();

    }

    public void sendData(){

        final String json = formatData();

        new AsyncTask<Void , Void , String>(){

            @Override
            protected String doInBackground(Void... voids) {

                return getResponse(json);
            }

            @Override
            protected void onPostExecute(String s) {
                Log.d("post" , s);
                pd.dismiss();
            }
        }.execute();
    }

    String getResponse(String json) {
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://dev.citrans.net:8888/skymeet/poi/add");
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httpPost);

            return httpResponse.toString();

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }


        return result;

    }

    public String formatData(){

        JSONObject jo = new JSONObject();
        try {
            jo.put("id" , "abc");
            jo.put("version" , 0);
            jo.put("status" , "ACTIVE");
            jo.put("createdAt" , 0);
            jo.put("createdBy" , "abc");
            jo.put("updatedAt" , 0);
            jo.put("updatedBy" , "abc");
            jo.put("poiId" , "abc");
            jo.put("title" , et.getText().toString());
            jo.put("description" , "abc");
            jo.put("imageUrl" , "abc");


            JSONArray location = new JSONArray();
            location.put(lat);
            location.put(lng);

            jo.put("location" , location);
            return jo.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("problem" , "offline");
        }

        return null;
    }


    public void askLocPer() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission. ACCESS_FINE_LOCATION}, 99);
    }
}

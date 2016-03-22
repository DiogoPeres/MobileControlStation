package com.diogoperes.mobilecontrolstation.connectionHandler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ServerConnection  extends AsyncTask<String, String, JSONObject>  {

    private Activity mActivity;
    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;
    private static final String LOGIN_URL = "http://192.168.2.130/teste.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    //private ArrayList<UV> availableUVs;

    public ServerConnection(Activity mainActivity) {
        mActivity = mainActivity;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(mActivity);
        pDialog.setMessage("Attempting login...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    @Override
    protected JSONObject doInBackground(String... args) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("name", "Joao");
            params.put("password", "12345");

            Log.d("request", "starting");

            JSONObject json = jsonParser.makeHttpRequest(
                    LOGIN_URL, "POST", params);

            if (json != null) {
                Log.d("JSON result", json.toString());
                return json;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void onPostExecute(JSONObject json) {
        int success = 0;
        String message = "";
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        if (json != null) {
            Toast.makeText(mActivity, json.toString(),
                    Toast.LENGTH_LONG).show();
            try {
                success = json.getInt(TAG_SUCCESS);
                message = json.getString(TAG_MESSAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (success == 1) {
            Log.d("Success!", message);
        }else{
            Log.d("Failure", message);
        }
    }

}

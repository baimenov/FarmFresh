package Handler;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import Structure.PostParams;
import Structure.RegistrationDetails;
import group3.tcss450.uw.edu.farmfresh.Main2Activity;
import group3.tcss450.uw.edu.farmfresh.MainActivity;
import group3.tcss450.uw.edu.farmfresh.R;

/**
 * Created by Doseo on 11/3/2017.
 */

public class LoginPostHandler extends AsyncTask<PostParams, Integer, String>{


    MainActivity weakActivity;
    //android.support.v4.app.FragmentTransaction transaction;
    public LoginPostHandler(MainActivity activity) {
        weakActivity = activity;
        //transaction = ts;
    }

    @Override
    public String doInBackground(PostParams... params) {
        String response = "";
        HttpURLConnection urlConnection = null;
        String url = params[0].url;
        HashMap<String, String> map = params[0].postSet;
        try {
            URL urlObject = new URL(url);
            urlConnection = (HttpURLConnection) urlObject.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(map));

            writer.flush();
            writer.close();
            os.close();

            InputStream content = urlConnection.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String s = "";
            while ((s = buffer.readLine()) != null) {
                response += s;
            }
        } catch (Exception e) {
            response = "Unable to connect, Reason: "
                    + e.getMessage();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        Log.d("POST_RESPONse", response);
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        //details.load.setVisibility(View.GONE);
        // Something wrong with the network or the URL.
        if (response.startsWith("Unable to")) {
            Toast.makeText(weakActivity.getApplicationContext(), response, Toast.LENGTH_LONG)
                    .show();
            return;
        } else {
            try {
                JSONObject mainObject = new JSONObject(response);
                String message = mainObject.getString("message");
                Integer code = mainObject.getInt("code");
                if (code == 300) {
                    //success
                    weakActivity.startActivity(new Intent(weakActivity, Main2Activity.class));
                } else if (code == 200) {
                    //wrong login
                } else if (code == 201){
                    //wrong pass
                }
                Toast.makeText(weakActivity.getApplicationContext(),
                        message, Toast.LENGTH_LONG).show();
                weakActivity.getLoginProgressBar().setVisibility(ProgressBar.GONE);
                weakActivity.setLFREnabled(true);
                return;
            } catch (Exception ex) {
                //not JSON RETURNED
            }
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        weakActivity.getLoginProgressBar().setVisibility(ProgressBar.VISIBLE);
        weakActivity.getLoginProgressBar().setProgress(0);
        weakActivity.setLFREnabled(false);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        weakActivity.getLoginProgressBar().setProgress(values[0]);
    }



    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}

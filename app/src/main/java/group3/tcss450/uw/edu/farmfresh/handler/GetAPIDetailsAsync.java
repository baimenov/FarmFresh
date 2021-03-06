package group3.tcss450.uw.edu.farmfresh.handler;

import android.os.AsyncTask;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import group3.tcss450.uw.edu.farmfresh.R;
import group3.tcss450.uw.edu.farmfresh.SearchActivity;

import static group3.tcss450.uw.edu.farmfresh.util.Links.API_DETAILS_LINK;

/**
 * Async task that gets the details of the market through market ID using API.
 * Created by baimenov on 12/2/2017.
 */

public class GetAPIDetailsAsync extends AsyncTask<String, Void, String> {

    /**
     * SearchActivity.
     */
    private SearchActivity activity;

    //Filters to check for in products.
    private String[] myFilters;



    /**
     * Constructor
     * @param activity SearchActivity
     */
    public GetAPIDetailsAsync(SearchActivity activity) {
        this.activity = activity;
    }


    /**
     * Background task that gets the details of a specific market from the API.
     * @param details the market ID to search for when searching API.
     * @return JSON String of the market details.
     */
    @Override
    protected String doInBackground(String... details) {
        String response = "";
        HttpURLConnection urlConnection = null;
        try {
            URL urlObject = new URL(API_DETAILS_LINK + details[0]);
            urlConnection = (HttpURLConnection) urlObject.openConnection();
            InputStream content = urlConnection.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String s = "";
            while ((s = buffer.readLine()) != null) {
                response += s;
            }
        } catch (Exception ex) {
            response = "Unable to connect, Reason: " +
                    ex.getMessage();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        //Log.d("Details Response", response);
        return response;
    }

    /**
     * Execution after the background task is complete, filters through the JSON and displays
     * the text on the current fragment.
     * @param response The JSON string response sent back from the API.
     */
    @Override
    protected void onPostExecute(String response) {

        if (response.startsWith("Unable to")) {
            //Log.d("error","error DetailsAPI");
            return;
        } else {
            try {
                JSONObject js_result = new JSONObject(response);
                JSONObject details = new JSONObject(js_result.getString("marketdetails"));

                String address = (String) details.get("Address");
                String googleLink = (String) details.get("GoogleLink");
                String products = (String) details.get("Products");
                String schedule = (String) details.get("Schedule");

                TextView addressTV = (TextView) activity.findViewById(R.id.address_text_view);
                addressTV.setText("Address: " + address);

                TextView googleLinkTV = (TextView) activity.findViewById(R.id.maps_text_view);
                googleLinkTV.setText(Html.fromHtml("<a href=\"" + googleLink + "\">Directions</a>"));
                googleLinkTV.setClickable(true);
                googleLinkTV.setMovementMethod(LinkMovementMethod.getInstance());

                TextView productsTv = (TextView) activity.findViewById(R.id.products_text_view);
                productsTv.setText("Products: " + products.replace(';', ','));
                productsTv.setMovementMethod(new ScrollingMovementMethod());

                TextView scheduleTV = (TextView) activity.findViewById(R.id.schedule_text_view);
                scheduleTV.setText("Dates: " + schedule.substring(0, schedule.indexOf(';')));

            } catch (Exception ex) {

            }
        }
    }
}

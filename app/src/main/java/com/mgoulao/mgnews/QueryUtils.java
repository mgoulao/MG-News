package com.mgoulao.mgnews;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by msilv on 7/9/2017.
 */

public class QueryUtils {

    private static final String MG_TAG = "MG";
    private static final String RESPONSE = "response";
    private static final String RESULTS = "results";
    private static final String TITLE = "webTitle";
    private static final String SECTION = "sectionName";
    private static final String DATE = "webPublicationDate";
    private static final String URL = "webUrl";

    public QueryUtils(){
    }

    public ArrayList<New> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(MG_TAG, "Error closing input stream", e);
        }

        // Return the {@link Event}, value extracted from the JSON response
        return extractNews(jsonResponse);
    }

    /**
     * Return a list of {@link New} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<New> extractNews(String jsonResponse) {

        // Create an empty ArrayList that we can start adding News to
        ArrayList<New> NewsList = new ArrayList<>();

        // Try to parse the jsonResponse. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Parse the response given by the jsonResponse and
            // build up a list of New objects with the corresponding data.
            JSONObject jsonNew = new JSONObject(jsonResponse);

            JSONObject response = jsonNew.getJSONObject(RESPONSE);

            // Getting JSON Array node
            JSONArray results = response.getJSONArray(RESULTS);

            // looping through all the News
            for (int z = 0; z < results.length(); z++) {
                String title = "";
                String section = "";
                String date = "";
                String url = "";

                JSONObject currentNew = results.getJSONObject(z);

                title = currentNew.getString(TITLE);
                section = currentNew.getString(SECTION);
                date = currentNew.getString(DATE);
                url = currentNew.getString(URL);

                NewsList.add(new New(title, section, date, url));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }

        // Return the list of News
        return NewsList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(MG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(MG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(MG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

}

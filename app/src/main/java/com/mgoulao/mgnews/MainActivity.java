package com.mgoulao.mgnews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<New>> {

    private static final String MG_TAG = "MG";
    public String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?section=technology&api-key=test";
    ListView listView;
    ProgressBar progressBar;
    TextView offlineTextView , cantFind;
    NewsAdapter newsAdapter;
    ArrayList<New> newsList = new ArrayList<>();
    private static final int NEWS_LOADER_ID = 1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_tech:
                    progressBar.setVisibility(View.VISIBLE);
                    GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?section=technology&api-key=test";
                    getLoaderManager().restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
                    return true;
                case R.id.navigation_sport:
                    progressBar.setVisibility(View.VISIBLE);
                    GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?section=sport&api-key=test";
                    getLoaderManager().restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
                    return true;
                case R.id.navigation_environment:
                    progressBar.setVisibility(View.VISIBLE);
                    GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?section=environment&api-key=test";
                    getLoaderManager().restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.news_list_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        offlineTextView = (TextView) findViewById(R.id.offline);
        cantFind = (TextView) findViewById(R.id.cant_find);

        newsAdapter = new NewsAdapter(this, newsList);
        listView.setAdapter(newsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                New currentNews = newsAdapter.getItem(position);

                Uri newsUri = Uri.parse(currentNews.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                startActivity(websiteIntent);
            }
        });

        // Check the Connectivity
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            offlineTextView.setText(getResources().getString(R.string.offline));
            Toast.makeText(this, "Please check your connection", Toast.LENGTH_SHORT).show();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_filter:
                startActivity(new Intent(MainActivity.this, FiltersActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<ArrayList<New>> onCreateLoader(int id, Bundle args) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPrefs.getString(
                getString(R.string.filters_order_by_key),
                getString(R.string.filters_order_by_default));

        long fromDateLong = sharedPrefs.getLong(
                getString(R.string.filters_from_date_key), 0
        );

        Date dateObject = new Date(fromDateLong);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateObject);
        String fromDate = dateFormat.format(calendar.getTime());

        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("from-date", fromDate);
        Log.d(MG_TAG, uriBuilder.toString());

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<New>> loader, ArrayList<New> news) {
        progressBar.setVisibility(View.INVISIBLE);

        newsAdapter.clear();
        Log.d(MG_TAG, ""+ news);
        if (news != null && !news.isEmpty()) {
            if (cantFind.getText().length() > 0) {
                cantFind.setText("");
            }
            newsAdapter.addAll(news);
        } else {
            cantFind.setText(getResources().getString(R.string.no_results));
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}

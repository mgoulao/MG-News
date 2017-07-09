package com.mgoulao.mgnews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by msilv on 7/9/2017.
 */

public class NewsLoader extends AsyncTaskLoader<ArrayList<New>>{

    String mUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<New> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        return new QueryUtils().fetchNewsData(mUrl);
    }
}

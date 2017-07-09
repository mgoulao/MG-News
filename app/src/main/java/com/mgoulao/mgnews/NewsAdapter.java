package com.mgoulao.mgnews;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by msilv on 7/9/2017.
 */

public class NewsAdapter extends ArrayAdapter<New> {


    public NewsAdapter(Activity context, ArrayList<New> mNew) {
        super(context, 0, mNew);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list, parent, false);
        }

        New currentNew = getItem(position);

        TextView title = (TextView) listItemView.findViewById(R.id.title);
        title.setText(currentNew.getTitle());

        TextView section = (TextView) listItemView.findViewById(R.id.section);
        section.setText(currentNew.getSection());

        TextView date = (TextView) listItemView.findViewById(R.id.date);
        String formatedDate = formatDate(currentNew.getDate());
        date.setText(formatedDate);


        return listItemView;
    }

    private String formatDate(String date) {

        String dateFormatted = "";
        String dateNew = date.substring(0, 10);

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date dt = inputFormat.parse(dateNew);
            dateFormatted = newFormat.format(dt);
        } catch (ParseException e) {
            Log.e("MG", "Date Format", e);
        }
        return dateFormatted;
    }
}
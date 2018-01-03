/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.telematics.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.haukesomm.telematics.R;
import de.haukesomm.telematics.net.GeocodeApiClient;

/**
 * Created on 19.11.17
 * <p>
 * This class is a subclass of {@link BaseAdapter} and functions as a List- or Spinner-Adapter for
 * data of the Blackbox's cache-table in form of {@link JSONObject}s.<br>
 * For each cache entry a view representing the start- and destination-locations as well as the
 * average speed will be generated.
 *
 * @author Hauke Sommerfeld
 */
public class BlackboxAdapter extends BaseAdapter {

    /**
     * This constructor takes the app's context and a list of JSONObjects representing the cache-
     * entries. If created with this constructor the adapter can be used as is with no further
     * action needed.
     *
     * @param context   The app's context
     * @param tables    List of tables to display
     */
    public BlackboxAdapter(@NonNull Context context, @NonNull Blackbox blackbox,
                           @NonNull ArrayList<String> tables) {
        mContext = context;

        mBlackbox = blackbox;
        mBlackbox.open();

        mTables = tables;
    }


    @Override
    public void finalize() {
        mBlackbox.close();
    }



    private final Context mContext;



    private final Blackbox mBlackbox;


    private final ArrayList<String> mTables;


    /**
     * This method returns the number of objects the adapter is working with.
     *
     * @return  Number of objects
     */
    @Override
    public int getCount() {
        return mTables.size();
    }


    /**
     * This method returns the corresponding object to a position in the list.
     *
     * @param position  Position of the object which should be returned
     * @return          The corresponding object
     */
    @Override
    public Object getItem(int position) {
        return mBlackbox.getEntireTable(position);
    }


    /**
     * @deprecated
     * This method does nothing and should not be used.
     */
    @Deprecated
    @Override
    public long getItemId(int position) {
        return 0;
    }


    /**
     * This method generates the actual view for each object.
     *
     * @return The actual view
     */
    @SuppressLint({"InflateParams", "SetTextI18n", "SimpleDateFormat"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final String table = mTables.get(position);
        final ArrayList<JSONObject> data = mBlackbox.getEntireTable(table);


        View view = convertView;
        if (view == null)
        {
            view = LayoutInflater.from(mContext).inflate(R.layout.blackbox_data_preview, null, false);
            int padding = (int) mContext.getResources().getDimension(R.dimen.margin_default);
            view.setPadding(padding, padding / 2, padding, padding / 2);
        } else {
            return view;
        }


        String date = null;
        try {
            TextView dateText = view.findViewById(R.id.blackbox_data_preview_date);

            SimpleDateFormat formatIn = new SimpleDateFormat("yyyyMMdd");
            Date rawDate = formatIn.parse(table.replace(Blackbox.DATA_TABLE_PREFIX, ""));

            DateFormat formatOut = DateFormat.getDateInstance();
            date = formatOut.format(rawDate);

            dateText.setText(date);
        } catch (ParseException e) {
            Log.w("BlackboxAdapter", "Unable to set date: " + e.getMessage());
        }


        final GeocodeApiClient geocoder = new GeocodeApiClient();

        try {
            final TextView start = view.findViewById(R.id.blackbox_data_preview_start);

            double latitude = data.get(0).getDouble(Blackbox.DATA_LATITUDE);
            double longitude = data.get(0).getDouble(Blackbox.DATA_LONGITUDE);

            geocoder.requestAddress(latitude, longitude, new GeocodeApiClient.ResponseListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String city = geocoder.decodeCity(response);
                        if (city != null) {
                            start.setText(city);
                        }
                    } catch (JSONException e) {
                        //
                    }
                }
            });
        } catch (JSONException e) {
            Log.w("BlackboxAdapter", "Unable to set start: " + e.getMessage());
        }

        try {
            final TextView destination = view.findViewById(R.id.blackbox_data_preview_destination);

            double latitude = data.get(data.size() - 1).getDouble(Blackbox.DATA_LATITUDE);
            double longitude = data.get(data.size() - 1).getDouble(Blackbox.DATA_LONGITUDE);

            geocoder.requestAddress(latitude, longitude, new GeocodeApiClient.ResponseListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String city = geocoder.decodeCity(response);
                        if (city != null) {
                            destination.setText(city);
                        }
                    } catch (JSONException e) {
                        //
                    }
                }
            });
        } catch (JSONException e) {
            Log.w("BlackboxAdapter", "Unable to set destination: " + e.getMessage());
        }


        final String _date = date;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dataActivity = new Intent(mContext, DataActivity.class);
                dataActivity.putExtra(DataActivity.EXTRA_BLACKBOX_TABLE, table);
                dataActivity.putExtra(DataActivity.EXTRA_DATE, _date);
                mContext.startActivity(dataActivity);
            }
        });


        return view;
    }
}

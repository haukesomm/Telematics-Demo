/*
 * This file is part of the "Telematics App Mockup" project.
 * Copyright 2017-2018, Hauke Sommerfeld and Sarah Schulz-Mukisa
 *  
 * Licensed under the MIT license.
 * A copy can be obtained under the following link:
 * https://github.com/haukesomm/Telematics-App-Mockup/blob/master/LICENSE
 */

package de.haukesomm.telematics.privacy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import de.haukesomm.telematics.R;

/**
 * Created on 08.03.18
 * <p>
 * This adapter takes {@link PrivacyMode}s and displays them in a list.
 * </p>
 *
 * @author Hauke Sommerfeld
 */
public class PrivacyModeAdapter extends BaseAdapter {

    /**
     * This constructor takes the app's Context and an array of {@link PrivacyMode}s.
     *
     * @param context   The app's context
     * @param modes     Array of PrivacyModes to display
     */
    public PrivacyModeAdapter(@NonNull Context context, @NonNull PrivacyMode... modes) {
        mContext = context;
        mModeHolder = modes;
    }



    private final Context mContext;



    private final PrivacyMode[] mModeHolder;




    /**
     * This method returns the number of {@link PrivacyMode}s the adapter is working with.
     *
     * @return  Number of objects
     */
    @Override
    public int getCount() {
        return mModeHolder.length;
    }


    /**
     * This method returns the corresponding {@link PrivacyMode} to a position in the list.
     *
     * @param position  Position of the object which should be returned
     * @return          The corresponding object
     */
    @Override
    public Object getItem(int position) {
        return mModeHolder[position];
    }


    /**
     * This method returns the ID of a mode using {@link PrivacyMode#getID()}.
     */
    @Override
    public long getItemId(int position) {
        return mModeHolder[position].getID();
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
        {
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.privacy_mode_adapter, null, false);
        }

        TextView text = view.findViewById(R.id.privacy_mode_adapter_text);
        text.setText(mModeHolder[position].getNameRes());
        Drawable drawable = mContext.getDrawable(mModeHolder[position].getDrawableRes());
        DrawableCompat.setTint(drawable, mContext.getColor(R.color.colorPrimary));
        text.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

        return view;
    }

}

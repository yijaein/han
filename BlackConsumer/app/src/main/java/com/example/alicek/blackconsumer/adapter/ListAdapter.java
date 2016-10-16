package com.example.alicek.blackconsumer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alicek.blackconsumer.R;
import com.example.alicek.blackconsumer.model.BeaconDao;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;

/**
 * Created by Kwak on 2016-06-28.
 */
public class ListAdapter extends BaseAdapter {
    private ArrayList<BeaconDao> mlist;
    private Context mContext;

    public ListAdapter(ArrayList<BeaconDao> data, Context mcon) {
        this.mlist = data;
        this.mContext = mcon;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mlist.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.list_view_layout, parent, false);
        TextView textname = (TextView)convertView.findViewById(R.id.listname);
        TextView textcontent = (TextView)convertView.findViewById(R.id.listcontent);
        textname.setText(mlist.get(position).getName());
        textcontent.setText(mlist.get(position).getContent());
        return convertView;
    }
}

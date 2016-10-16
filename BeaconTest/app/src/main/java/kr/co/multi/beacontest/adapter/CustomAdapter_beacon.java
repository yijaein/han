package kr.co.multi.beacontest.adapter;

import android.content.Context;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import kr.co.multi.beacontest.R;
import kr.co.multi.beacontest.model.ContactDao_beacon;

public class CustomAdapter_beacon extends BaseAdapter {
    private Context mContext;
    public ArrayList<ContactDao_beacon> mList = new ArrayList<ContactDao_beacon>();

    public CustomAdapter_beacon(Context mContext){
        super();
        this.mContext=mContext;
    }

    private class ViewHolder {
        public ImageView mIcon;
        public TextView mText;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.beacon_custom, null);

            holder.mIcon = (ImageView) convertView.findViewById(R.id.mImage);
            holder.mText = (TextView) convertView.findViewById(R.id.mText);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        ContactDao_beacon mData = mList.get(position);

        if (mData.mIcon != null) {
            holder.mIcon.setVisibility(View.VISIBLE);
            holder.mIcon.setImageDrawable(mData.mIcon);
        }else{
            holder.mIcon.setVisibility(View.GONE);
        }
        holder.mText.setText(mData.mTitle);

        return convertView;
    }

    public void addItem(Drawable mIcon, String mTitle){
        ContactDao_beacon addInfo = new ContactDao_beacon();
        addInfo.mIcon = mIcon;
        addInfo.mTitle = mTitle;

        mList.add(addInfo);
    }
}

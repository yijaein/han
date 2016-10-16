package kr.co.multi.beacontest.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import kr.co.multi.beacontest.MountainInfoActivity;
import kr.co.multi.beacontest.model.AppConfig;

import kr.co.multi.beacontest.R;

public class MountainListAdapter extends ArrayAdapter<String> {
    private String[] url;
    private String[] name;
    private Activity context;
    private String name2;
    private int listCount;

    public MountainListAdapter(Activity context, String[] ids, String[] name) {
        super(context, R.layout.list_view_layout, ids);
        this.context = context;
        this.url = ids;
        this.name = name;
        this.listCount = name.length;
    }

    @Override
    public int getCount() {
        return listCount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_view_layout, null, true);
        ImageView imageView = (ImageView) listViewItem.findViewById(R.id.imgAvatar);
        Picasso.with(context)
                .load(AppConfig.URL_DOMAIN + url[position])
                .into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name2 = name[pos];
                Intent intent = new Intent(context,MountainInfoActivity.class);
                intent.putExtra("name", name2);

                context.startActivity(intent);
            }
        });
        return listViewItem;
    }


}
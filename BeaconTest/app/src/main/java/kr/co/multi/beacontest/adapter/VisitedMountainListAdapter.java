package kr.co.multi.beacontest.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import kr.co.multi.beacontest.R;
import com.squareup.picasso.Picasso;

import kr.co.multi.beacontest.model.AppConfig;

public class VisitedMountainListAdapter extends ArrayAdapter<String> {
    private String[] url;
    private String[] name;
    private String[] starttime;
    private String[] endtime;
    private String[] record;
    private Activity context;

    public VisitedMountainListAdapter(Activity context, String[] ids, String[] name, String[] starttime, String[] endtime, String[] record) {
        super(context, R.layout.list_view_layout, ids);
        this.context = context;
        this.url = ids;
        this.name = name;
        this.starttime = starttime;
        this.endtime = endtime;
        this.record = record;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.visited_list_view_layout, null, true);
        ImageView imageView = (ImageView) listViewItem.findViewById(R.id.visited_img);
        TextView textView = (TextView)listViewItem.findViewById(R.id.visited_name);
        TextView textView2 = (TextView)listViewItem.findViewById(R.id.visited_starttime);
        TextView textView3 = (TextView)listViewItem.findViewById(R.id.visited_endtime);
        TextView textView4 = (TextView)listViewItem.findViewById(R.id.visited_record);
        Picasso.with(context)
                .load(AppConfig.URL_DOMAIN + url[position])
                .into(imageView);
        textView.setText(name[position]);
        textView2.setText(starttime[position]);
        textView3.setText(endtime[position]);
        textView4.setText(record[position]);
        return listViewItem;
    }
}
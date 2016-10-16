package kr.co.multi.beacontest.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import kr.co.multi.beacontest.model.AppConfig;
import kr.co.multi.beacontest.R;

public class CustomAdapter extends ArrayAdapter<String> {
	private String[] url;
	private String[] text;
	private Activity context;

	public CustomAdapter(Activity context, String[] ids, String[] text) {
		super(context, R.layout.customlist, ids);
		this.context = context;
		this.url = ids;
		this.text = text;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View listViewItem = inflater.inflate(R.layout.customlist, null, true);
		ImageView imageView = (ImageView) listViewItem.findViewById(R.id.listimg);
		TextView textView = (TextView) listViewItem.findViewById(R.id.listtext);
		Picasso.with(context)
				.load(AppConfig.URL_DOMAIN + url[position])
				.into(imageView);
		textView.setText(text[position]);

		return listViewItem;
	}
}

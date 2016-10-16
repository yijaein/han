package kr.co.multi.beacontest;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.co.multi.beacontest.control.AppController;
import kr.co.multi.beacontest.model.AppConfig;
import kr.co.multi.beacontest.model.SQLiteHandler;

public class MountainInfoActivity extends Activity {
	String name,number;
	private TextView infoAlti, infoAdrr, infoCos, infoContent;
	private Button call;
	private ImageView infoimg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.mountain_info);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.maintitle);
		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		TextView tes = (TextView)findViewById(R.id.maintitletext);
		tes.setText(name);
		sendRequest(name);
		init();

	}

	private void sendRequest(final String _name){
		String tag_string_req = "req_listview";
		StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_MOUNTAININFO, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {

				try {

					JSONObject jObj = new JSONObject(response);
					boolean error = jObj.getBoolean("error");
					if(!error) {
						makelayout(response);
					}else{
						String errorMsg = jObj.getString("error_msg");
						Toast.makeText(getApplicationContext(),
								errorMsg, Toast.LENGTH_LONG).show();
					}
				}catch (JSONException e){
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(),
						error.getMessage(), Toast.LENGTH_LONG).show();
			}
		}){
			protected Map<String, String> getParams(){
				Map<String, String> params = new HashMap<String, String>();
				params.put("mName", _name);
				return params;
			}
		};
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}
	private void makelayout(String json){
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONObject mountainInfo = jsonObject.getJSONObject("mountain");
			infoAlti.setText("해발고도 : "+mountainInfo.getString("mAltitude")+"m");
			infoAdrr.setText(mountainInfo.getString("mAddress"));
			infoCos.setText(mountainInfo.getString("mCourse")+"개의 코스");
			infoContent.setText(mountainInfo.getString("mContent"));
			number = mountainInfo.getString("tel");
			Picasso.with(MountainInfoActivity.this)
					.load(AppConfig.URL_DOMAIN + mountainInfo.getString("infoImgPath"))
					.into(infoimg);
		}catch  (JSONException e) {
			e.printStackTrace();
		}
	}

	private void init(){
		infoAlti = (TextView)findViewById(R.id.infoAlti);
		infoAdrr = (TextView)findViewById(R.id.infoAdrr);
		infoCos = (TextView)findViewById(R.id.infoCos);
		infoContent = (TextView)findViewById(R.id.infoContent);
		call = (Button)findViewById(R.id.call);
		infoimg = (ImageView)findViewById(R.id.infoimg);
		call.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:" + number));
				startActivity(intent);
			}
		});
	}
}

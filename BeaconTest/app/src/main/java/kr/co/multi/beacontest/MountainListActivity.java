package kr.co.multi.beacontest;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kr.co.multi.beacontest.adapter.MountainListAdapter;
import kr.co.multi.beacontest.control.AppController;
import kr.co.multi.beacontest.model.AppConfig;
import kr.co.multi.beacontest.model.MountListParseDao;

public class MountainListActivity extends Activity {
	SearchView _searchView;
	String num;
	private ListView listView;
	int count;
	TextView tvCount;
	RadioButton option1;
	RadioButton option2;
	String mode = "num";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.mount_list);
//		getWindow().setFeatureInt(Window.FEATURE_ACTION_BAR, R.layout.mlist_bar);
		listView = (ListView) findViewById(R.id.listView);
		option1 = (RadioButton) findViewById(R.id.option1);
		option2 = (RadioButton) findViewById(R.id.option2);

		option1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mode = "num";
				sendRequest(num, mode,"");
				Intent intent = new Intent(MountainListActivity.this, MountainListActivity.class);
				startActivity(intent);
			}
		});

		option2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mode = "ganada";
				sendRequest(num, mode,"");
				Intent intent = new Intent(MountainListActivity.this, MountainListActivity.class);
				startActivity(intent);
			}
		});

		Intent intent = getIntent();
		num = intent.getStringExtra("number");
		sendRequest(num, mode,"");
		tvCount = (TextView) findViewById(R.id.count);
	}


	private void sendRequest(final String _num, final String _mode, final String _s){
		String tag_string_req = "req_listview";
		StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_VIEWLIST, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				try {
					JSONObject jObj = new JSONObject(response);
					boolean error = jObj.getBoolean("error");
					if(!error) {
						showJSON(response);
					}else{
						String errorMsg = jObj.getString("error_msg");
						Toast.makeText(getApplicationContext(),
								errorMsg, Toast.LENGTH_LONG).show();
					}
				}catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(),
						error.getMessage(), Toast.LENGTH_LONG).show();
			}
		}){
			protected Map<String, String> getParams(){
				Map<String, String> params = new HashMap<String, String>();
				params.put("mode", _mode);
				params.put("location", _num);
				params.put("search", _s);
				return params;
			}
		};
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}

	private void showJSON(String json) {
		MountListParseDao pj = new MountListParseDao(json);
		pj.parseJSON("mountain", "mPath", "mName");
		MountainListAdapter cl = new MountainListAdapter(this, pj.mPath, pj.mName);
		listView.setAdapter(cl);
		count = cl.getCount();
		tvCount.setText("총 " + count + "건");
	}

	@SuppressLint("NewApi")
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.action_bar, menu);
		
		_searchView = (SearchView) menu.findItem(R.id.action_serch).getActionView();
        _searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {
				mode = "search";
				sendRequest(num, mode, s);
				Intent intent = new Intent(MountainListActivity.this, MountainListActivity.class);
				startActivity(intent);
				return false;
			}
			@Override
			public boolean onQueryTextChange(String arg0) {
				return false;
			}
		});
         
        return true;
	}
}




package kr.co.multi.beacontest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import kr.co.multi.beacontest.adapter.VisitedMountainListAdapter;
import kr.co.multi.beacontest.control.AppController;
import kr.co.multi.beacontest.model.AppConfig;
import kr.co.multi.beacontest.model.SQLiteHandler;
import kr.co.multi.beacontest.model.VisitedMountListParseDao;

public class MyPageActivity extends Fragment {

	private ListView listView;
	private SQLiteHandler db;
	TextView textgender,textage,totalDistance,totalNumber;
	HashMap<String, String> user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.userinfo, container, false);

		db = new SQLiteHandler(getActivity());
		user = db.getUserDetails();
		listView = (ListView)layout.findViewById(R.id.visitedlist);
		View header = getLayoutInflater(savedInstanceState).inflate(R.layout.mypage, null, false);
		totalDistance = (TextView)header.findViewById(R.id.totalDistance);
		totalNumber = (TextView)header.findViewById(R.id.totalNumber);
		textgender = (TextView)header.findViewById(R.id.infogender);
		textage = (TextView)header.findViewById(R.id.infoage);
		textgender.setText(user.get("gender"));
		textage.setText(user.get("age"));
		listView.addHeaderView(header);
		return layout;
	}

	@Override
	public void onResume() {
		super.onResume();
		//totalNumber.setText(user.get("totalNumber"));
		sendRequest(user.get("uid"));
	}

	private void showJSON(String json){
		VisitedMountListParseDao pj = new VisitedMountListParseDao(json);
		pj.parseJSON();
		VisitedMountainListAdapter cl = new VisitedMountainListAdapter(getActivity(), pj.mPath, pj.mName,pj.startTime,pj.endTime,pj.record);
		listView.setAdapter(cl);
	}

	private void sendRequest(final String id){
		String tag_string_req = "req_listview";
		StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_VISITEDVIEWLIST, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				try {
					JSONObject jObj = new JSONObject(response);
					boolean error = jObj.getBoolean("error");
					if(!error) {
						totalDistance.setText(jObj.getString("totalDistance")+"km");
						totalNumber.setText(jObj.getString("totalNumber")+"번");
						showJSON(response);
					}else{
						String errorMsg = jObj.getString("error_msg");
						Toast.makeText(getActivity(),
								errorMsg, Toast.LENGTH_LONG).show();
					}
				}catch (JSONException e){
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getActivity(),
						error.getMessage(), Toast.LENGTH_LONG).show();
			}
		}){
			protected Map<String, String> getParams(){
				Map<String, String> params = new HashMap<String, String>();
				params.put("id", id);
				return params;
			}
		};
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}
}

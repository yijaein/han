package kr.co.multi.beacontest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.HashMap;
import java.util.Map;

import kr.co.multi.beacontest.control.AppController;
import kr.co.multi.beacontest.model.AppConfig;

public class InActivity extends AppCompatActivity {

    ImageView inoutimg;
    TextView inname, nowDate;
    String id;
    String setDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beacon_in);

        inoutimg = (ImageView)findViewById(R.id.beacon_enter_img);
        inname = (TextView)findViewById(R.id.inname);
        nowDate = (TextView)findViewById(R.id.time);
        Intent intent = getIntent();
        id = intent.getStringExtra("uid");
        setDate = intent.getStringExtra("nowDate");
        sendRequest(id);
    }

    @Override
    protected void onStart() {
        super.onStart();
        nowDate.setText(setDate);

    }

    private void sendRequest(final String _inoutId){
        String tag_string_req = "req_listview";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_INOUT, new Response.Listener<String>() {

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
                params.put("inoutId", _inoutId);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void makelayout(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject ininfo = jsonObject.getJSONObject("inout");
            Picasso.with(InActivity.this)
                    .load(AppConfig.URL_DOMAIN + ininfo.getString("inoutPath"))
                    .into(inoutimg);
            inname.setText(ininfo.getString("inoutName") + "의 비콘을 지나쳤습니다.\n등산을 시작합니다.");

        }catch  (JSONException e) {
            e.printStackTrace();
        }
    }

}
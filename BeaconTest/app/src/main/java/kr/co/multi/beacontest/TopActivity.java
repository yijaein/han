package kr.co.multi.beacontest;

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

public class TopActivity extends AppCompatActivity {
    String id;
    String time;
    ImageView topimg;
    TextView topname, toptime, godo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beacon_top);
        topimg = (ImageView)findViewById(R.id.topimg);
        topname = (TextView)findViewById(R.id.topname);
        toptime = (TextView)findViewById(R.id.toptime);
        godo = (TextView)findViewById(R.id.topgodo);
        id = getIntent().getStringExtra("uid");
        time = getIntent().getStringExtra("time");
        toptime.setText(time);
        sendRequest(id);
    }

    private void sendRequest(final String _topId){
        String tag_string_req = "req_listview";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_TOP, new Response.Listener<String>() {

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
                params.put("topId", _topId);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void makelayout(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject topinfo = jsonObject.getJSONObject("top");
            Picasso.with(TopActivity.this)
                    .load(AppConfig.URL_DOMAIN + topinfo.getString("topPath"))
                    .into(topimg);
            godo.setText(topinfo.getString("topAltitude"));
            topname.setText(topinfo.getString("topName") + " 정상을 지나쳤습니다.\n등산을 시작합니다.");

        }catch  (JSONException e) {
            e.printStackTrace();
        }
    }
}

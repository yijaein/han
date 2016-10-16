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

public class OutActivity extends AppCompatActivity {
    String id;
    ImageView outimg;
    TextView endtime, enddistance, outname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.beacon_out);
        outimg = (ImageView)findViewById(R.id.beacon_out_img) ;
        endtime = (TextView)findViewById(R.id.endtime);
        enddistance = (TextView)findViewById(R.id.enddistance);
        outname = (TextView)findViewById(R.id.outname);
        Intent intent = getIntent();
        endtime.setText(intent.getStringExtra("time"));
        id = intent.getStringExtra("uid");
        sendRequest(id);
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
            JSONObject outinf = jsonObject.getJSONObject("inout");
            Picasso.with(OutActivity.this)
                    .load(AppConfig.URL_DOMAIN + outinf.getString("inoutPath"))
                    .into(outimg);
            outname.setText(outinf.getString("inoutName") + "의 비콘을 지나쳤습니다.\n등산을 종료합니다.");

        }catch  (JSONException e) {
            e.printStackTrace();
        }
    }
}

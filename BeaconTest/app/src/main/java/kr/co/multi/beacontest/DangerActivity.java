package kr.co.multi.beacontest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

import java.util.HashMap;
import java.util.Map;

import kr.co.multi.beacontest.control.AppController;
import kr.co.multi.beacontest.model.AppConfig;

public class DangerActivity extends AppCompatActivity {
    String id;
    ImageView dangerimg;
    TextView content1, content2;
    Button callbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beacon_danger);
        dangerimg = (ImageView)findViewById(R.id.danger_img);
        content1 = (TextView)findViewById(R.id.danger_content1);
        content2 = (TextView)findViewById(R.id.danger_content2);
        id = getIntent().getStringExtra("uid");
        sendRequest(id);
        callbtn=(Button)findViewById(R.id.danger_call);
        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:010-0000-0000"));
                startActivity(intent);
            }
        });
    }

    private void sendRequest(final String _dangerId){
        String tag_string_req = "req_listview";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_DANGER, new Response.Listener<String>() {

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
                params.put("dangerId", _dangerId);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void makelayout(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject dangerinfo = jsonObject.getJSONObject("danger");
            Picasso.with(DangerActivity.this)
                    .load(AppConfig.URL_DOMAIN + dangerinfo.getString("dangerPath"))
                    .into(dangerimg);
            content1.setText(dangerinfo.getString("content1"));
            content2.setText(dangerinfo.getString("content2"));
        }catch  (JSONException e) {
            e.printStackTrace();
        }
    }
}

package kr.co.multi.beacontest;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.multi.beacontest.adapter.CustomAdapter;
import kr.co.multi.beacontest.control.AppController;
import kr.co.multi.beacontest.model.AppConfig;
import kr.co.multi.beacontest.model.ContactDao;

public class PhotoActivity extends AppCompatActivity {
    String id;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beacon_photo);
        listView = (ListView) findViewById(R.id.photo_listview);
        id = getIntent().getStringExtra("uid");
        sendRequest(id);
    }

    private void sendRequest(final String _photoId){
        String tag_string_req = "req_listview";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_PHOTO, new Response.Listener<String>() {

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
                params.put("photoId", _photoId);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showJSON(String json){
        ContactDao contactDao = new ContactDao(json);
        contactDao.parseJSON();
        CustomAdapter customAdapter = new CustomAdapter(this, contactDao.mPath, contactDao.text);
        listView.setAdapter(customAdapter);
    }
}

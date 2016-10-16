package kr.co.multi.beacontest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kr.co.multi.beacontest.control.AppController;
import kr.co.multi.beacontest.model.AppConfig;
import kr.co.multi.beacontest.model.SQLiteHandler;

public class SOSActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {
    String uid, gpsBeacon[], gpsGoal[];
    private GoogleMap mMap, mMap2;
    CompassView compassView;
    Button callbtn, hochool;

    AlertDialog dialog;

    private SQLiteHandler db;
    HashMap<String, String> user;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new SQLiteHandler(this);
        user = db.getUserDetails();
        id = user.get("uid");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("구조대를 호출하시겠습니까?")        // 제목 설정
                .setMessage("호출시 그 자리에 대기해 주세요.")        // 메세지 설정
                .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    // 확인 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton){
                        Toast.makeText(getApplicationContext(), "기다려주세요, 최대한 빨리 가겠습니다!", Toast.LENGTH_SHORT).show();

                        sendRequest(uid, id);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    // 취소 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

        dialog = builder.create();    // 알림창 객체 생성

        setContentView(R.layout.beacon_sos);

        uid = getIntent().getStringExtra("uid");
        sendRequest(uid, "id_null");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        compassView = new CompassView(this);

        addContentView(compassView, new LinearLayout.LayoutParams(210, 210));

        //setContentView(compassView);
        //시스템으로부터 센서 메니저 객체 얻어오기
        SensorManager sensorM = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //센서값이 바뀔때마다 수정되야 하므로 리스너를 등록한다.
        //센서 리스너 객체(센서이벤트리스너 임플리먼츠), 센서타입, 센서 민감도를 매니져에 등록한다.
        sensorM.registerListener(this, //Activity가 직접 리스너를 구현
                sensorM.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
        callbtn=(Button)findViewById(R.id.hochool_call);
        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:010-0000-0000"));
                startActivity(intent);
            }
        });
        hochool = (Button)findViewById(R.id.hochool);
        hochool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    private void sendRequest(final String _sosId, final String _id){
        String tag_string_req = "req_listview";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SOS, new Response.Listener<String>() {

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
                params.put("sosId", _sosId);
                params.put("id", _id);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void makelayout(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject sosinfo = jsonObject.getJSONObject("sos");

            gpsBeacon = sosinfo.getString("gpsBeacon").split(",");
            gpsGoal = sosinfo.getString("gpsGoal").split(",");

            LatLng sydney = new LatLng(Double.parseDouble(gpsBeacon[0]), Double.parseDouble(gpsBeacon[1]));
            mMap.addMarker(new MarkerOptions().position(sydney).title("내 위치"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            LatLng sydney2 = new LatLng(Double.parseDouble(gpsGoal[0]), Double.parseDouble(gpsGoal[1]));
            mMap2.addMarker(new MarkerOptions().position(sydney2).title("목표 지점"));
            mMap2.moveCamera(CameraUpdateFactory.newLatLng(sydney2));

        }catch  (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap2 = googleMap;
    }

    //센서의 정확도가 바뀌었을때(호출될일 없음, 향후 업데이트를 위해서)
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    //등록한 방향 센서 값이 바뀌었을 때 호출되는 콜백 메소드
    @Override
    public void onSensorChanged(SensorEvent event) {
        //event 객체에 센서값에 대한 정보가 들어있다.
        switch(event.sensor.getType()){
            case Sensor.TYPE_ORIENTATION : //방향센서 값이 바뀌었을때
                int heading = (int)event.values[0];
                //헤딩값을 View에 반영한다.
                compassView.updateUI(heading);
                break;
        }
    }
}

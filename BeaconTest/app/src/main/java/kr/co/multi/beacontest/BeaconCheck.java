package kr.co.multi.beacontest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.multi.beacontest.adapter.CustomAdapter_beacon;
import kr.co.multi.beacontest.control.AppController;
import kr.co.multi.beacontest.model.AppConfig;
import kr.co.multi.beacontest.model.SQLiteHandler;

public class BeaconCheck extends Service implements BeaconConsumer {
    String m_display_string;
    long m_current_time;
    long m_start_time;
    Timer m_timer;
    TimerTask m_timerTask;

    String nowDate;
    String topTime[];
    String outTime;
    int topNum;
    int photoNum;
    int dangerNum;

    // 푸쉬알림 ID
    private static final int OUT_ID = 1;
    private static final int TOP_ID = 2;
    private static final int PHOTO_ID = 3;
    private static final int DANGER_ID = 4;
    private static final int SOS_ID = 5;

    private BeaconManager beaconManager;
    //  감지된 비콘들을 임시로 담을 리스트
    private List<Beacon> beaconList = new ArrayList<>();

    private ArrayList<String> arrayList;
    private ArrayList<String> arrayBeacon;

    // 내가 다녀온 산 기록
    private String record;
    private boolean w;
    // 정상에 갔는지 체크
    private boolean topCheck = false;

    Context context;

    private SQLiteHandler db;

    private CustomAdapter_beacon adapter;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        context = getApplicationContext();

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215, i:4-19, i:20-21, i:22-23, p:24-24, d:25-25"));
/*
        beaconManager.setBackgroundScanPeriod(1100l);
        beaconManager.setBackgroundBetweenScanPeriod(30000l);
        try {
            beaconManager.updateScanPeriods();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
*/
        arrayList = new ArrayList<String>();
        // 리스트에 비콘들이 중복되지 않도록 검사하기 위한 ArrayList
        arrayBeacon = new ArrayList<String>();

        adapter = new CustomAdapter_beacon(this);
        if (mCallbackList != null) mCallbackList.sendData(adapter);

        record = "";
        w = false;

        // 화면 깨우기 설정
        BeaconListActivity.mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        m_timerTask = new TimerTask() {
            public void run() {
                // 현재 시간을 밀리초 단위로 얻어온다.
                m_current_time = System.currentTimeMillis();

                // 현재 시간에서 시작 시간을 빼서 경과시간을 얻는다.
                long millis = m_current_time - m_start_time;
                // 밀리초에 1000을 나누어 초단위로 변경한다.
                int seconds = (int) (millis / 1000);
                // 초단위의 시간정보를 60으로 나누어 분단위로 변경한다.
                int minutes = seconds / 60;
                // 분단위의 시간정보를 60으로 나누어 시단위로 변경한다.
                int hour = seconds / 60 / 60;
                // 초단위의 시간정보를 60으로 나머지 연산하여 초 값을 얻는다.
                seconds = seconds % 60;

                // 출력할 문자열을 구성한다.
                m_display_string = String.format("%02d : %02d : %02d", hour, minutes, seconds);

                if(mCallbackTime != null) mCallbackTime.sendData(m_display_string);
            }
        };

        topTime = new String[10];
        topNum = 1;
        photoNum = 1;
        dangerNum = 1;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        clearList();
        arrayList.add("입구");
        adapter.addItem(getResources().getDrawable(R.drawable.in), "입구");
        beaconManager.bind(this);
        handler.sendEmptyMessage(0);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    for (Beacon beacon : beacons) {
                        beaconList.add(beacon);
                    }
                }
            }
        });
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myBeacon", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    for (Beacon beacon : beaconList) {
                        String minor = beacon.getId3().toString();
                        // 지나간 비콘인지 검색
                        if(!arrayBeacon.contains(minor) && beacon.getDistance() < 2) {
                            String subBeacon = minor.substring(0, 1);
                            switch (subBeacon) {
                                case "2":
                                    if (!minor.equals("21")) break; // DB 검색
                                    arrayBeacon.add(minor);
                                    arrayList.add("정상");
                                    adapter.addItem(getResources().getDrawable(R.drawable.top), "정상" + topNum);
                                    topCheck = true;
                                    topTime[topNum] = m_display_string;
                                    AlarmWakeLock.wakeLock(BeaconListActivity.mActivity);
                                    AlarmWakeLock.releaseWakeLock();
                                    sendNotification(TOP_ID, minor, topNum);
                                    topNum++;

                                    sendRequestRecord(minor);
                                    w = true;

                                    break;
                                case "3":
                                    if (!(minor.equals("31") || minor.equals("32"))) break;
                                    arrayBeacon.add(minor);
                                    arrayList.add("포토");
                                    adapter.addItem(getResources().getDrawable(R.drawable.photo), "포토" + photoNum);
                                    AlarmWakeLock.wakeLock(BeaconListActivity.mActivity);
                                    AlarmWakeLock.releaseWakeLock();
                                    sendNotification(PHOTO_ID, minor, photoNum);
                                    photoNum++;
                                    break;
                                case "4":
                                    if (!minor.equals("41")) break;
                                    arrayBeacon.add(minor);
                                    arrayList.add("위험");
                                    adapter.addItem(getResources().getDrawable(R.drawable.danger), "위험" + dangerNum);
                                    AlarmWakeLock.wakeLock(BeaconListActivity.mActivity);
                                    AlarmWakeLock.releaseWakeLock();
                                    sendNotification(DANGER_ID, minor, dangerNum);
                                    dangerNum++;
                                    break;
                                case "5":
                                    if (!minor.equals("51")) break;
                                    arrayBeacon.add(minor);
                                    arrayList.add("조난");
                                    adapter.addItem(getResources().getDrawable(R.drawable.jonan), "조난");
                                    AlarmWakeLock.wakeLock(BeaconListActivity.mActivity);
                                    AlarmWakeLock.releaseWakeLock();
                                    sendNotification(SOS_ID, minor, 0);
                                    break;
                                case "1":
                                    if (topCheck) {
                                        if (!minor.equals("12")) break;
                                        arrayBeacon.add(minor);
                                        arrayList.add("출구");
                                        adapter.addItem(getResources().getDrawable(R.drawable.out), "출구");
                                        outTime = m_display_string;
                                        AlarmWakeLock.wakeLock(BeaconListActivity.mActivity);
                                        AlarmWakeLock.releaseWakeLock();
                                        sendNotification(OUT_ID, minor, 0);
                                        m_timer.cancel();
                                        m_timerTask.cancel();

                                        sendRequestRecord(minor);
                                        w = false;

                                        Handler h = new Handler();
                                        h.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                db = new SQLiteHandler(context);
                                                HashMap<String, String> user = db.getUserDetails();
                                                sendRequest(arrayBeacon.get(0), user.get("uid"), nowDate, outTime, record);

                                                handler.removeMessages(0);
                                                if (mCallback != null) mCallback.sendData(1);
                                            }
                                        }, 1000);
                                    }
                                    break;
                            }
                        }
                        if (mCallbackList != null) mCallbackList.sendData(adapter);
                    }
                    handler.sendEmptyMessageDelayed(0, 1100);
                    beaconList.clear();
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        handler.removeMessages(0);
        beaconManager.unbind(this);
        if (mCallback != null) mCallback.sendData(1);
    }

    public class serviceBinder extends Binder {
        BeaconCheck getService() {
            return BeaconCheck.this;
        }
    }

    private final IBinder mBinder = new serviceBinder();

    public interface ICallback {
        void sendData(int i);
    }
    public interface ICallbackList {
        void sendData(CustomAdapter_beacon adapter);
    }
    public interface ICallbackTime {
        void sendData(String m_display_string);
    }

    private ICallback mCallback;
    private ICallbackList mCallbackList;
    private ICallbackTime mCallbackTime;

    public void registerCallback(ICallback cb) {
        mCallback = cb;
    }
    public void registerCallback(ICallbackList cb) {
        mCallbackList = cb;
    }
    public void registerCallback(ICallbackTime cb) {
        mCallbackTime = cb;
    }

    public void clearList() {
        arrayBeacon.clear();
        arrayList.clear();
        record = "";
    }

    private void sendNotification(int id, String minor, int n) {
        // 노티 빌드?
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent pendingIntent;
        Intent intent;
        switch (id) {
            case 2:
                intent = new Intent(this, TopActivity.class);
                intent.putExtra("uid", minor);
                intent.putExtra("time", topTime[n]);
                pendingIntent = PendingIntent.getActivity(this, id, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                // 최상단에 아이콘
                builder.setSmallIcon(R.drawable.ic_stat_notification);
                // 리스트에 왼쪽 메인 아이콘
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.top));
                // 리스트 타이틀과 내용
                builder.setContentTitle("정상");
                builder.setContentText("충분한 휴식을 취하시고 내려오세요.");
                break;
            case 3:
                intent = new Intent(this, PhotoActivity.class);
                intent.putExtra("uid", minor);
                pendingIntent = PendingIntent.getActivity(this, id, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                // 최상단에 아이콘
                builder.setSmallIcon(R.drawable.ic_stat_notification);
                // 리스트에 왼쪽 메인 아이콘
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.photo));
                // 리스트 타이틀과 내용
                builder.setContentTitle("풍경이 아름다운 구간");
                builder.setContentText("근처 포토존을 보고 싶으면 누르세요.");
                break;
            case 4:
                intent = new Intent(this, DangerActivity.class);
                intent.putExtra("uid", minor);
                pendingIntent = PendingIntent.getActivity(this, id, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                // 최상단에 아이콘
                builder.setSmallIcon(R.drawable.ic_stat_notification);
                // 리스트에 왼쪽 메인 아이콘
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.danger));
                // 리스트 타이틀과 내용
                builder.setContentTitle("위험 구간");
                builder.setContentText("어떤 위험인지 알고 싶으면 누르세요");
                break;
            case 5:
                intent = new Intent(this, SOSActivity.class);
                intent.putExtra("uid", minor);
                pendingIntent = PendingIntent.getActivity(this, id, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                // 최상단에 아이콘
                builder.setSmallIcon(R.drawable.ic_stat_notification);
                // 리스트에 왼쪽 메인 아이콘
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jonan));
                // 리스트 타이틀과 내용
                builder.setContentTitle("조난 위험 구간");
                builder.setContentText("등산 코스가 아닙니다. 정보를 확인하세요.");
                break;
            case 1:
                intent = new Intent(this, OutActivity.class);
                intent.putExtra("uid", minor);
                intent.putExtra("time", outTime);
                pendingIntent = PendingIntent.getActivity(this, id, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                // 최상단에 아이콘
                builder.setSmallIcon(R.drawable.ic_stat_notification);
                // 리스트에 왼쪽 메인 아이콘
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.out));
                // 리스트 타이틀과 내용
                builder.setContentTitle("출구");
                builder.setContentText("수고하셨습니다. 자신의 등산 기록을 보세요.");
                break;
        }
        builder.setAutoCancel(true);
        notificationManager.notify(id, builder.build());
    }

    public void enterInActivity(String str) {
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        nowDate = sdfNow.format(new Date(System.currentTimeMillis()));

        arrayBeacon.add(str);
        //Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, InActivity.class);
        intent.putExtra("uid", str);
        intent.putExtra("nowDate", nowDate);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        sendRequestRecord(str);
        w = true;

        if(m_timer == null) {
            // 현재 시간에서 이전에 중지했던 시간(경과시간 - 시작시간)을 뺀다.
            // 5초에서 중지를 누른 후 다시 시작을 누르면 1초후에 6초가 될 수 있도록 연산한다.
            m_start_time = System.currentTimeMillis() - (m_current_time - m_start_time);
            // 타이머 객체를 생성한다.
            m_timer = new Timer();
            // 사용자정의 TimerTask 객체를 넘겨주고, 100 밀리초 후에 수행되며,
            // 200 밀리마다 반복 수행되도록 설정한다.
            m_timer.scheduleAtFixedRate(m_timerTask, 100, 200);
        }
    }

    public void callActivity(String str) {
        Intent intent;
        for(int i = 0; i < arrayList.size(); i++) {
            String subBeacon = str.substring(0, 2);
            if(arrayList.get(i).equals(subBeacon)) {

                if(subBeacon.equals("입구")) {
                    intent = new Intent(context, InActivity.class);
                    intent.putExtra("uid", arrayBeacon.get(i));
                    intent.putExtra("nowDate", nowDate);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else if(subBeacon.equals("정상")) {
                    intent = new Intent(context, TopActivity.class);
                    intent.putExtra("uid", arrayBeacon.get(i));
                    int j = Integer.parseInt(str.substring(2));
                    intent.putExtra("time", topTime[j]);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else if(subBeacon.equals("포토")) {
                    intent = new Intent(context, PhotoActivity.class);
                    intent.putExtra("uid", arrayBeacon.get(i));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else if(subBeacon.equals("위험")) {
                    intent = new Intent(context, DangerActivity.class);
                    intent.putExtra("uid", arrayBeacon.get(i));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else if(subBeacon.equals("출구")) {
                    intent = new Intent(context, OutActivity.class);
                    intent.putExtra("uid", arrayBeacon.get(i));
                    intent.putExtra("time", outTime);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else if(subBeacon.equals("조난")) {
                    intent = new Intent(this, SOSActivity.class);
                    intent.putExtra("uid", arrayBeacon.get(i));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                break;
            }
        };
    }

    private void sendRequestRecord(final String _minor) {
        String tag_string_req = "req_listview";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_RECORD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if(!error) {
                        setRecord(response);
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
                params.put("minor", _minor);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void setRecord(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject info = jsonObject.getJSONObject("record");
            record += info.getString("rName");
            if(w) {
                record += ">>";
            }
        }catch  (JSONException e) {
            e.printStackTrace();

        }
    }

    private void sendRequest(final String _inoutId, final String _userId, final String _startDate, final String _outTime, final String _record){
        String tag_string_req = "req_listview";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_HIKING, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if(!error) {
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
                params.put("userId", _userId);
                params.put("startDate", _startDate);
                params.put("outTime", _outTime);
                params.put("record", _record);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}

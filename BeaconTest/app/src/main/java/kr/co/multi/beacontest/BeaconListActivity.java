package kr.co.multi.beacontest;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kr.co.multi.beacontest.adapter.CustomAdapter_beacon;
import kr.co.multi.beacontest.model.ContactDao_beacon;

public class BeaconListActivity extends AppCompatActivity implements BeaconConsumer {

    public static final String TAG = "TestLog";
    // 입구 비콘 스캔
    private BeaconConsumer consumer;

    private BeaconManager beaconManager;
    //  감지된 비콘들을 임시로 담을 리스트
    private List<Beacon> beaconList = new ArrayList<>();
    // 블루투스 제어
    private BluetoothAdapter mBluetoothAdapter;

    // 입구 스캔 핸들러
    private Handler mHandler;
    // 입구 스캔 시간
    private static final int SCAN_PERIOD = 10000;
    // 스캔 버튼 토글 제어
    private int mScanning = 1;

    public BeaconCheck mService;

    boolean mBound = false;

    private Intent intent;

    public static AppCompatActivity mActivity;
    LinearLayout time_bg;
    TextView m_time_view;
    String m_display_string;

    AlertDialog dialog;

    private ListView listView;
    private CustomAdapter_beacon adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beacon_list);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215, i:4-19, i:20-21, i:22-23, p:24-24, d:25-25"));

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = new Handler();
        // 비콘매니저의 bind를 쓰기 위해
        consumer = this;

        intent = new Intent(this, BeaconCheck.class);

        listView = (ListView)findViewById(R.id.beacon_listview);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mActivity = this;

        m_time_view = (TextView)findViewById(R.id.time);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("등산을 종료하시겠습니까?")        // 제목 설정
                .setMessage("리스트의 기록이 지워지고 저장되지 않습니다.")        // 메세지 설정
                .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    // 확인 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton){
                        mScanning = 1;
                        invalidateOptionsMenu();
                        if(mBound) {
                            mService.handler.removeMessages(0);
                            mService.clearList();
                            mService.m_timer.cancel();
                            mService.m_timerTask.cancel();
                            unbindService(mConnection);
                            mBound = false;
                        }
                        stopService(intent);
                        m_time_view.setText(null);
                        listView.setAdapter(null);
                        time_bg.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    // 취소 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

        dialog = builder.create();    // 알림창 객체 생성
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactDao_beacon mData = adapter.mList.get(position);
                //Toast.makeText(getApplicationContext(), mData.mTitle, Toast.LENGTH_SHORT).show();
                mService.callActivity(mData.mTitle);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        if(serviceRunningCheck()) {
            Log.i(TAG, "onResume1");
            if(!mBound) {
                Log.i(TAG, "onResume2");
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            }
            mScanning = 3;
            invalidateOptionsMenu();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        if(mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(consumer);
    }

    public boolean serviceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(AppCompatActivity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("kr.co.multi.beacontest.BeaconCheck".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.beacon_menu, menu);
        if (mScanning == 1) {
            menu.findItem(R.id.menu_refresh).setActionView(null);
            menu.findItem(R.id.menu_end).setVisible(false);
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
        } else if (mScanning == 2) {
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress);
            menu.findItem(R.id.menu_end).setVisible(false);
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
        } else if (mScanning == 3) {
            menu.findItem(R.id.menu_refresh).setActionView(null);
            menu.findItem(R.id.menu_end).setVisible(true);
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                m_time_view.setText(null);
                listView.setAdapter(null);
                if(!mBluetoothAdapter.isEnabled()) {
                    Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnOn, 0);
                } else {
                    scanDevice(true);
                }
                break;
            case R.id.menu_stop:
                scanDevice(false);
                break;
            case R.id.menu_end:
                dialog.show();    // 알림창 띄우기
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            // 사용자가 블루투스 활성화 승인했을때
            if (resultCode == RESULT_OK) {
                scanDevice(true);
            }
        }
    }

    Handler handler = new Handler() {
        @Override
         public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    handler.sendEmptyMessageDelayed(0, 1100);
                    for (Beacon beacon : beaconList) {
                        String minor = beacon.getId3().toString();
                        if (minor != "11") continue;
                        final String in = minor;
                        //textView.append("ID : " + beacon.getId3() + " / Distance : " + Double.parseDouble(String.format("%.3f", beacon.getDistance())) + "m\n");
                        mHandler.removeCallbacksAndMessages(null);
                        handler.removeMessages(0);
                        // 서비스 시작
                        startService(intent);
                        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                        beaconManager.unbind(consumer);

                        mScanning = 3;
                        invalidateOptionsMenu();

                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(mBound) mService.enterInActivity(in);
                            }
                        }, 1000);
                    }
                    beaconList.clear();
                    break;
            }
        }
    };

    private void scanDevice(final boolean enable) {
        if (enable) {
            mScanning = 2;
            beaconManager.bind(consumer);
            handler.sendEmptyMessage(0);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = 1;
                    invalidateOptionsMenu();
                    handler.removeMessages(0);
                    beaconManager.unbind(consumer);
                }
            }, SCAN_PERIOD);
        } else {
            mScanning = 1;
            // postDelayed 취소
            mHandler.removeCallbacksAndMessages(null);
            handler.removeMessages(0);
            beaconManager.unbind(consumer);
        }
        invalidateOptionsMenu();
    }

    public ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((BeaconCheck.serviceBinder)service).getService();
            mService.registerCallback(mCallback);
            mService.registerCallback(mCallbackList);
            mService.registerCallback(mCallbackTime);
            mBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }

        private BeaconCheck.ICallback mCallback = new BeaconCheck.ICallback() {
            public void sendData(int n) {
                switch (n) {
                    case 1:
                        mScanning = 1;
                        invalidateOptionsMenu();
                        if(mBound) {
                            unbindService(mConnection);
                            mBound = false;
                        }
                        stopService(intent);
                        break;
                }
            }
        };
        private BeaconCheck.ICallbackList mCallbackList = new BeaconCheck.ICallbackList() {
            public void sendData(CustomAdapter_beacon ca) {
                adapter = ca;
                listView.setAdapter(ca);
            }
        };

        public BeaconCheck.ICallbackTime mCallbackTime = new BeaconCheck.ICallbackTime() {
            public void sendData(String time) {

                m_display_string = time;

                Runnable m_display_run = new Runnable() {
                    public void run() {
                        // 텍스트뷰는 시간을 출력한다.
                        time_bg=(LinearLayout)findViewById(R.id.time_bg);
                        time_bg.setVisibility(View.VISIBLE);
                        m_time_view.setText(m_display_string);
                    }
                };
                // 해당 문자열을 출력하도록 메인 쓰레드에 전달한다.
                m_time_view.post(m_display_run);
            }
        };
    };
}

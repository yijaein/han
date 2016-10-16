package kr.co.multi.beacontest;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import kr.co.multi.beacontest.control.SessionManager;
import kr.co.multi.beacontest.model.SQLiteHandler;


public class HomeActivity extends Fragment {
	private SQLiteHandler db;
	private SessionManager session;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.home, container, false);
		Button kkd_btn = (Button)layout.findViewById(R.id.kkd_btn);
		Button ksbd = (Button)layout.findViewById(R.id.ksbd);
		kkd_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),MountainListActivity.class);
				intent.putExtra("number","1");
				getActivity().startActivity(intent);
			}
		});
		ksbd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),MountainListActivity.class);
				intent.putExtra("number","3");
				getActivity().startActivity(intent);
			}
		});
		return layout;
	}


}

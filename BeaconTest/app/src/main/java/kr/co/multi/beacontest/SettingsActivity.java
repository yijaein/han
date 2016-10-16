package kr.co.multi.beacontest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import kr.co.multi.beacontest.control.SessionManager;
import kr.co.multi.beacontest.model.SQLiteHandler;


public class SettingsActivity extends Fragment {
	SessionManager session;
	SQLiteHandler db;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		session = new SessionManager(getActivity());
		db = new SQLiteHandler(getActivity());
		LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.settings, container, false);
		LinearLayout settings_logoutbtn = (LinearLayout)layout.findViewById(R.id.settings_logoutbtn);
		settings_logoutbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				logoutUser();
				Toast.makeText(getContext(), "로그아웃 되었습니다.", Toast.LENGTH_LONG).show();
			}
		});
		return layout;
	}

	private void logoutUser() {
		session.setLogin(false);
		db.deleteUsers();
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		startActivity(intent);
		getActivity().finish();
	}
	
}

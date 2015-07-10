package com.bignerdranch.android.photoballery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

public abstract class VisibleFragment extends Fragment {
	public static final String TAG = "VisibleFragment";
	
	private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
		public void onReceive(android.content.Context arg0, android.content.Intent intent) {
//			Toast.makeText(getActivity(), "Got a broadcast:" + intent.getAction(), Toast.LENGTH_LONG).show();
			// If we receive this, we're visible, so cancel the notification
			Log.i(TAG, "canceling notification");
			setResultCode(Activity.RESULT_CANCELED);
		};
	};
	
	public void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
		getActivity().registerReceiver(mOnShowNotification, filter, PollService.PERM_PRIVATE, null);
	};
	
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(mOnShowNotification);
	};
}

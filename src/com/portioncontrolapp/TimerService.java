package com.portioncontrolapp;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

public class TimerService extends IntentService {

	public TimerService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO do something useful
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO for communication return IBinder implementation
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// 20 minutes
		new CountDownTimer(1000, 1000) {

			public void onTick(long millisUntilFinished) {

			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub

			}

		}.start();

	}
}
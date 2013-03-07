/*
 * File:        MainActivity.java
 * Created:     3/6/2013
 * Author:      Jason House
 * Description: Have a motivational count down timer for people to wait 20 minutes after eating
 * 
 * This code is copyright (c) 2013 Jason House
 *  
 * History:
 * Revision v 0.1
 * 
 * Basic app showing count down clock, some motivations and a button to start
 * Used threading to wait and incremented seconds from that
 * Have notifications to let users do other things
 * 
 * Revision v 0.2
 * 
 * Changed the code to use Android's CountDownTimer for more accurate clock
 * 
 */

package com.portioncontrolapp;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private static final int SECONDS = 59;

	private int notifyID = 1;
	private int intCurrentMinutes = 20;
	private int intCurrentSeconds = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent intent = getIntent();
		if (intent.getExtras() != null) {
			intCurrentMinutes = intent.getExtras().getInt("intCurrentMinutes");
			intCurrentSeconds = intent.getExtras().getInt("intCurrentSeconds");
			Log.i(TAG, "intCurrentMinutes: " + intCurrentMinutes);
			Log.i(TAG, "intCurrentSeconds: " + intCurrentSeconds);

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onClick_finishedEating(View view) {
		Log.d(TAG, "onClick_finishedEating()");
		startCountdown();
	}

	private void startCountdown() {
		Log.d(TAG, "startCountdown()");
		intCurrentMinutes = 20;
		intCurrentSeconds = 0;
		((Button) findViewById(R.id.butFinishedEating)).setEnabled(false);

		// 20 minutes
		new CountDownTimer(1200000, 1000) {

			public void onTick(long millisUntilFinished) {
				runOnUiThread(new Runnable() {
					public void run() {
						// Update the clock
						TextView countdown = (TextView) findViewById(R.id.txtCountdown);
						countdown.setText(createOutput(intCurrentMinutes,
								intCurrentSeconds));

						// Update the reinforcement text
						TextView reinforcements = (TextView) findViewById(R.id.txtReinforcements);
						switch (intCurrentMinutes) {
						case 19:
						case 18:
							reinforcements.setText(R.string.minute_19);
							break;
						case 17:
						case 16:
							reinforcements.setText(R.string.minute_17);
							break;
						case 15:
						case 14:
							reinforcements.setText(R.string.minute_15);
							break;
						case 13:
						case 12:
							reinforcements.setText(R.string.minute_13);
							break;
						case 11:
						case 10:
							reinforcements.setText(R.string.minute_11);
							break;
						case 9:
						case 8:
							reinforcements.setText(R.string.minute_09);
							break;
						case 7:
						case 6:
							reinforcements.setText(R.string.minute_07);
							break;
						case 5:
						case 4:
							reinforcements.setText(R.string.minute_05);
							break;
						case 3:
						case 2:
							reinforcements.setText(R.string.minute_03);
							break;
						case 1:
						case 0:
							reinforcements.setText(R.string.minute_01);
							break;
						default:
							countdown.setText(R.string.timeZero);
							reinforcements.setText(R.string._blank);
						}

						NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
								MainActivity.this)
								.setSmallIcon(R.drawable.ic_launcher)
								.setContentTitle("Time left")
								.setContentText(
										createOutput(intCurrentMinutes,
												intCurrentSeconds));

						Intent notificationIntent = new Intent(
								MainActivity.this, MainActivity.class);
						notificationIntent.putExtra("intCurrentMinutes",
								intCurrentMinutes);
						notificationIntent.putExtra("intCurrentSeconds",
								intCurrentSeconds);
						notificationIntent
								.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
										| Intent.FLAG_ACTIVITY_SINGLE_TOP);

						TaskStackBuilder stackBuilder = TaskStackBuilder
								.create(MainActivity.this);
						stackBuilder.addParentStack(MainActivity.class);
						stackBuilder.addNextIntent(notificationIntent);

						PendingIntent resultPendingIntent = PendingIntent
								.getActivity(MainActivity.this, 0,
										notificationIntent, 0);

						mBuilder.setContentIntent(resultPendingIntent);
						NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

						mNotificationManager.notify(notifyID, mBuilder.build());

					}
				});

				intCurrentSeconds--;
				if (intCurrentSeconds < 0) {
					intCurrentSeconds = SECONDS;
					intCurrentMinutes--;
				}
			}

			public void onFinish() {
				((Button) findViewById(R.id.butFinishedEating))
						.setEnabled(true);
			}
		}.start();

	}

	private String createOutput(int minutes, int seconds) {
		DecimalFormat nft = new DecimalFormat("#00.###");
		return nft.format(minutes) + ":" + nft.format(seconds);
	}

}

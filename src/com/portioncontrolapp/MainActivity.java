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
 * Fixed the back button then load from the notification error
 * 
 * Revision v 0.3
 * 
 * Fixed the rotation reset issue
 * Change color of fonts
 * Rotate text
 * Allow for reruns of app
 * 
 * Revision v 0.4
 * 
 * Removed button and added touch interface
 */

package com.portioncontrolapp;

import java.text.DecimalFormat;
import java.util.Random;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private static final int SECONDS = 59;
	private static final int MAX_ITERATIONS = 510; 
	// Red, orange, yellow, to green

	private int notifyID = 1;
	private int intCurrentMinutes;
	private int intCurrentSeconds;
	private CountDownTimer cdt;
	private int red = 255;
	private int green = 0;
	private int blue = 0;
	private int deltaValue;
	private Random rand;
	private int rotation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		intCurrentMinutes = 1;
		intCurrentSeconds = 0;
		rand = new Random();
		rotation = rand.nextInt(20) - 10;
		deltaValue = MAX_ITERATIONS
				/ (intCurrentMinutes * 60 + intCurrentSeconds);
		Log.i(TAG, "deltaValue: " + deltaValue);

		red = 255;
		green = 0;
		blue = 0;

		Intent intent = getIntent();
		if (intent.getExtras() != null) {
			intCurrentMinutes = intent.getExtras().getInt("intCurrentMinutes");
			intCurrentSeconds = intent.getExtras().getInt("intCurrentSeconds");
			Log.i(TAG, "intCurrentMinutes: " + intCurrentMinutes);
			Log.i(TAG, "intCurrentSeconds: " + intCurrentSeconds);
			startCountdown();
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
		((RelativeLayout) findViewById(R.id.rlMainActivity)).setEnabled(false);
		startCountdown();
	}

	private void startCountdown() {
		Log.d(TAG, "startCountdown()");

		if (cdt != null) {
			cdt.cancel();
			cdt = null;
		}

		// 20 minutes
		cdt = new CountDownTimer(
				(intCurrentMinutes * 60 + intCurrentSeconds) * 1000, 1000) {

			public void onTick(long millisUntilFinished) {

				if (intCurrentSeconds == 0)
					rotation = rand.nextInt(20) - 10;

				if (green >= 255) {
					green = 255;
					red -= deltaValue;
				} else {
					green += deltaValue;
				}

				runOnUiThread(new Runnable() {
					public void run() {
						// Update the clock
						TextView countdown = (TextView) findViewById(R.id.txtCountdown);
						countdown.setText(createOutput(intCurrentMinutes,
								intCurrentSeconds));
						countdown.setTextColor(Color.rgb(red, green, blue));

						// Update the reinforcement text
						TextView reinforcements = (TextView) findViewById(R.id.txtReinforcements);
						switch (intCurrentMinutes) {
						case 19:
						case 18:
							reinforcements.setText(R.string.minute_19);
							reinforcements.setRotation(rotation);
							break;
						case 17:
						case 16:
							reinforcements.setText(R.string.minute_17);
							reinforcements.setRotation(rotation);
							break;
						case 15:
						case 14:
							reinforcements.setText(R.string.minute_15);
							reinforcements.setRotation(rotation);
							break;
						case 13:
						case 12:
							reinforcements.setText(R.string.minute_13);
							reinforcements.setRotation(rotation);
							break;
						case 11:
						case 10:
							reinforcements.setText(R.string.minute_11);
							reinforcements.setRotation(rotation);
							break;
						case 9:
						case 8:
							reinforcements.setText(R.string.minute_09);
							reinforcements.setRotation(rotation);
							break;
						case 7:
						case 6:
							reinforcements.setText(R.string.minute_07);
							reinforcements.setRotation(rotation);
							break;
						case 5:
						case 4:
							reinforcements.setText(R.string.minute_05);
							reinforcements.setRotation(rotation);
							break;
						case 3:
						case 2:
							reinforcements.setText(R.string.minute_03);
							reinforcements.setRotation(rotation);
							break;
						case 1:
						case 0:
							reinforcements.setText(R.string.minute_01);
							reinforcements.setRotation(rotation);
							break;
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
						notificationIntent.removeExtra("intCurrentMinutes");
						notificationIntent.removeExtra("intCurrentSeconds");
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
										notificationIntent,
										PendingIntent.FLAG_UPDATE_CURRENT);

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
				intCurrentSeconds = 0;
				intCurrentMinutes = 1;

				TextView countdown = (TextView) findViewById(R.id.txtCountdown);
				countdown.setText(R.string.finished);
				countdown.setTextColor(Color.BLACK);
				
				TextView reinforcements = (TextView) findViewById(R.id.txtReinforcements);
				reinforcements.setRotation(0);
				reinforcements.setText(R.string.goAgain);
				
				((RelativeLayout) findViewById(R.id.rlMainActivity)).setEnabled(true);

				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						MainActivity.this).setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle("Finished")
						.setContentText(createOutput(0, 0));

				Intent notificationIntent = new Intent(MainActivity.this,
						MainActivity.class);
				notificationIntent.removeExtra("intCurrentMinutes");
				notificationIntent.removeExtra("intCurrentSeconds");
				notificationIntent.putExtra("intCurrentMinutes", 1);
				notificationIntent.putExtra("intCurrentSeconds", 0);
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_SINGLE_TOP);

				TaskStackBuilder stackBuilder = TaskStackBuilder
						.create(MainActivity.this);
				stackBuilder.addParentStack(MainActivity.class);
				stackBuilder.addNextIntent(notificationIntent);

				PendingIntent resultPendingIntent = PendingIntent.getActivity(
						MainActivity.this, 0, notificationIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);

				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

				mNotificationManager.notify(notifyID, mBuilder.build());
			}
		}.start();

	}

	private String createOutput(int minutes, int seconds) {
		DecimalFormat nft = new DecimalFormat("#00.###");
		return nft.format(minutes) + ":" + nft.format(seconds);
	}

}

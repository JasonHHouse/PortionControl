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
 * Added static int's for default times
 * Added random colors to fonts
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
	private static final int MAX_ITERATIONS = 510;
	private static final int START_MINUTES = 20;
	private static final int START_SECONDS = 0;
	private static final int SECONDS = 59;

	private int notifyID = 1;
	private int intCurrentMinutes;
	private int intCurrentSeconds;
	private double red;
	private double green;
	private double blue;
	private double deltaValue;
	private int rotation;
	private int color;
	private int size;
	private CountDownTimer cdt;
	private Random rand;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		intCurrentMinutes = START_MINUTES;
		intCurrentSeconds = START_SECONDS;
		rand = new Random();
		rotation = rand.nextInt(20) - 10;
		color = rand.nextInt(10);
		size = rand.nextInt(20) + 40;
		deltaValue = (double) MAX_ITERATIONS
				/ (intCurrentMinutes * 60 + intCurrentSeconds);
		Log.i(TAG, "deltaValue: " + deltaValue);

		red = 255;
		green = 0;
		blue = 0;

		Intent intent = getIntent();
		if (intent.getExtras() != null) {
			intCurrentMinutes = intent.getExtras().getInt("intCurrentMinutes");
			intCurrentSeconds = intent.getExtras().getInt("intCurrentSeconds");
			red = intent.getExtras().getDouble("red");
			green = intent.getExtras().getDouble("green");
			blue = intent.getExtras().getDouble("blue");
			Log.i(TAG, "intCurrentMinutes: " + intCurrentMinutes);
			Log.i(TAG, "intCurrentSeconds: " + intCurrentSeconds);
			Log.i(TAG, "red: " + red);
			Log.i(TAG, "green: " + green);
			Log.i(TAG, "blue: " + blue);
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

				if (intCurrentSeconds == 59) { 
					rotation = rand.nextInt(20) - 10;
					color = rand.nextInt(10);
					size = rand.nextInt(20) + 40;
				}

				if(0 < 255 - (green + deltaValue)) {
					green += deltaValue;
				} else {
					green = 255;
					red -= deltaValue;
				}
				
				Log.i(TAG, "Time: " + createOutput(intCurrentMinutes,
								intCurrentSeconds) + ", RGB: " + red + ", " + green + ", " + blue);
				

				runOnUiThread(new Runnable() {
					public void run() {
						// Update the clock
						TextView countdown = (TextView) findViewById(R.id.txtCountdown);
						countdown.setText(createOutput(intCurrentMinutes,
								intCurrentSeconds));
						countdown.setTextColor(Color.rgb((int) red,
								(int) green, (int) blue));

						// Update the reinforcement text
						TextView reinforcements = (TextView) findViewById(R.id.txtReinforcements);
						setNotificationText(reinforcements);
						reinforcements.setRotation(rotation);
						reinforcements.setTextColor(getColor(color));
						reinforcements.setTextSize(size);

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
						notificationIntent.removeExtra("red");
						notificationIntent.removeExtra("green");
						notificationIntent.removeExtra("blue");
						notificationIntent.putExtra("intCurrentMinutes",
								intCurrentMinutes);
						notificationIntent.putExtra("intCurrentSeconds",
								intCurrentSeconds);
						notificationIntent.putExtra("red", red);
						notificationIntent.putExtra("green", green);
						notificationIntent.putExtra("blue", blue);
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
				//Reset the values
				intCurrentMinutes = START_MINUTES;
				intCurrentSeconds = START_SECONDS;

				red = 255;
				green = 0;
				blue = 0;
				
				TextView countdown = (TextView) findViewById(R.id.txtCountdown);
				countdown.setText(R.string.finished);
				countdown.setTextColor(Color.BLACK);

				TextView reinforcements = (TextView) findViewById(R.id.txtReinforcements);
				reinforcements.setRotation(0);
				reinforcements.setTextColor(Color.BLACK);
				reinforcements.setText(R.string.goAgain);

				((RelativeLayout) findViewById(R.id.rlMainActivity))
						.setEnabled(true);

				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						MainActivity.this).setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle(getString(R.string.finished))
						.setContentText(createOutput(0, 0));

				Intent notificationIntent = new Intent(MainActivity.this,
						MainActivity.class);
				notificationIntent.removeExtra("intCurrentMinutes");
				notificationIntent.removeExtra("intCurrentSeconds");
				notificationIntent.removeExtra("red");
				notificationIntent.removeExtra("green");
				notificationIntent.removeExtra("blue");
				// notificationIntent.putExtra("intCurrentMinutes",
				// START_MINUTES);
				// notificationIntent.putExtra("intCurrentSeconds",
				// START_SECONDS);
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

	private void setNotificationText(TextView reinforcements) {
		switch (intCurrentMinutes) {
		case 20:
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
		}

	}

	private String createOutput(int minutes, int seconds) {
		DecimalFormat nft = new DecimalFormat("#00.###");
		return nft.format(minutes) + ":" + nft.format(seconds);
	}

	private int getColor(int rand) {
		switch (rand) {
		case 0:
			return Color.BLACK;
		case 1:
			return Color.BLUE;
		case 2:
			return Color.CYAN;
		case 3:
			return Color.DKGRAY;
		case 4:
			return Color.GRAY;
		case 5:
			return Color.GREEN;
		case 6:
			return Color.LTGRAY;
		case 7:
			return Color.MAGENTA;
		case 9:
			return Color.RED;
		default:
			// Orange
			return Color.rgb(255, 127, 0);
		}
	}
}

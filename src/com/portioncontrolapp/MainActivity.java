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

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private static final int START_MINUTES = 0;
	private static final int START_SECONDS = 10;

	private NotificationManager mNotificationManager;
	private ResponseReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		receiver = new ResponseReceiver();
		registerReceiver(receiver, filter);

		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		Log.d(TAG, "onSaveInstanceState()");
		// savedInstanceState.putDouble("red", red);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.d(TAG, "onRestoreInstanceState()");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onClick_finishedEating(View view) {
		Log.d(TAG, "onClick_finishedEating()");

		Intent msgIntent = new Intent(this, TimerService.class);
		msgIntent.putExtra(TimerService.INT_CURRENT_MINUTES, START_MINUTES);
		msgIntent.putExtra(TimerService.INT_CURRENT_SECONDS, START_SECONDS);
		startService(msgIntent);

	}

	public class ResponseReceiver extends BroadcastReceiver {
		public static final String ACTION_RESP = "com.mamlambo.intent.action.MESSAGE_PROCESSED";

		private double red;
		private double green;
		private double blue;
		private int rotation;
		private int color;
		private int size;
		private int notifyID = 1;
		private int intCurrentMinutes;
		private int intCurrentSeconds;

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive()");
			intCurrentMinutes = intent.getIntExtra(TimerService.INT_CURRENT_MINUTES, 0);
			intCurrentSeconds = intent.getIntExtra(TimerService.INT_CURRENT_SECONDS, 0);
			red = intent.getDoubleExtra(TimerService.RED, 0);
			green = intent.getDoubleExtra(TimerService.GREEN, 0);
			blue = intent.getDoubleExtra(TimerService.BLUE, 0);
			rotation = intent.getIntExtra(TimerService.ROTATION, 0);
			color = intent.getIntExtra(TimerService.COLOR, 0);
			size = intent.getIntExtra(TimerService.SIZE, 0);

			if (intCurrentMinutes == 0 && intCurrentSeconds == 0) {
				onFinish();
			} else {
				onUpdate();
			}

			// Log.i(TAG,
			// "Time: "
			// + createOutput(intCurrentMinutes, intCurrentSeconds)
			// + ", RGB: " + red + ", " + green + ", " + blue);

		}

		private void onUpdate() {
			// Update the clock
			TextView countdown = (TextView) findViewById(R.id.txtCountdown);
			countdown.setText(createOutput(intCurrentMinutes, intCurrentSeconds));
			countdown.setTextColor(Color.rgb((int) red, (int) green, (int) blue));

			// Update the reinforcement text
			TextView reinforcements = (TextView) findViewById(R.id.txtReinforcements);
			setNotificationText(reinforcements);
			reinforcements.setRotation(rotation);
			reinforcements.setTextColor(getColor(color));
			reinforcements.setTextSize(size);

			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this)
					.setSmallIcon(R.drawable.ic_launcher).setContentTitle("Time left")
					.setContentText(createOutput(intCurrentMinutes, intCurrentSeconds)).setOngoing(true);

			Intent notificationIntent = getNotificationIntent(true);

			TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
			stackBuilder.addParentStack(MainActivity.class);
			stackBuilder.addNextIntent(notificationIntent);

			PendingIntent resultPendingIntent = PendingIntent.getActivity(MainActivity.this, 0, notificationIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			mBuilder.setContentIntent(resultPendingIntent);

			mNotificationManager.notify(notifyID, mBuilder.build());
		}

		private void onFinish() {
			// Reset the values
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

			((RelativeLayout) findViewById(R.id.rlMainActivity)).setEnabled(true);

			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this)
					.setSmallIcon(R.drawable.ic_launcher).setContentTitle(getString(R.string.finished))
					.setContentText(createOutput(0, 0));

			Intent notificationIntent = getNotificationIntent(false);

			TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
			stackBuilder.addParentStack(MainActivity.class);
			stackBuilder.addNextIntent(notificationIntent);

			PendingIntent resultPendingIntent = PendingIntent.getActivity(MainActivity.this, 0, notificationIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			mBuilder.setContentIntent(resultPendingIntent);

			mNotificationManager.notify(notifyID, mBuilder.build());

			// Get instance of Vibrator from current Context
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(new long[] { 100, 200, 100, 500, 100, 200, 100, 500, 100, 200, 100, 500 }, -1);

			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
			r.play();
		}

		private Intent getNotificationIntent(boolean addValues) {
			Intent notificationIntent = new Intent(MainActivity.this, MainActivity.class);
			notificationIntent.removeExtra("intCurrentMinutes");
			notificationIntent.removeExtra("intCurrentSeconds");
			notificationIntent.removeExtra("red");
			notificationIntent.removeExtra("green");
			notificationIntent.removeExtra("blue");
			notificationIntent.removeExtra("isRunning");
			if (addValues) {
				notificationIntent.putExtra("intCurrentMinutes", intCurrentMinutes);
				notificationIntent.putExtra("intCurrentSeconds", intCurrentSeconds);
				notificationIntent.putExtra("red", red);
				notificationIntent.putExtra("green", green);
				notificationIntent.putExtra("blue", blue);
			}
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

			return notificationIntent;
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
	}

	public static String createOutput(int minutes, int seconds) {
		DecimalFormat nft = new DecimalFormat("#00.###");
		return nft.format(minutes) + ":" + nft.format(seconds);
	}

}

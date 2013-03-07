package com.portioncontrolapp;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

	private int intCurrentMinutes = 20;
	private int intCurrentSeconds = 0;
	private CountdownClock cc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		cc = new CountdownClock();

		Intent intent = getIntent();
		if (intent.getExtras() != null) {
			intCurrentMinutes = intent.getExtras().getInt("intCurrentMinutes");
			intCurrentSeconds = intent.getExtras().getInt("intCurrentSeconds");
			Log.i(TAG, "intCurrentMinutes: " + intCurrentMinutes);
			Log.i(TAG, "intCurrentSeconds: " + intCurrentSeconds);

			cc.cancel(true);
			cc.execute();
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
		cc.execute();
	}

	private class CountdownClock extends AsyncTask<Void, Void, String> {
		Button countdown = (Button) findViewById(R.id.butFinishedEating);
		int notifyID = 1;

		@Override
		protected void onPostExecute(String result) {
			runOnUiThread(new Runnable() {
				public void run() {
					countdown.setEnabled(true);
				}
			});
		}

		@Override
		protected void onPreExecute() {
			runOnUiThread(new Runnable() {
				public void run() {
					countdown.setEnabled(false);
				}
			});
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}

		@Override
		protected String doInBackground(Void... params) {
			Log.d(TAG, "startCountdown()");
			intCurrentMinutes = 20;
			intCurrentSeconds = 0;

			while (intCurrentMinutes >= 0 && intCurrentSeconds >= 0) {
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

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		private String createOutput(int minutes, int seconds) {
			DecimalFormat nft = new DecimalFormat("#00.###");
			return nft.format(minutes) + ":" + nft.format(seconds);
		}
	}
}

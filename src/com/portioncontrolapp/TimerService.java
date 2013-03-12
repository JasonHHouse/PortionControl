package com.portioncontrolapp;

import java.util.Random;

import com.portioncontrolapp.MainActivity.ResponseReceiver;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class TimerService extends IntentService {

	public static final String TAG = "TimerService";
	public static final String INT_CURRENT_MINUTES = "INT_CURRENT_MINUTES";
	public static final String INT_CURRENT_SECONDS = "INT_CURRENT_SECONDS";
	public static final String RED = "RED";
	public static final String GREEN = "GREEN";
	public static final String BLUE = "BLUE";
	public static final String ROTATION = "ROTATION";
	public static final String COLOR = "COLOR";
	public static final String SIZE = "SIZE";

	private static final int SECONDS = 59;
	private static final int MAX_ITERATIONS = 510;

	private int intCurrentMinutes;
	private int intCurrentSeconds;
	private double red;
	private double green;
	private double blue;
	private int rotation;
	private int color;
	private int size;
	private double deltaValue;
	private Random rand;

	public TimerService(String name) {
		super(name);
		Log.d(TAG, "TimerService(" + name + ")");
		defaultVariables();
	}

	public TimerService() {
		super("TimerService");
		Log.d(TAG, "TimerService()");
		defaultVariables();
	}

	private void defaultVariables() {
		rand = new Random();
		red = 255;
		green = 0;
		blue = 0;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent()");
		intCurrentMinutes = intent.getIntExtra(INT_CURRENT_MINUTES, 0);
		intCurrentSeconds = intent.getIntExtra(INT_CURRENT_SECONDS, 0);
		deltaValue = (double) MAX_ITERATIONS / (intCurrentMinutes * 60 + intCurrentSeconds);

		// 20 minutes
		while ((intCurrentMinutes * 60 + intCurrentSeconds) >= 0) {

			if (intCurrentSeconds == 59) {
				rotation = rand.nextInt(20) - 10;
				color = rand.nextInt(10);
				size = rand.nextInt(20) + 40;
			}

			if (0 < 255 - (green + deltaValue)) {
				green += deltaValue;
			} else {
				green = 255;
				red -= deltaValue;
			}

			// processing done here….
			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction(ResponseReceiver.ACTION_RESP);
			broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			broadcastIntent.putExtra(INT_CURRENT_MINUTES, intCurrentMinutes);
			broadcastIntent.putExtra(INT_CURRENT_SECONDS, intCurrentSeconds);
			broadcastIntent.putExtra(RED, red);
			broadcastIntent.putExtra(GREEN, green);
			broadcastIntent.putExtra(BLUE, blue);
			broadcastIntent.putExtra(ROTATION, rotation);
			broadcastIntent.putExtra(COLOR, color);
			broadcastIntent.putExtra(SIZE, size);

			intCurrentSeconds--;
			if (intCurrentSeconds < 0) {
				intCurrentSeconds = SECONDS;
				intCurrentMinutes--;
			}

			sendBroadcast(broadcastIntent);
			android.os.SystemClock.sleep(1000);
		}

	}
}
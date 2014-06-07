package com.tymm.hexapod;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.hardware.Sensor;
import java.lang.Math;

class SensorInformation implements SensorEventListener {
	private Sensor mRotationVectorSensor;
	private final float[] mRotationMatrix = new float[16];
	private SensorManager mSensorManager;
	private float[] orientation = new float[3];
	private float[] prev_orientation = new float[3];
	private final int diff_degree = 5;

	public SensorInformation(SensorManager SensorManager) {
		mSensorManager = SensorManager;

		// find the rotation-vector sensor
		mRotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

		mRotationMatrix[ 0] = 1;
		mRotationMatrix[ 4] = 1;
		mRotationMatrix[ 8] = 1;
		mRotationMatrix[12] = 1;
	}

	public void start() {
		// enable our sensor when the activity is resumed, ask for
		// 10 ms updates.
		mSensorManager.registerListener(this, mRotationVectorSensor, 10000);
	}

	public void stop() {
		// make sure to turn our sensor off when the activity is paused
		mSensorManager.unregisterListener(this);
	}

	public void onSensorChanged(SensorEvent event) {
		// we received a sensor event. it is a good practice to check
		// that we received the proper event
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
			SensorManager.getOrientation(mRotationMatrix, orientation);

			if (isDiffBigEnoughChange(orientation)) {
				Log.i("Hexapod", "Z: " + Math.toDegrees(orientation[0]) + " X: " + Math.toDegrees(orientation[1]) + " Y: " + Math.toDegrees(orientation[2]));
				// Sending sensor information here
			}
		}
	}

	public boolean isDiffBigEnoughChange(float[] orientation) {
		double Z_prev = Math.toDegrees(prev_orientation[0]);
		double X_prev = Math.toDegrees(prev_orientation[1]);
		double Y_prev = Math.toDegrees(prev_orientation[2]);

		double Z = Math.toDegrees(orientation[0]);
		double X = Math.toDegrees(orientation[1]);
		double Y = Math.toDegrees(orientation[2]);

		if (Math.abs(Z-Z_prev) >= diff_degree || Math.abs(X-X_prev) >= diff_degree || Math.abs(Y-Y_prev) >= diff_degree) {
			// Copy by value
			System.arraycopy(orientation, 0, this.prev_orientation, 0, orientation.length);
			return true;
		} else {
			return false;
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
}
package edu.ucla.m117;



import static android.provider.BaseColumns._ID;

import java.util.Calendar;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class ReadingsActivity extends Activity implements SensorEventListener{
  EventDataSQLHelper eventsData;
  TextView output;
  private SensorManager sensorManager;
  String sensorReading;
  int count =0;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    TelephonyManager manager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
    String imei = manager.getDeviceId();
    // for location
    LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
  
    LocationListener mlocListener = new MyLocationListener(getApplicationContext(),imei);
    mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 100000000, 0, mlocListener);

    
    
    //for accelerometer
    sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
    sensorManager.registerListener(this,
			sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
			100000000);

    output = (TextView) findViewById(R.id.output);
    eventsData = new EventDataSQLHelper(this);
    Cursor cursor = getEvents();
   // showEvents(cursor);
   // addGPSData("location");
    Cursor cursor2= getGPS();
    //showGPS(cursor2);
    showEverything(cursor,cursor2);
    
  }
  public void onResume(){
		super.onResume();
		Log.v("aditya", "inside on resume");
		EventDataSQLHelper mSQLDataHelper = new EventDataSQLHelper(this);
		SQLiteDatabase db = mSQLDataHelper.getReadableDatabase();
		SQLiteDatabase dbWrite = mSQLDataHelper.getWritableDatabase();
		Cursor curRead = db.query(mSQLDataHelper.TABLE,new String[]{"user_id","time"},null,null,null,null,null);
		Log.v("count","count is"+curRead.getCount()+"");
		if(curRead.getCount() > 120){
			curRead.moveToFirst();
			dbWrite.delete(mSQLDataHelper.TABLE, null, null);
			Log.v("resume", "Deleting Records");
		}
		curRead.close();
		
		// for location 
		curRead = db.query(mSQLDataHelper.TABLE2,new String[]{"user_id","time"},null,null,null,null,null);
		Log.v("count","count for "+mSQLDataHelper.TABLE2+" is "+curRead.getCount()+"");
		if(curRead.getCount() > 120){
			curRead.moveToFirst();
			dbWrite.delete(mSQLDataHelper.TABLE2, null, null);
			Log.v("resume", "Deleting Records");
		}
		curRead.close();
		
		
		
		db.close();
		dbWrite.close();
  }
  @Override
  public void onDestroy() {
	super.onDestroy();
    eventsData.close();
  }

   private void addEvent(String readings) {
    SQLiteDatabase db = eventsData.getWritableDatabase();
    TelephonyManager manager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
    String imei = manager.getDeviceId();
    ContentValues values = new ContentValues();
    Calendar calendar = Calendar.getInstance();

    values.put(EventDataSQLHelper.USERID, imei);
    values.put(EventDataSQLHelper.TIME, DateFormat.format("yyyy-MM-dd kk:mm:ss", calendar.getTime()) + "");
    values.put(EventDataSQLHelper.ACCEL,readings);
    db.insert(EventDataSQLHelper.TABLE, null, values);
  //  Log.v("aditya",DateFormat.format("yyyy-MM-dd kk:mm:ss", calendar.getTime()) + "");

  }
   
   
   private  void addGPSData(String location)
   {
	   TelephonyManager manager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
	    String imei = manager.getDeviceId();
 	  	Log.v("aditya", "inside addGSPDATA "+location);
 	  	SQLiteDatabase db = eventsData.getWritableDatabase();
 	  	
 	    ContentValues values = new ContentValues();
 	    Calendar calendar = Calendar.getInstance();

 	    values.put(EventDataSQLHelper.USERID, imei);
 	    values.put(EventDataSQLHelper.TIME, DateFormat.format("yyyy-MM-dd kk:mm:ss", calendar.getTime()) + "");
 	    values.put(EventDataSQLHelper.GPS,location);
 	    db.insert(EventDataSQLHelper.TABLE2, null, values);
 	    Log.v("aditya","data inserted");
 	  
   }


  private void call()
  {
	    addEvent(sensorReading); 
	  
  }
  private Cursor getEvents() {
    SQLiteDatabase db = eventsData.getReadableDatabase();
    Cursor cursor = db.query(EventDataSQLHelper.TABLE, null, null, null, null,
        null, null);
    
    startManagingCursor(cursor);
    return cursor;
  }
 
  private Cursor getGPS() {
	    SQLiteDatabase db = eventsData.getReadableDatabase();
	    Cursor cursor2 = db.query(EventDataSQLHelper.TABLE2, null, null, null, null,
	        null, null);
	    
	    startManagingCursor(cursor2);
	    return cursor2;
	  }

  
  
  private void showEvents(Cursor cursor) {
    StringBuilder ret = new StringBuilder("Saved Accel:\n\n");
    Log.v("aditya","count is "+cursor.getCount()+"");
    while (cursor.moveToNext()) {
    	Log.v("aditya","inside while of events");
      String user_id = cursor.getString(0);
      String time = cursor.getString(1);
      String accel = cursor.getString(2);
      ret.append(user_id + ": " + time + ": " + accel + "\n");
    }
    Log.v("aditya","now outside while");
    output.setText(ret);
  }
  
  
  
  
  
  private void showGPS(Cursor cursor) {
	    StringBuilder ret = new StringBuilder("Saved GPS Data:\n\n");
	   Log.v("aditya","count is "+cursor.getCount()+"");
	    while (cursor.moveToNext()) {
		    Log.v("aditya","inside while");
	      String user_id = cursor.getString(0);
	      String time = cursor.getString(1);
	      String gps = cursor.getString(2);
		    Log.v("aditya",user_id+time+gps);
	      ret.append(user_id + ": " + time + ": " + "\n"+ gps + "\n");
	    }
	    Log.v("aditya","outside GPS");
	    output.setText(ret);
	  }

  
  
  // show everything 
  private void showEverything(Cursor cursor, Cursor cursor2) {
	    StringBuilder ret = new StringBuilder("Saved Accel: \n\n");
	    Log.v("aditya","count is "+cursor.getCount()+"");
	    while (cursor.moveToNext()) {
	    	Log.v("aditya","inside while of events");
	      String user_id = cursor.getString(0);
	      String time = cursor.getString(1);
	      String accel = cursor.getString(2);
	      ret.append(user_id + ": " + time + ": " + accel + "\n");
	    }
	    ret.append("\n\n");
	    ret.append("Saved GPS readings"+"\n");
	    while (cursor2.moveToNext()) {
		    Log.v("aditya","inside while evrythinf");
	      String user_id = cursor2.getString(0);
	      String time = cursor2.getString(1);
	      String gps = cursor2.getString(2);
		    Log.v("aditya",user_id+time+gps);
	      ret.append(user_id + ": " + time + ": " + "\n"+ gps + "\n");
	    }
	  
	    output.setText(ret);
	    
	  }
	  
@Override
public void onAccuracyChanged(Sensor arg0, int arg1) {
	// TODO Auto-generated method stub
	
}

@Override
public void onSensorChanged(SensorEvent event) {
	
	if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
		
		float x=event.values[0];
		float y=event.values[1];
		float z=event.values[2];
		
		sensorReading = x+","+y+","+z;
		call();
		
		
	}
	
	
}



}